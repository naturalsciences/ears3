/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import static eu.eurofleets.ears3.controller.rest.EventController.sanitizeParam;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.EventList;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.EventDTOList;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.ProgramService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thomas
 */
@RestController()
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventDTOController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private EventService eventService;
    @Autowired
    private ProgramService programService;
    @Autowired
    private CruiseService cruiseService;

    @RequestMapping(method = {RequestMethod.GET}, value = {"dto/events"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public EventDTOList getEvents(@RequestParam Map<String, String> allParams) {
        String platformIdentifier = sanitizeParam(allParams, "platformIdentifier");
        String cruiseIdentifier = sanitizeParam(allParams, "cruiseIdentifier");
        String programIdentifier = sanitizeParam(allParams, "programIdentifier");
        String actorEmail = sanitizeParam(allParams, "actorEmail");
        String startDate = sanitizeParam(allParams, "startDate");
        String endDate = sanitizeParam(allParams, "endDate");
        OffsetDateTime start = null;
        OffsetDateTime end = null;
        if (startDate != null) {
            start = OffsetDateTime.parse(startDate);
        }
        if (endDate != null) {
            end = OffsetDateTime.parse(endDate);
        }

        List<Event> res = null;
        if (platformIdentifier == null && programIdentifier == null && actorEmail == null && start == null && end == null && cruiseIdentifier == null) {
            res = this.eventService.findAll();
        } else if (programIdentifier == null && actorEmail == null && start == null && end == null && cruiseIdentifier == null) {
            res = this.eventService.findAllByPlatformCode(platformIdentifier);
        } else if (cruiseIdentifier == null) {
            res = this.eventService.findAllByPlatformActorProgramAndDates(platformIdentifier, actorEmail, programIdentifier, start, end);
        } else {
            res = this.eventService.findAllByCruiseProgramAndActor(cruiseIdentifier, programIdentifier, actorEmail);
        }

        EventDTOList r = new EventDTOList(res, true);
        return r;
    }

    /*   @RequestMapping(method = {RequestMethod.GET}, value = {"events"}, params = {"platformIdentifier"}, produces = {"application/json"})
    public EventDTOList getEvents(@RequestParam(required = false, defaultValue = "") String platformIdentifier) {
        List<Event> res;
        if (platformIdentifier == null || "".equals(platformIdentifier)) {
            res = this.eventService.findAll();
        } else {
            res = this.eventService.findAllByPlatformCode(platformIdentifier);
        }
        return new EventDTOList(res, true);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"dto/events"}, params = {"after"}, produces = {"application/json"})
    public EventDTOList getRecentOrModifiedEvents(@RequestParam(name = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime after) {
        List<Event> res = this.eventService.findCreatedOrModifiedAfter(after);
        return new EventDTOList(res, true);
    }*/
    @RequestMapping(method = {RequestMethod.GET}, value = {"dto/event"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public EventDTO getEventByIdentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        return new EventDTO(this.eventService.findByIdentifier(identifier));

    }
}
