package eu.eurofleets.ears3.domain.ontology;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ontology_tool")
public class ToolDefinition extends EarsTermDefinitionBase {

    private String toolIdentifier;

    private String serialNumber;

    /** isMemberOf - a Tool can belong to several ToolCategories. Owning side. */
    @ManyToMany
    @JoinTable(name = "ontology_tool_toolcategory",
            joinColumns = @JoinColumn(name = "tool_id"),
            inverseJoinColumns = @JoinColumn(name = "toolcategory_id"))
    private Set<ToolCategoryDefinition> toolCategories = new HashSet<>();

    /** canHost - tools this tool can physically host (owning side). */
    @ManyToMany
    @JoinTable(name = "ontology_tool_hosts",
            joinColumns = @JoinColumn(name = "host_tool_id"),
            inverseJoinColumns = @JoinColumn(name = "hosted_tool_id"))
    private Set<ToolDefinition> hostedTools = new HashSet<>();

    /** Inverse of hostedTools - tools that host this one. A non-empty set here means isHostedTool(). */
    @ManyToMany(mappedBy = "hostedTools")
    private Set<ToolDefinition> hostTools = new HashSet<>();

    public boolean isHostedTool() {
        return !hostTools.isEmpty();
    }

    public String getToolIdentifier() {
        return toolIdentifier;
    }

    public void setToolIdentifier(String toolIdentifier) {
        this.toolIdentifier = toolIdentifier;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Set<ToolCategoryDefinition> getToolCategories() {
        return toolCategories;
    }

    public void setToolCategories(Set<ToolCategoryDefinition> toolCategories) {
        this.toolCategories = toolCategories;
    }

    public Set<ToolDefinition> getHostedTools() {
        return hostedTools;
    }

    public void setHostedTools(Set<ToolDefinition> hostedTools) {
        this.hostedTools = hostedTools;
    }

    public Set<ToolDefinition> getHostTools() {
        return hostTools;
    }

    public void setHostTools(Set<ToolDefinition> hostTools) {
        this.hostTools = hostTools;
    }
}
