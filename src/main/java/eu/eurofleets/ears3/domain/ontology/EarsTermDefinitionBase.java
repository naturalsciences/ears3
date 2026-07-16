package eu.eurofleets.ears3.domain.ontology;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.time.OffsetDateTime;

/**
 * Shared term-level metadata for every ontology concept (ToolCategory, Tool, Process,
 * Action, Property, GenericEventDefinition, SpecificEventDefinition).
 *
 * Mirrors the "concept" metadata that EARSOntologyCreator.makeConceptAssertions()
 * writes onto a linked skos:Concept individual in the RDF (skos:prefLabel, dc:creator,
 * dc:identifier, owl:versionInfo, etc.) - here it's flattened directly onto the entity
 * for simplicity, since this is the editable-database side, not the RDF side.
 *
 * Named "...Definition" throughout this package to keep these entities clearly
 * separate from the EARS event tables (eu.eurofleets.ears3.domain.Tool, .Property, etc.)
 * which represent actual logged events, not the vocabulary/ontology itself.
 */
@MappedSuperclass
public abstract class EarsTermDefinitionBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Stable URI/URN for this term. On EventDefinition subclasses, this is the value
     * that eu.eurofleets.ears3.domain.Event.eventDefinitionId is matched against.
     * Deliberately NOT a hard foreign key - see the note in OntologyTreeService /
     * project README about why this stays a soft/logical reference.
     */
    @Column(unique = true, nullable = false, length = 200)
    private String identifier;

    @Column(nullable = false)
    private String prefLabel;

    private String altLabel;

    @Column(length = 2000)
    private String definition;

    /** Organisation/vessel code that authored this term. Not enforced at POC stage. */
    private String creator;

    /** e.g. ACTIVE, WAIT_FOR_APPROVAL, DEPRECATED */
    private String status;

    private String versionInfo;

    private OffsetDateTime creationDate;

    private OffsetDateTime modifDate;

    /** Soft reference (by identifier, not FK) to the term that supersedes this one, if any. */
    private String substituteIdentifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }

    public String getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(String altLabel) {
        this.altLabel = altLabel;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public OffsetDateTime getModifDate() {
        return modifDate;
    }

    public void setModifDate(OffsetDateTime modifDate) {
        this.modifDate = modifDate;
    }

    public String getSubstituteIdentifier() {
        return substituteIdentifier;
    }

    public void setSubstituteIdentifier(String substituteIdentifier) {
        this.substituteIdentifier = substituteIdentifier;
    }
}
