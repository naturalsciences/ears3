package eu.eurofleets.ears3.domain.ears2;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IProperty;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.Property;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://www.eurofleets.eu/", name = "event")
public class EventBean implements Serializable {

    private Event event;

    public EventBean() {
    }

    public EventBean(Event event) {
        this.event = event;
    }

    @XmlAttribute(name = "eventId")
    public String getEventId() {
        return event.getIdentifier();
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "id")
    public String getId() {
        return Long.toString(event.getId());
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "timeStamp")
    public String getTimeStamp() {
        return event.getTimeStamp().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "program")
    public String getProgram() {
        if (event.getProgram() != null) {
            return event.getProgram().getIdentifier();
        } else {
            return null;
        }
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "actor")
    public ActorBean getActor() {
        return new ActorBean(event.getActor());
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "platform")
    public String getPlatform() {
        return termToJson(event.getPlatform().getTerm());
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "property")
    public String getProperty() {
        StringJoiner sj = new StringJoiner(",");

        for (IProperty property : event.getProperties()) {
            Property prop = (Property) property;
            ILinkedDataTerm key = prop.getKey();
            sj.add("{\"code\":\"" + key.getIdentifier() + "\",\"name\":\"" + key.getName() + "\",\"value\":\"" + prop.getValue() + "\"}");
        }

        return "[" + sj.toString() + "]";
    }

    private String termToJson(ILinkedDataTerm term) {
        return "{\"" + term.getIdentifier() + "\":\"" + term.getName() + "\"}";
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "toolCategory")
    public String getToolCategory() {
        return termToJson(event.getToolCategory());
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "tool")
    public ToolBean getTool() {
        return new ToolBean(event.getTool());
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "process")
    public String getProcess() {
        return termToJson(event.getProcess());
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "action")
    public String getAction() {
        return termToJson(event.getAction());
    }

    @Override
    public String toString() {
        return this.event.toString();
    }
}
