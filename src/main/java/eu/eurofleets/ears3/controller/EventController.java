package eu.eurofleets.ears3.controller;

import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.EventList;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.ProgramService;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private EventService eventService;
    @Autowired
    private ProgramService programService;
    @Autowired
    private CruiseService cruiseService;

    @RequestMapping(method = {RequestMethod.GET}, value = {"events"}, produces = {"application/xml"})
    public EventList getEvents() {
        return new EventList(this.eventService.findAll());
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"event"}, params = {"id"}, produces = {"application/xml"})
    public Event getEventById(@PathParam(value = "id") long id) {
        return this.eventService.findById(id);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"event"}, params = {"identifier"}, produces = {"application/xml"})
    public Event getEventByIdentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        return this.eventService.findByIdentifier(identifier);
    }

    @PostMapping(value = {"event"}, produces = {"application/xml"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDTO) {
        return new ResponseEntity<Event>(this.eventService.save(eventDTO), HttpStatus.CREATED);
    }

    /* @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"getEvent"}, params = {"date"}, produces = {"application/xml"})
    public Event getEventByDate(@RequestParam(required = true, value = "date") String date) throws ParseException {
        return this.eventService.getEventByDate(DateUtilities.parseDate(date));
    }*/

 /*@RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"insertEvent"}, params = {"id"}, produces = {"application/xml"})
    public Message insertEvent(@RequestParam(required = true) String id, @RequestParam(required = false) String date, @RequestParam(required = false) String actor, @RequestParam(required = false) String subject, @RequestParam(required = false) String actionName, @RequestParam(required = false) String actionProperty, @RequestParam(required = false) String categoryName, @RequestParam(required = false) String tool)
            throws ParseException {
        Event event = new Event();
        event.setEventId(id);
        if (date != null) {
            event.setTimeStamp(DateUtilities.parseDate(date));
        }
        event.setActor(actor);
        event.setSubject(subject);
        event.setActionName(actionName);
        event.setActionProperty(actionProperty);
        event.setCategoryName(categoryName);

        if (tool != null) {

            ToolO toolObject = new ToolO();
            toolObject.setName(tool);
            event.setTool(toolObject);
        }

        this.eventService.insertEvent(event);

        return new Message(id, "Event inserted");
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"getEvent"}, params = {"program"}, produces = {"application/xml"})
    public EventList getEventByProgram(@RequestParam(required = true, value = "program") String program) throws ParseException {
        ProgramO programObj = this.programService.getProgramByID(program);
        System.out.println("** " + programObj.toString());
        CruiseO cruise = this.cruiseService.getCruiseByCruiseId(programObj.getCruiseId());
        System.out.println("** " + cruise.toString());
        List<Event> list = this.eventService.getEventBetweenDates(cruise.getStartDate(), cruise.getEndDate());
        return new EventList(list);
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"getEvent"}, params = {"cruise"}, produces = {"application/xml"})
    public EventList getEventByCruise(@RequestParam(required = true, value = "cruise") String cruise) throws ParseException {
        CruiseO cruiseObj = this.cruiseService.getCruiseByCruiseId(cruise);
        System.out.println("** " + cruiseObj.toString());
        System.out.println("** " + cruise.toString());
        List<Event> list = this.eventService.getEventBetweenDates(cruiseObj.getStartDate(), cruiseObj.getEndDate());
        return new EventList(list);
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"getEvent"}, params = {"startDate", "endDate"}, produces = {"application/xml"})
    public EventList getEventByRange(@RequestParam(required = true, value = "startDate") String startDate, @RequestParam(required = true, value = "endDate") String endDate)
            throws ParseException {
        List<Event> res = this.eventService.getEventBetweenDates(DateUtilities.parseDate(startDate), DateUtilities.parseDate(endDate));

        return new EventList(res);
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"removeEvent"}, params = {"id"}, produces = {"application/xml"})
    public Message removeEventByIDXML(@RequestParam(required = true, value = "id") String eventId) {
        this.eventService.removeEventById(eventId);
        return new Message("0", "Event deleted");
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"removeEvent"}, params = {"startDate", "endDate"}, produces = {"application/xml"})
    public Message removeEventByRange(@RequestParam(required = true, value = "startDate") String startDate, @RequestParam(required = true, value = "endDate") String endDate)
            throws ParseException {
        this.eventService.removeEventsBetweenDates(DateUtilities.parseDate(startDate), DateUtilities.parseDate(endDate));

        return new Message("0", "Events deleted");
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"modifyEvent"}, produces = {"application/xml"})
    public Message modifyEvent(@RequestParam(required = true) String eventId, @RequestParam(required = true) String timeStamp, @RequestParam(required = true) String actor, @RequestParam(required = true) String subject, @RequestParam(required = true) String actionName, @RequestParam(required = true) String actionProperty, @RequestParam(required = true) String categoryName, @RequestParam(required = true) String tool)
            throws ParseException {
        Event event = this.eventService.getEventById(eventId);
        event.setEventId(eventId);
        event.setTimeStamp(DateUtilities.parseDate(timeStamp));
        event.setActor(actor);
        event.setSubject(subject);
        event.setActionName(actionName);
        event.setActionProperty(actionProperty);
        event.setCategoryName(categoryName);

        this.eventService.insertEvent(event);

        return new Message(eventId, "Event modified");
    }*/
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu.eurofleets.ears3/controller/EventController.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
