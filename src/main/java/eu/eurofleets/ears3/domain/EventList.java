package eu.eurofleets.ears3.domain;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "events")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventList {
    
    @XmlElement(name = "event")
    private List<Event> events;

    public EventList() {
    }

    public EventList(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        if (this.events == null) {
            this.events = new ArrayList<>();
        }
        return this.events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
