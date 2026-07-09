/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.EventDTOPage;
import eu.eurofleets.ears3.domain.EventPage;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.EventDTOList;
import eu.eurofleets.ears3.service.EventService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Thomas Vandenberghe
 */
@RestController()
@RequestMapping(value = "/api/dto")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventDTOController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private EventService eventService;

    @GetMapping(value = {"events"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EventDTOPage getEvents(@RequestParam Map<String, String> allParams,
                                  @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<Event> events = this.eventService.advancedFind(allParams, pageable);
        Page<EventDTO> eventDtos = events.map(EventDTO::new);
        EventDTOPage pg = new EventDTOPage(eventDtos);
        return pg;
    }

    @GetMapping(value = {"event"}, params = {"identifier"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EventDTO getEventByIdentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        return new EventDTO(this.eventService.findByIdentifier(identifier));

    }
}
