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
}
