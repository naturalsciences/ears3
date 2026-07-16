package eu.eurofleets.ears3.domain.ontology;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ontology_generic_event_definition")
public class GenericEventDefinition extends EventDefinition {

    @ManyToOne(optional = false)
    private ToolCategoryDefinition toolCategory;

    public ToolCategoryDefinition getToolCategory() {
        return toolCategory;
    }

    public void setToolCategory(ToolCategoryDefinition toolCategory) {
        this.toolCategory = toolCategory;
    }
}
