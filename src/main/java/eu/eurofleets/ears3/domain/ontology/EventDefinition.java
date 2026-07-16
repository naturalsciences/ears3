package eu.eurofleets.ears3.domain.ontology;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

/**
 * Common base for GenericEventDefinition (tool-category level) and
 * SpecificEventDefinition (specific-tool level). JOINED inheritance so that
 * triggeredEvents can hold a mix of both subtypes in one join table without a
 * hand-rolled discriminator, matching how AsConceptNode/ConceptHierarchy treat
 * both kinds of event definitions polymorphically.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ontology_event_definition")
public abstract class EventDefinition extends EarsTermDefinitionBase {

    @ManyToOne(optional = false)
    private ProcessDefinition process;

    @ManyToOne(optional = false)
    private ActionDefinition action;

    private boolean dataProvider;

    @ManyToMany
    @JoinTable(name = "ontology_event_triggers",
            joinColumns = @JoinColumn(name = "event_definition_id"),
            inverseJoinColumns = @JoinColumn(name = "triggered_event_definition_id"))
    private Set<EventDefinition> triggeredEvents = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "ontology_event_properties",
            joinColumns = @JoinColumn(name = "event_definition_id"),
            inverseJoinColumns = @JoinColumn(name = "property_id"))
    private Set<PropertyDefinition> properties = new HashSet<>();

    public ProcessDefinition getProcess() {
        return process;
    }

    public void setProcess(ProcessDefinition process) {
        this.process = process;
    }

    public ActionDefinition getAction() {
        return action;
    }

    public void setAction(ActionDefinition action) {
        this.action = action;
    }

    public boolean isDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(boolean dataProvider) {
        this.dataProvider = dataProvider;
    }

    public Set<EventDefinition> getTriggeredEvents() {
        return triggeredEvents;
    }

    public void setTriggeredEvents(Set<EventDefinition> triggeredEvents) {
        this.triggeredEvents = triggeredEvents;
    }

    public Set<PropertyDefinition> getProperties() {
        return properties;
    }

    public void setProperties(Set<PropertyDefinition> properties) {
        this.properties = properties;
    }
}
