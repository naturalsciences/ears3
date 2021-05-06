package eu.eurofleets.ears3.domain.ears2;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import be.naturalsciences.bmdc.cruise.model.ITool;

@XmlRootElement(namespace = "http://www.eurofleets.eu/", name = "tool")
public class ToolBean {

    private String name;
    private String identifier;
    private ToolBean parentTool;

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "identifier")
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "parentTool")
    public ToolBean getParentTool() {
        return parentTool;
    }

    public ToolBean() {
    }

    public ToolBean(ITool tool) {
        this.name = tool.getTerm().getName();
        this.identifier = tool.getTerm().getIdentifier();
        if (tool.getParentTool() != null) {
            this.parentTool = new ToolBean(tool.getParentTool().getName(), tool.getParentTool().getIdentifier(), null);
        }
    }

    public ToolBean(String name, String identifier, ToolBean parentTool) {
        this.name = name;
        this.identifier = identifier;
        this.parentTool = parentTool;
    }

    @Override
    public String toString() {
        return "ToolBean{" + "name=" + name + '}';
    }

    public String fullName() {
        String r = this.name;
        if (getParentTool() != null) {
            r = r + " ∈ " + getParentTool().getName();
        }
        return r;
    }

}
