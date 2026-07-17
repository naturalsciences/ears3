package eu.eurofleets.ears3.service.ontology;

import eu.eurofleets.ears3.domain.ontology.ActionDefinition;
import eu.eurofleets.ears3.domain.ontology.EarsTermDefinitionBase;
import eu.eurofleets.ears3.domain.ontology.GenericEventDefinition;
import eu.eurofleets.ears3.domain.ontology.ProcessDefinition;
import eu.eurofleets.ears3.domain.ontology.PropertyDefinition;
import eu.eurofleets.ears3.domain.ontology.SpecificEventDefinition;
import eu.eurofleets.ears3.domain.ontology.ToolCategoryDefinition;
import eu.eurofleets.ears3.domain.ontology.ToolDefinition;
import eu.eurofleets.ears3.rdf.OntologyRdfReader;
import eu.eurofleets.ears3.rdf.OntologyRdfReader.RawConcept;
import eu.eurofleets.ears3.service.ontology.ActionDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.GenericEventDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.ProcessDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.PropertyDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.SpecificEventDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.ToolCategoryDefinitionRepository;
import eu.eurofleets.ears3.service.ontology.ToolDefinitionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * OPTIONAL, master/ACTIVE-instance-only convenience: fully repopulates the ontology
 * database tables from an RDF file, so the tree-browsing UI has something to show.
 *
 * This is deliberately NOT wired into the default ship/PASSIVE workflow - see
 * StagedRdfService and the project README. On a passive ship, activating a file
 * never calls this; the database stays empty/unused and the RDF file itself remains
 * the single source of truth for SPARQL.
 *
 * Two-pass import: pass 1 creates every entity row keyed by its RDF URI (since
 * object-property relations reference other individuals that may not exist yet in
 * JPA-land when first encountered), pass 2 wires all the relations using that URI map.
 *
 * KNOWN GAP: realizedBy / triggersHostedEvent (both reference SEV/GEV rows created in
 * pass 2 itself) are not yet resolved here - they need a third pass once this has
 * been verified against a real exported file. Left as a clearly marked TODO rather
 * than a guessed implementation.
 */
@Service
public class OntologyRdfImportService {

    private final ToolCategoryDefinitionRepository tcRepo;
    private final ToolDefinitionRepository toolRepo;
    private final ProcessDefinitionRepository processRepo;
    private final ActionDefinitionRepository actionRepo;
    private final PropertyDefinitionRepository propertyRepo;
    private final GenericEventDefinitionRepository gevRepo;
    private final SpecificEventDefinitionRepository sevRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public OntologyRdfImportService(ToolCategoryDefinitionRepository tcRepo,
                                     ToolDefinitionRepository toolRepo,
                                     ProcessDefinitionRepository processRepo,
                                     ActionDefinitionRepository actionRepo,
                                     PropertyDefinitionRepository propertyRepo,
                                     GenericEventDefinitionRepository gevRepo,
                                     SpecificEventDefinitionRepository sevRepo) {
        this.tcRepo = tcRepo;
        this.toolRepo = toolRepo;
        this.processRepo = processRepo;
        this.actionRepo = actionRepo;
        this.propertyRepo = propertyRepo;
        this.gevRepo = gevRepo;
        this.sevRepo = sevRepo;
    }

    @Transactional
    public void importFullReplacement(InputStream rdfInput) {
        entityManager.createNativeQuery("delete from ontology_event_definition").executeUpdate();
        propertyRepo.deleteAllInBatch();
        actionRepo.deleteAllInBatch();
        processRepo.deleteAllInBatch();
        toolRepo.deleteAllInBatch();
        tcRepo.deleteAllInBatch();

        // Buffer the bytes since OntologyRdfReader needs to be constructed once but
        // we walk it multiple times below (once per readXxx() call).
        byte[] bytes;
        try {
            bytes = rdfInput.readAllBytes();
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed reading RDF input", e);
        }

        OntologyRdfReader reader = new OntologyRdfReader(new ByteArrayInputStream(bytes));
        Map<String, Object> byUri = new HashMap<>();

        for (RawConcept rc : reader.readToolCategories()) {
            ToolCategoryDefinition e = new ToolCategoryDefinition();
            applyCommon(e, rc);
            byUri.put(rc.uri, tcRepo.save(e));
        }
        for (RawConcept rc : reader.readTools()) {
            ToolDefinition e = new ToolDefinition();
            applyCommon(e, rc);
            e.setToolIdentifier(rc.toolIdentifier);
            e.setSerialNumber(rc.serialNumber);
            byUri.put(rc.uri, toolRepo.save(e));
        }
        for (RawConcept rc : reader.readProcesses()) {
            ProcessDefinition e = new ProcessDefinition();
            applyCommon(e, rc);
            byUri.put(rc.uri, processRepo.save(e));
        }
        for (RawConcept rc : reader.readActions()) {
            ActionDefinition e = new ActionDefinition();
            applyCommon(e, rc);
            byUri.put(rc.uri, actionRepo.save(e));
        }
        for (RawConcept rc : reader.readProperties()) {
            PropertyDefinition e = new PropertyDefinition();
            applyCommon(e, rc);
            e.setMandatory(Boolean.TRUE.equals(rc.mandatory));
            e.setMultiple(Boolean.TRUE.equals(rc.multiple));
            byUri.put(rc.uri, propertyRepo.save(e));
        }

        // Pass 2: relations, now that every URI resolves to a saved row.
        for (RawConcept rc : reader.readTools()) {
            ToolDefinition tool = (ToolDefinition) byUri.get(rc.uri);
            for (String catUri : rc.isMemberOf) {
                ToolCategoryDefinition tc = (ToolCategoryDefinition) byUri.get(catUri);
                if (tc != null) {
                    tool.getToolCategories().add(tc);
                }
            }
            for (String hostedUri : rc.canHost) {
                ToolDefinition hosted = (ToolDefinition) byUri.get(hostedUri);
                if (hosted != null) {
                    tool.getHostedTools().add(hosted);
                }
            }
            toolRepo.save(tool);
        }

        for (RawConcept rc : reader.readGenericEventDefinitions()) {
            GenericEventDefinition e = new GenericEventDefinition();
            applyCommon(e, rc);
            e.setProcess((ProcessDefinition) byUri.get(rc.hasProcess));
            e.setAction((ActionDefinition) byUri.get(rc.hasAction));
            e.setToolCategory((ToolCategoryDefinition) byUri.get(rc.withTool));
            for (String propUri : rc.hasProperty) {
                PropertyDefinition p = (PropertyDefinition) byUri.get(propUri);
                if (p != null) {
                    e.getProperties().add(p);
                }
            }
            byUri.put(rc.uri, gevRepo.save(e));
        }

        for (RawConcept rc : reader.readSpecificEventDefinitions()) {
            SpecificEventDefinition e = new SpecificEventDefinition();
            applyCommon(e, rc);
            e.setProcess((ProcessDefinition) byUri.get(rc.hasProcess));
            e.setAction((ActionDefinition) byUri.get(rc.hasAction));
            e.setTool((ToolDefinition) byUri.get(rc.withTool));
            for (String propUri : rc.hasProperty) {
                PropertyDefinition p = (PropertyDefinition) byUri.get(propUri);
                if (p != null) {
                    e.getProperties().add(p);
                }
            }
            byUri.put(rc.uri, sevRepo.save(e));
        }

        // TODO: third pass for realizedBy / triggersHostedEvent, once verified against
        // a real exported file - see class-level javadoc.
    }

    private void applyCommon(EarsTermDefinitionBase e, RawConcept rc) {
        // rc.uri is the EarsTerm individual's own RDF node - guaranteed unique per
        // resource. rc.identifier is the dc:identifier of whatever :asConcept points
        // to, which is a NERC-vocab-style Concept - and more than one EarsTerm
        // (e.g. two Tool instances on board with different labels/toolIdentifier)
        // can legitimately share the same Concept, so that value is NOT safe as the
        // unique `identifier` column here. Keep it, non-unique, in substituteIdentifier.
        e.setIdentifier(rc.uri);
        e.setSubstituteIdentifier(rc.identifier);
        e.setPrefLabel(rc.prefLabel);
        e.setAltLabel(rc.altLabel);
        e.setDefinition(rc.definition);
        e.setCreator(rc.creator);
        e.setStatus(rc.status);
        e.setVersionInfo(rc.versionInfo);
    }
}
