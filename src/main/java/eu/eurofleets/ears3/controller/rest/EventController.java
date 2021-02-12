package eu.eurofleets.ears3.controller.rest;

import be.naturalsciences.bmdc.cruise.model.IProperty;
import be.naturalsciences.bmdc.ontology.writer.StringUtils;
import com.opencsv.CSVWriter;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.EventList;
import eu.eurofleets.ears3.domain.Message;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Weather;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.ProgramService;
import java.io.IOException;
import java.io.Writer;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.io.output.StringBuilderWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private EventService eventService;
    @Autowired
    private ProgramService programService;
    @Autowired
    private CruiseService cruiseService;

    @Autowired
    private Environment env;

    /*@RequestMapping(method = {RequestMethod.GET}, value = {"events"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public EventList getEvents() {
        List<Event> res = this.eventService.findAll();
        return new EventList(res);
    }*/
    public static String sanitizeParam(Map<String, String> allParams, String name) {
        String get = allParams.get(name);
        if (get == null || get.equals("") || get.equals(" ")) {
            return null;
        } else {
            return get;
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"events"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public EventList getEvents(@RequestParam Map<String, String> allParams) {
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

        return new EventList(res);
    }

    private static String doubleOrNull(Double val) {
        return (val != null) ? val.toString() : "";
    }

    private static String instantOrNull(Instant val) {
        return (val != null) ? val.toString() : "";
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"events.csv"}, produces = {"text/csv; charset=utf-8"})
    public String getEventsAsCSV(@RequestParam(value = "platformIdentifier", required = false, defaultValue = "") String platformIdentifier) throws IOException {

        List<Event> events;
        if (platformIdentifier == null || "".equals(platformIdentifier)) {
            events = this.eventService.findAll();
        } else {
            events = this.eventService.findAllByPlatformCode(platformIdentifier);
        }

        List<String> header = new ArrayList<>(Arrays.asList("Time stamp", "Actor", "Tool category", "Tool", "Process", "Action",
                "Acquisition Timestamp", "Latitude", "Longitude", "Depth", "Surface water temperature", "Heading", "Course over Ground", "Speed over Ground"));
        Map<String, String> properties = new TreeMap<>();
        for (Event event : events) {
            for (IProperty property : event.getProperties()) {
                properties.put(property.getKey().getIdentifier(), property.getKey().getName());
            }
        }
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String propertyName = entry.getValue();
            header.add(propertyName);
        }
        /*  header.addAll(Arrays.asList("Heading", "Course over Ground", "Speed over Ground",
                "Salinity", "Conductivity", "Sigma T", "Wind speed", "Wind direction",
                "Air temperature", "Air pressure", "Solar Radiation", "Turbidity L",
                "Turbidity H", "OBS L", "OBS H", "Salinity", "Chlorophyll", "Blue Algae",
                "CDOM", "pH", "Fluorescence", "pCO2", "PAR"));*/

        header.addAll(Arrays.asList("Salinity", "Conductivity", "Sigma T", "Wind speed", "Wind direction",
                "Air temperature", "Air pressure", "Solar Radiation"));

        String[] entry = new String[header.size()];
        entry = header.toArray(entry);

        Writer writer = new StringBuilderWriter();
        CSVWriter csvWriter = null;

        csvWriter = new CSVWriter(writer, ',');
        csvWriter.writeNext(entry, true);

        for (Event event : events) {
            Navigation nav = (Navigation) (new ArrayList(event.getNavigation())).get(0);
            Thermosal tss = (Thermosal) (new ArrayList(event.getThermosal())).get(0);
            Weather met = (Weather) (new ArrayList(event.getWeather())).get(0);
            List<String> elements = new ArrayList<>(Arrays.asList(
                    event.getTimeStamp().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    event.getActor().getFirstName() + event.getActor().getLastName(),
                    event.getToolCategory().getName(),
                    event.getTool().getTerm().getName(),
                    event.getProcess().getName(),
                    event.getAction().getName()
            ));
            if (nav != null) {
                elements.addAll(Arrays.asList(
                        instantOrNull(nav.getTimeStamp()),
                        doubleOrNull(nav.getLat()),
                        doubleOrNull(nav.getLon()),
                        doubleOrNull(nav.getDepth()),
                        doubleOrNull(tss.getTemperature()),
                        doubleOrNull(nav.getHeading()),
                        doubleOrNull(nav.getCog()),
                        doubleOrNull(nav.getSog())));
            } else {
                elements.addAll(Arrays.asList(
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""));
            }

            for (String propertyUrl : properties.keySet()) {
                List<String> propertyValues = event.getPropertyValues(propertyUrl);
                if (propertyValues != null) {
                    elements.add(StringUtils.join(propertyValues, ","));
                } else {
                    elements.add("");
                }
            }
            if (tss != null) {
                elements.addAll(Arrays.asList(
                        doubleOrNull(tss.getSalinity()),
                        doubleOrNull(tss.getConductivity()),
                        doubleOrNull(tss.getSigmat())));
            } else {
                elements.addAll(Arrays.asList(
                        "",
                        "",
                        ""));
            }
            if (met != null) {
                elements.addAll(Arrays.asList(
                        doubleOrNull(met.getWindSpeedAverage()),
                        doubleOrNull(met.getWindDirection()),
                        doubleOrNull(met.getAtmosphericTemperature()),
                        doubleOrNull(met.getAtmosphericPressure()),
                        doubleOrNull(met.getSolarRadiation())));
            } else {
                elements.addAll(Arrays.asList(
                        "",
                        "",
                        "",
                        "",
                        ""));
            }
            entry = new String[elements.size()];
            entry = elements.toArray(entry);

            csvWriter.writeNext(entry, true);
        }
        writer.flush();
        writer.close();
        return writer.toString();
    }

    /*@RequestMapping(method = {RequestMethod.GET}, value = {"events"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public EventList getEventsByActorAndProgram(@RequestParam(required = false, defaultValue = "") String platformIdentifier, @RequestParam(required = false, defaultValue = "") String programIdentifier, @RequestParam(required = false, defaultValue = "") String actorEmail) {
        List<Event> res;
        if ((programIdentifier == null || "".equals(programIdentifier)) && (actorEmail == null || "".equals(actorEmail))) {
            return getEvents(platformIdentifier);
        } else {
            res = this.eventService.findAllByPlatformActorAndProgram(platformIdentifier, actorEmail, programIdentifier);
        }
        return new EventList(res);
    }*/
    @RequestMapping(method = {RequestMethod.GET}, value = {"event/{id}"}, produces = {"application/xml", "application/json"})
    public Event getEventById(@PathVariable(value = "id") String id) {
        return this.eventService.findById(Long.parseLong(id));

    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"event"}, params = {"identifier"}, produces = {"application/xml", "application/json"})
    public Event getEventByIdentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        return this.eventService.findByIdentifier(identifier);
    }

    @PostMapping(value = {"event"}, produces = {"application/xml; charset=utf-8", "application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Message<EventDTO>> createEvent(@RequestBody EventDTO eventDTO) {
        if (eventDTO.platform == null) {
            String property = env.getProperty("ears.platform");
            if (property != null) {
                eventDTO.setPlatform(property);
            } else {
                throw new IllegalArgumentException("No platform has been provided in the POST body and no platform has been set in the web service configuration.");
            }
        }
        Event event = this.eventService.save(eventDTO);
        eventDTO = new EventDTO(event);
        return new ResponseEntity<Message<EventDTO>>(new Message<EventDTO>(HttpStatus.CREATED.value(), event.getIdentifier(), eventDTO), HttpStatus.CREATED);
        // return new ResponseEntity<Event>(, HttpStatus.CREATED);event
    }

    @DeleteMapping(value = {"event"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String removeEventByIdentifier(@RequestParam(required = true) String identifier) {
        this.eventService.deleteByIdentifier(identifier);
        return "";
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
