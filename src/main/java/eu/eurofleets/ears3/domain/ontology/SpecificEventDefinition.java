package eu.eurofleets.ears3.domain.ontology;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ontology_specific_event_definition")
public class SpecificEventDefinition extends EventDefinition {

    @ManyToOne(optional = false)
    private ToolDefinition tool;

    /** realizedBy inverse - nullable, points to the generic event this one specializes. */
    @ManyToOne(optional = true)
    private GenericEventDefinition realizes;

    public ToolDefinition getTool() {
        return tool;
    }

    public void setTool(ToolDefinition tool) {
        this.tool = tool;
    }

    public GenericEventDefinition getRealizes() {
        return realizes;
    }

    public void setRealizes(GenericEventDefinition realizes) {
        this.realizes = realizes;
    }
}
