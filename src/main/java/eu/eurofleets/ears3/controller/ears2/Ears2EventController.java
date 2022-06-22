package eu.eurofleets.ears3.controller.ears2;

import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.ears2.EventBean;
import eu.eurofleets.ears3.domain.ears2.EventBeanList;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.ProgramService;
//import io.swagger.v3.oas.annotations.Operation;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/ears2")
@CrossOrigin(origins = "*", maxAge = 3600)
public class Ears2EventController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private EventService eventService;
    @Autowired
    private ProgramService programService;
    @Autowired
    private CruiseService cruiseService;

    @Autowired
    private Environment env;

    private static String sanitizeParam(Map<String, String> allParams, String name) {
        String get = allParams.get(name);
        if (get == null || get.equals("") || get.equals(" ")) {
            return null;
        } else {
            return get;
        }
    }

    //@Operation(hidden = true, summary = "Get the events in EARS2 formatted xml.")
    @RequestMapping(method = {RequestMethod.GET}, value = {"events"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public EventBeanList getEvents(@RequestParam Map<String, String> allParams) {
        String platformIdentifier = sanitizeParam(allParams, "platformIdentifier");
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
        if (platformIdentifier == null && programIdentifier == null && actorEmail == null && start == null && end == null) {
            res = this.eventService.findAll();
        } else if (programIdentifier == null && actorEmail == null && start == null && end == null) {
            res = this.eventService.findAllByPlatformCode(platformIdentifier);
        } else {
            res = this.eventService.findAllByPlatformActorProgramAndDates(platformIdentifier, actorEmail, programIdentifier, start, end);
        }

        return new EventBeanList(res, true);
    }

    //@Operation(hidden = true, summary = "Get an event by identifier EARS2 formatted xml.")
    @RequestMapping(method = {RequestMethod.GET}, value = {"event"}, params = {"identifier"}, produces = {"application/xml", "application/json"})
    public EventBean getEventByIdentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        return new EventBean(this.eventService.findByIdentifier(identifier));
    }
}
