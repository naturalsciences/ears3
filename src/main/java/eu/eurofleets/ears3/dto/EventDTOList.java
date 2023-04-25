/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import eu.eurofleets.ears3.domain.Event;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Thomas Vandenberghe
 */
@XmlRootElement(name = "events")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventDTOList {

    @XmlElement(name = "event")
    private List<EventDTO> events;

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

    public List<EventDTO> getEvents() {
        if (this.events == null) {
            this.events = new ArrayList<>();
        }
        return this.events;
    }

    public void setEvents(List<EventDTO> events) {
        this.events = events;
    }
}
