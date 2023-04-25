/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.EventDTOList;
import eu.eurofleets.ears3.service.EventService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    //@Operation(summary = "Find all events in DTO format")
    @RequestMapping(method = {RequestMethod.GET}, value = {"events"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public EventDTOList getEvents(@RequestParam Map<String, String> allParams) {
        List<Event> res = this.eventService.advancedFind(allParams);
        return new EventDTOList(res, true);
    }

    //@Operation(summary = "Find a single event by its identifier in DTO format")
    @RequestMapping(method = {RequestMethod.GET}, value = {"event"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public EventDTO getEventByIdentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        return new EventDTO(this.eventService.findByIdentifier(identifier));

    }
}
