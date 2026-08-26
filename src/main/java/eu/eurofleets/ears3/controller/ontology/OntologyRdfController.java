package eu.eurofleets.ears3.controller.ontology;

import eu.eurofleets.ears3.domain.Message;
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
 * <p>
 * PORTED, faithfully:
 * - SPARQL query execution (/sparql) - same Jena Query/QueryExecution/
 * ResultSetFormatter.outputAsJSON pipeline as vesselSparqlEndpoint()'s simple
 * (non-"program"-combining) path.
 * - HTTP Basic Auth gate on publish operations (/stage, /activate), reusing the
 * same ears.ontology.username / ears.ontology.password properties.
 * - Scope validation on /stage: rejects a file whose header scope is present and
 * not VESSEL, exactly matching the old uploadOntology()'s leniency (a file with
 * no scope annotation at all is still allowed through).
 * - /authenticate diagnostic endpoint, for any existing client scripts that probe it.
 * <p>
 * REPLACED:
 * - The date lookup no longer calls the external IOntologyModel.getStaticStuff(),
 * which built a full in-memory RDF tree just to read two annotation values. See
 * OntologyHeaderReader - a streaming, early-exit reader that only ever parses
 * the small <owl:Ontology> header block.
 * <p>
 * DROPPED, per instruction: all "program" ontology endpoints (program/upload,
 * program, program/date, program/sparql) and the vessel+program model-combining
 * logic in the old vesselSparqlEndpoint(). Program ontologies are no longer kept
 * as separate trees - there is now only ever the one (vessel) ontology.
 * <p>
 * /stage and /activate are additionally gated by OntologyEditingGuard.
 * assertImportAllowed() (the instance-wide kill switch discussed earlier) - Basic
 * Auth is a per-request identity check on top of that, not a replacement for it.
 */
@RestController
@RequestMapping("/api/ontology")
public class OntologyRdfController {

    private final StagedRdfService rdfService;
    private final OntologyEditingGuard guard;
    private final OntologySparqlService sparqlService;
    private final OntologyRdfImportService importService;

    /**
     * POC-stage opt-in: if true AND the instance is in ACTIVE mode, activating a
     * file also repopulates the local ontology database from it, purely so the
     * tree-browsing UI has something to show. Defaults to false. Never applies on
     * a PASSIVE ship instance regardless of this flag - see assertion in activate().
     */
    @Value("${app.ontology.rdf.populate-db-on-activate:false}")
    private boolean populateDbOnActivate;

    public OntologyRdfController(StagedRdfService rdfService, OntologyEditingGuard guard,
                                 OntologySparqlService sparqlService,
                                 OntologyRdfImportService importService) {
        this.rdfService = rdfService;
        this.guard = guard;
        this.sparqlService = sparqlService;
        this.importService = importService;
    }

    /**
     * Serves/downloads the currently active RDF file. This file is used by the SPARQL engine as well.
     */
    @GetMapping(value = {"/staged", ""})
    public ResponseEntity<InputStreamResource> getStaged() throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + rdfService.stagedFilename() + "\"")
                .contentType(MediaType.parseMediaType("application/rdf+xml"))
                .body(new InputStreamResource(rdfService.openStaged()));
    }

    @GetMapping(value = {"/staged/info", "/info"})
    public FileInfo liveInfo() {
        return rdfService.stagedInfo();
    }

    @GetMapping(value = {"/staged/date", "date"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> liveDate() throws IOException {
        OntologyHeaderReader.OntologyHeader header = rdfService.stagedHeader();
        String best = header.bestDate();
        if (best == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The live ontology has neither a dc:modified nor an owl:versionInfo annotation.");
        }
        return ResponseEntity.ok(best);
    }

    /**
     * Serves/downloads the RDF straight from the database
     */
    @GetMapping(value = "/live")
    public ResponseEntity<InputStreamResource> getLive() throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + rdfService.stagedFilename() + "\"")
                .contentType(MediaType.parseMediaType("application/rdf+xml"))
                .body(new InputStreamResource(rdfService.openStaged()));
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
    @PostMapping("/stage-from-file")
    public ResponseEntity<Message> stageRDFFromFile(@RequestParam("file") MultipartFile file) throws IOException {
        guard.assertImportAllowed();
        //basicAuth.assertAuthorized(authorization);

        byte[] bytes = file.getBytes();
        OntologyHeaderReader.OntologyHeader header;
        try {
            header = OntologyHeaderReader.read(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot read the uploaded file as an ontology RDF/XML file.", e);
        }

        String scope = header.scope();
        if (scope != null && !scope.equals("VESSEL")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot stage file: not recognized as a Vessel ontology "
                            + "(found scope=" + scope + "). Program ontologies are no longer supported.");
        }
        rdfService.archiveStaged();
        rdfService.stage(new ByteArrayInputStream(bytes));
        Message m = new Message(202, null, null, "File correctly saved", null);
        return new ResponseEntity<>(m, HttpStatus.ACCEPTED);
    }

    @PostMapping("/stage-from-db")
    public ResponseEntity<Message> stageRDFFromDB() throws IOException {

        guard.assertImportAllowed();
        //basicAuth.assertAuthorized(authorization);

        //byte[] rdfBytes = buildRdfFromDatabase();
        //rdfService.stageFromBytes(rdfBytes);

        rdfService.archiveStaged();

        Message m = new Message(500, null, null, "Not implemented", null);
        return new ResponseEntity<>(m, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/ingest")
    public ResponseEntity<Message> populateDBFromRDF(@RequestParam("file") MultipartFile file)
            throws IOException {

        guard.assertImportAllowed();
        //basicAuth.assertAuthorized(authorization);

        byte[] bytes = file.getBytes();
        OntologyHeaderReader.OntologyHeader header;
        try {
            header = OntologyHeaderReader.read(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot read the uploaded file as an ontology RDF/XML file.", e);
        }

        String scope = header.scope();
        if (scope != null && !scope.equals("VESSEL")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot stage file: not recognized as a Vessel ontology "
                            + "(found scope=" + scope + "). Program ontologies are no longer supported.");
        }
        rdfService.archiveStaged();
        rdfService.tmp(new ByteArrayInputStream(bytes));

        try (var in = rdfService.openTmp()) {
            importService.importFullReplacement(in);
        }
        Message m = new Message(202, null, null, "File correctly saved", null);
        return new ResponseEntity<>(m, HttpStatus.ACCEPTED);
    }

    // ------------------------------------------------------------------
    // SPARQL - faithful recreation of legacy GET /ontology/vessel/sparql's
    // simple-query path (no "program" combine - see class javadoc).
    // ------------------------------------------------------------------

    @GetMapping(value = "/sparql", produces = MediaType.APPLICATION_JSON_VALUE)
    public String sparql(@RequestParam(required = true, value = "q") String sparqlQuery) throws IOException {
        return sparqlService.executeSelectAsJson(sparqlQuery);
    }

    @GetMapping(value = "/sparql/bindings", produces = MediaType.APPLICATION_JSON_VALUE)
    public String sparqlBindings() throws IOException {
        return sparqlService.executeBindings();
    }

}
