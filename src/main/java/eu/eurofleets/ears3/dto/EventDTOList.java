/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.eurofleets.ears3.domain.Event;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author thomas
 */
@XmlRootElement(name = "events")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventDTOList extends ArrayList<EventDTO> {

    public EventDTOList() {
    }

    public EventDTOList(List<EventDTO> events) {
        this.events = events;
    }

    public EventDTOList(List<Event> events, boolean fake) {
        if (this.events == null) {
            this.events = new ArrayList<>();
        }
        for (Event event : events) {
            this.events.add(new EventDTO(event));
        }
    }

    @JsonProperty("events")
    @XmlElement(name = "event")
    private List<EventDTO> events = null;

    public List<EventDTO> getEvents() {
        return this.events;
    }

    public void setEvents(List<EventDTO> events) {
        this.events = events;
    }
}
