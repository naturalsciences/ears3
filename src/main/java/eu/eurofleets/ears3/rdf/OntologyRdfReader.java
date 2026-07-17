package eu.eurofleets.ears3.rdf;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reads an EARS ontology RDF file back into flat, uninterpreted RawConcept records,
 * mirroring the individual/asConcept shape that EARSOntologyCreator writes
 * (makeConceptAssertions/saveConcept): term metadata lives on a linked skos:Concept
 * individual reached via :asConcept, not on the entity individual itself.
 * <p>
 * ASSUMPTION (see conversation notes): the file being read is self-contained
 * (produced with EARSOntologyCreator.LoadOnto.PASTE, or equivalent), not relying on
 * a live owl:imports fetch at read time - which matches an offline-safe "carry the
 * file to the ship" workflow. setProcessImports(false) enforces this: imports are
 * never resolved over the network here.
 * <p>
 * This reader is currently only wired up as an OPTIONAL convenience (see
 * OntologyRdfImportService) for a master/ACTIVE instance that wants its local
 * database populated from an existing file. It is NOT used on a PASSIVE ship
 * instance, where the RDF file is staged/activated/served as-is and the database is
 * never touched - see StagedRdfService.
 */
public class OntologyRdfReader {

    private static final String EARS_NS = "http://ontologies.ef-ears.eu/ears2/1#";
    private static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
    private static final String DC = "http://purl.org/dc/elements/1.1/";
    private static final String OWL_VERSION_INFO = "http://www.w3.org/2002/07/owl#versionInfo";

    private final OntModel model;

    public OntologyRdfReader(InputStream rdfInput) {
        this.model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model.getDocumentManager().setProcessImports(false);
        model.read(rdfInput, null);
    }

    public Set<RawConcept> readToolCategories() {
        return readIndividualsOfClass("ToolCategory");
    }

    public Set<RawConcept> readTools() {
        return readIndividualsOfClass("Tool");
    }

    public Set<RawConcept> readProcesses() {
        return readIndividualsOfClass("Process");
    }

    public Set<RawConcept> readActions() {
        return readIndividualsOfClass("ProcessStep");
    }

    public Set<RawConcept> readProperties() {
        return readIndividualsOfClass("EventProperty");
    }

    public Set<RawConcept> readGenericEventDefinitions() {
        return readIndividualsOfClass("GenericEventDefinition");
    }

    public Set<RawConcept> readSpecificEventDefinitions() {
        return readIndividualsOfClass("SpecificEventDefinition");
    }

    private Set<RawConcept> readIndividualsOfClass(String className) {
        OntClass ontClass = model.getOntClass(EARS_NS + className);
        if (ontClass == null) {
            return Set.of();
        }
        Set<RawConcept> result = new HashSet<>();
        ExtendedIterator<? extends OntResource> it = ontClass.listInstances();
        while (it.hasNext()) {
            Individual ind = (Individual) it.next();
            result.add(readConcept(ind));
        }
        return result;
    }

    private RawConcept readConcept(Individual entityIndividual) {
        RawConcept rc = new RawConcept();
        rc.uri = entityIndividual.getURI();

        Statement asConceptStmt = entityIndividual.getProperty(model.getProperty(EARS_NS + "asConcept"));
        Resource conceptRes = asConceptStmt != null ? asConceptStmt.getObject().asResource() : entityIndividual;

        rc.identifier = literalOrNull(conceptRes, model.getProperty(DC + "identifier"));
        rc.creator = literalOrNull(conceptRes, model.getProperty(DC + "creator"));
        rc.status = literalOrNull(conceptRes, model.getProperty(EARS_NS + "status"));
        rc.versionInfo = literalOrNull(conceptRes, model.getProperty(OWL_VERSION_INFO));

        rc.prefLabel = langLiteralOrNull(conceptRes, model.getProperty(SKOS + "prefLabel"), "en");
        rc.altLabel = langLiteralOrNull(conceptRes, model.getProperty(SKOS + "altLabel"), "en");
        rc.definition = langLiteralOrNull(conceptRes, model.getProperty(SKOS + "definition"), "en");

        rc.toolIdentifier = literalOrNull(entityIndividual, model.getProperty(EARS_NS + "toolIdentifier"));
        rc.serialNumber = literalOrNull(entityIndividual, model.getProperty(EARS_NS + "serialNumber"));
        rc.mandatory = boolOrNull(entityIndividual, model.getProperty(EARS_NS + "mandatory"));
        rc.multiple = boolOrNull(entityIndividual, model.getProperty(EARS_NS + "multiple"));

        rc.isMemberOf = objectUris(entityIndividual, model.getProperty(EARS_NS + "isMemberOf"));
        rc.canHost = objectUris(entityIndividual, model.getProperty(EARS_NS + "canHost"));
        rc.hasProcess = objectUriOrNull(entityIndividual, model.getProperty(EARS_NS + "hasProcess"));
        rc.hasAction = objectUriOrNull(entityIndividual, model.getProperty(EARS_NS + "hasAction"));
        // withTool holds a Tool for SpecificEventDefinition, a ToolCategory for GenericEventDefinition
        rc.withTool = objectUriOrNull(entityIndividual, model.getProperty(EARS_NS + "withTool"));
        rc.hasProperty = objectUris(entityIndividual, model.getProperty(EARS_NS + "hasProperty"));
        rc.realizedBy = objectUris(entityIndividual, model.getProperty(EARS_NS + "realizedBy"));
        rc.triggersHostedEvent = objectUris(entityIndividual, model.getProperty(EARS_NS + "triggersHostedEvent"));

        return rc;
    }

    private String literalOrNull(Resource r, Property p) {
        Statement s = r.getProperty(p);
        return s != null ? s.getString() : null;
    }

    private String langLiteralOrNull(Resource r, Property p, String lang) {
        StmtIterator it = r.listProperties(p);
        while (it.hasNext()) {
            Statement s = it.next();
            if (lang.equals(s.getLanguage())) {
                return s.getString();
            }
        }
        return null;
    }

    private Boolean boolOrNull(Resource r, Property p) {
        Statement s = r.getProperty(p);
        return s != null ? s.getBoolean() : null;
    }

    private String objectUriOrNull(Resource r, Property p) {
        Statement s = r.getProperty(p);
        return s != null ? s.getObject().asResource().getURI() : null;
    }

    private List<String> objectUris(Resource r, Property p) {
        return r.listProperties(p).toList().stream()
                .map(s -> s.getObject().asResource().getURI())
                .collect(Collectors.toList());
    }

    /**
     * Flat, uninterpreted read of one individual - resolved into real JPA entities in a second pass.
     */
    public static class RawConcept {
        public String uri;
        public String identifier;
        public String creator;
        public String status;
        public String versionInfo;
        public String prefLabel;
        public String altLabel;
        public String definition;
        public String toolIdentifier;
        public String serialNumber;
        public Boolean mandatory;
        public Boolean multiple;
        public List<String> isMemberOf = List.of();
        public List<String> canHost = List.of();
        public String hasProcess;
        public String hasAction;
        public String withTool;
        public List<String> hasProperty = List.of();
        public List<String> realizedBy = List.of();
        public List<String> triggersHostedEvent = List.of();

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof RawConcept that)) return false;
            return Objects.equals(uri, that.uri) && Objects.equals(identifier, that.identifier);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uri, identifier);
        }
    }
}
