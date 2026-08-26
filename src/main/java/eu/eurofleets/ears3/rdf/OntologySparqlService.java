package eu.eurofleets.ears3.rdf;

import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Faithful recreation of the legacy OntologyController.vesselSparqlEndpoint()'s
 * simple-query path (no "program" ontology to combine with - those are being
 * retired, see OntologyRdfController).
 *
 * Uses the classic, stable org.apache.jena.ontology.OntModel / ModelFactory
 * .createOntologyModel() API, NOT org.apache.jena.ontapi.model.OntModel. The
 * legacy code imported the latter but assigned it a value returned by the
 * former - those are different types in current Jena versions and that combination
 * would not compile as written; this uses the API that ModelFactory actually
 * returns.
 */
@Service
public class OntologySparqlService {

    private static final String CONCEPT_HIERARCHY_SPARQL = """
        PREFIX owl: <http://www.w3.org/2002/07/owl#>
        PREFIX dc: <http://purl.org/dc/elements/1.1/>
        PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
        PREFIX ears2:<http://ontologies.ef-ears.eu/ears2/1#>
        PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>
        SELECT DISTINCT (replace(replace(str(?e),".+?(gev?_)","ears:gev::","i"),".+?(sev?_)","ears:sev::","i") as ?eid)  (str(?c) as ?cu) (str(?cc) as ?ctu) ?cl (str(?t)  as ?tu) (str(?tc) as ?ttu) ?tl (str(?p) as ?pu) ?pl (str(?a) as ?au) ?al
        WHERE {
          {
            OPTIONAL {
              ?c a ears2:ToolCategory.
              ?t a ears2:Tool.
              ?t ears2:isMemberOf ?c.
              ?e ears2:hasProcess ?p.
              ?e ears2:hasAction ?a.
              {?e ears2:withTool ?t.} UNION {?e ears2:withTool ?c.}

              ?c ears2:asConcept ?cc.
              ?cc skos:prefLabel ?cl .

              ?t ears2:asConcept ?tc.
              ?tc skos:prefLabel ?tl_ .

              OPTIONAL {
                ?t ears2:toolIdentifier ?ti .
                BIND(CONCAT(": ", ?ti) AS ?ti_) .
              }
              ?p ears2:asConcept ?pc.
              ?pc skos:prefLabel ?pl .
              ?a ears2:asConcept ?ac.
              ?ac skos:prefLabel ?al.
              ?ac ears2:status ?as .

              BIND(COALESCE(?ti_, "") AS ?ti__) .
              BIND(CONCAT(?tl_, ?ti__) AS ?tl) .
            }
          }
          FILTER (!REGEX( ?al, "^New Action" ) && str(?as) != 'Deprecated' )
        }
        ORDER BY DESC(?eid) ?pl ?al""";

    private final StagedRdfService rdfService;

    public OntologySparqlService(StagedRdfService rdfService) {
        this.rdfService = rdfService;
    }

    /**
     * Executes a SELECT query against the currently live vessel ontology and
     * returns the result set serialized as JSON, exactly matching the legacy
     * endpoint's output format (ResultSetFormatter.outputAsJSON).
     *
     * @param encodedSparqlQuery the SPARQL query string, URL-encoded exactly as
     *                           the legacy endpoint expected it (decoded here via
     *                           UriUtils.decode, same as before).
     */
    public String executeSelectAsJson(String encodedSparqlQuery) throws IOException {
        String sparqlQuery = UriUtils.decode(encodedSparqlQuery, StandardCharsets.UTF_8);
        Query qry = QueryFactory.create(sparqlQuery);

        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model.getDocumentManager().setProcessImports(false); // self-contained file assumption, see StagedRdfService

        try (InputStream in = rdfService.openStaged()) {
            model.read(in, null);
        }

        try (QueryExecution qe = QueryExecutionFactory.create(qry, model)) {
            ResultSet rs = qe.execSelect();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ResultSetFormatter.outputAsJSON(outputStream, rs);
            return outputStream.toString(StandardCharsets.UTF_8);
        }
    }

    public String executeBindings() throws IOException {
        return executeSelectAsJson(CONCEPT_HIERARCHY_SPARQL);
    }
}
