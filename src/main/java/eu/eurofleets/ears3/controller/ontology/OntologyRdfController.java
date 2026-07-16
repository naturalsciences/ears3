package eu.eurofleets.ears3.controller.ontology;

import eu.eurofleets.ears3.rdf.OntologyHeaderReader;
import eu.eurofleets.ears3.rdf.OntologySparqlService;
import eu.eurofleets.ears3.rdf.StagedRdfService;
import eu.eurofleets.ears3.rdf.StagedRdfService.FileInfo;
import eu.eurofleets.ears3.service.ontology.OntologyRdfImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Stage/activate workflow for the distributable ontology RDF file, plus the
 * download / date / SPARQL functionality ported from the legacy
 * eu.eurofleets.ears3.controller.rest.OntologyController.
 *
 * PORTED, faithfully:
 *  - SPARQL query execution (/sparql) - same Jena Query/QueryExecution/
 *    ResultSetFormatter.outputAsJSON pipeline as vesselSparqlEndpoint()'s simple
 *    (non-"program"-combining) path.
 *  - HTTP Basic Auth gate on publish operations (/stage, /activate), reusing the
 *    same ears.ontology.username / ears.ontology.password properties.
 *  - Scope validation on /stage: rejects a file whose header scope is present and
 *    not VESSEL, exactly matching the old uploadOntology()'s leniency (a file with
 *    no scope annotation at all is still allowed through).
 *  - /authenticate diagnostic endpoint, for any existing client scripts that probe it.
 *
 * REPLACED:
 *  - The date lookup no longer calls the external IOntologyModel.getStaticStuff(),
 *    which built a full in-memory RDF tree just to read two annotation values. See
 *    OntologyHeaderReader - a streaming, early-exit reader that only ever parses
 *    the small <owl:Ontology> header block.
 *
 * DROPPED, per instruction: all "program" ontology endpoints (program/upload,
 * program, program/date, program/sparql) and the vessel+program model-combining
 * logic in the old vesselSparqlEndpoint(). Program ontologies are no longer kept
 * as separate trees - there is now only ever the one (vessel) ontology.
 *
 * /stage and /activate are additionally gated by OntologyEditingGuard.
 * assertImportAllowed() (the instance-wide kill switch discussed earlier) - Basic
 * Auth is a per-request identity check on top of that, not a replacement for it.
 */
@RestController
@RequestMapping("/api/ontology/rdf")
public class OntologyRdfController {

    private final StagedRdfService rdfService;
    private final OntologyEditingGuard guard;
    private final OntologyBasicAuthService basicAuth;
    private final OntologySparqlService sparqlService;
    private final OntologyRdfImportService importService;

    /**
     * POC-stage opt-in: if true AND the instance is in ACTIVE mode, activating a
     * file also repopulates the local ontology database from it, purely so the
     * tree-browsing UI has something to show. Defaults to false. Never applies on
     * a PASSIVE ship instance regardless of this flag - see assertion in activate().
     */
    @Value("${ears.ontology.rdf.populate-db-on-activate:false}")
    private boolean populateDbOnActivate;

    public OntologyRdfController(StagedRdfService rdfService, OntologyEditingGuard guard,
                                  OntologyBasicAuthService basicAuth, OntologySparqlService sparqlService,
                                  OntologyRdfImportService importService) {
        this.rdfService = rdfService;
        this.guard = guard;
        this.basicAuth = basicAuth;
        this.sparqlService = sparqlService;
        this.importService = importService;
    }

    // ------------------------------------------------------------------
    // Download - equivalent of legacy GET /ontology/vessel
    // ------------------------------------------------------------------

    /** Serves the currently active RDF file - point your SPARQL engine at this same file on disk. */
    @GetMapping(value = "/live", produces = "application/rdf+xml")
    public ResponseEntity<InputStreamResource> getLive() throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + rdfService.liveFilename() + "\"")
                .contentType(MediaType.parseMediaType("application/rdf+xml"))
                .body(new InputStreamResource(rdfService.openLive()));
    }

    @GetMapping("/live/info")
    public FileInfo liveInfo() {
        return rdfService.liveInfo();
    }

    @GetMapping("/staged/info")
    public FileInfo stagedInfo() {
        return rdfService.stagedInfo();
    }

    @GetMapping(value = "/live/date", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> liveDate() throws IOException {
        OntologyHeaderReader.OntologyHeader header = rdfService.liveHeader();
        String best = header.bestDate();
        if (best == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The live ontology has neither a dc:modified nor an owl:versionInfo annotation.");
        }
        return ResponseEntity.ok(best);
    }

    // ------------------------------------------------------------------
    // Upload / publish
    // ------------------------------------------------------------------

    /**
     * Receiving a file (upload, or an emailed attachment saved locally) - available
     * on both master and ship, same as before. Now additionally requires Basic Auth
     * and validates the file is actually VESSEL-scoped before it's even staged,
     * porting the checks that used to live in uploadOntology()/uploadVesselOntology().
     */
    @PostMapping("/stage")
    public void stage(@RequestHeader(value = "Authorization", required = false) String authorization,
                       @RequestParam("file") MultipartFile file) throws IOException {
        guard.assertImportAllowed();
        basicAuth.assertAuthorized(authorization);

        byte[] bytes = file.getBytes();
        OntologyHeaderReader.OntologyHeader header;
        try {
            header = OntologyHeaderReader.read(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot read the uploaded file as an ontology RDF/XML file.", e);
        }

        String scope = header.scope();
        if (scope != null && !scope.equals("Vessel")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot stage file: not recognized as a Vessel ontology "
                    + "(found scope=" + scope + "). Program ontologies are no longer supported.");
        }

        rdfService.stage(new ByteArrayInputStream(bytes));
    }

    /** Master-only: build straight from the database into staging. Still requires a separate activate() call. */
    @PostMapping("/generate")
    public void generate() throws IOException {
        guard.assertEditingAllowed();
        byte[] rdfBytes = buildRdfFromDatabase();
        rdfService.stageFromBytes(rdfBytes);
    }

    /**
     * The conscious "publish"/"activate" moment - same endpoint, same behavior, on
     * either side of the fleet. Requires Basic Auth, same as staging.
     */
    @PostMapping("/activate")
    public void activate(@RequestHeader(value = "Authorization", required = false) String authorization)
            throws IOException {
        guard.assertImportAllowed();
        basicAuth.assertAuthorized(authorization);
        rdfService.activate();

        if (populateDbOnActivate && guard.getMode() == OntologyEditingGuard.Mode.ACTIVE) {
            try (var in = rdfService.openLive()) {
                importService.importFullReplacement(in);
            }
        }
        // On a PASSIVE instance the database is never touched here, regardless of the
        // populate-db-on-activate flag - the file itself remains the sole source of truth.
    }

    // ------------------------------------------------------------------
    // Auth diagnostic - faithful recreation of legacy GET /ontology/authenticate
    // ------------------------------------------------------------------

    @GetMapping(value = "/authenticate", produces = MediaType.TEXT_PLAIN_VALUE)
    public String canAuthenticate(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return Boolean.toString(basicAuth.isAuthorized(authorization));
    }

    // ------------------------------------------------------------------
    // SPARQL - faithful recreation of legacy GET /ontology/vessel/sparql's
    // simple-query path (no "program" combine - see class javadoc).
    // ------------------------------------------------------------------

    @GetMapping(value = "/sparql", produces = MediaType.APPLICATION_JSON_VALUE)
    public String sparql(@RequestParam(required = true, value = "q") String sparqlQuery) throws IOException {
        return sparqlService.executeSelectAsJson(sparqlQuery);
    }

    /**
     * Wraps EARSOntologyCreator (the standalone library) to build the RDF bytes from
     * the current ontology_* tables. Left as a stub: needs adapter classes (see
     * conversation notes) implementing IToolCategory/ITool/IProcess/IAction/
     * IProperty/IGenericEventDefinition/ISpecificEventDefinition around
     * ToolCategoryDefinition/ToolDefinition/etc, then a straightforward
     * creator.setToolCategoryCollection(...) / createOntoFile(...) call sequence.
     */
    private byte[] buildRdfFromDatabase() {
        throw new UnsupportedOperationException(
                "Wire this up to EARSOntologyCreator once the IToolCategory/ITool/... adapter "
                + "classes around the JPA entities are written - see project README.");
    }
}
