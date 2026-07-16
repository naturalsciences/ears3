package eu.eurofleets.ears3.domain.ontology;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ontology_tool_category")
public class ToolCategoryDefinition extends EarsTermDefinitionBase {

    /** isMemberOf inverse - Tools that belong to this category. Owning side is ToolDefinition.toolCategories. */
    @ManyToMany(mappedBy = "toolCategories")
    private Set<ToolDefinition> tools = new HashSet<>();

    @OneToMany(mappedBy = "toolCategory")
    private Set<GenericEventDefinition> genericEventDefinitions = new HashSet<>();

    public Set<ToolDefinition> getTools() {
        return tools;
    }

    public void setTools(Set<ToolDefinition> tools) {
        this.tools = tools;
    }

    public Set<GenericEventDefinition> getGenericEventDefinitions() {
        return genericEventDefinitions;
    }

    public void setGenericEventDefinitions(Set<GenericEventDefinition> genericEventDefinitions) {
        this.genericEventDefinitions = genericEventDefinitions;
    }
}
