package eu.eurofleets.ears3.controller.rest;

import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProperty;
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
import org.apache.commons.lang3.StringUtils;
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

@RestController()
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private EventService eventService;

    @Autowired
    private Environment env;

    /*@RequestMapping(method = {RequestMethod.GET}, value = {"events"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public EventList getEvents() {
        List<Event> res = this.eventService.findAll();
        return new EventList(res);
    }*/
    @RequestMapping(method = {RequestMethod.GET}, value = {"events"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public EventList getEvents(@RequestParam Map<String, String> allParams) {
        List<Event> res = this.eventService.advancedFind(allParams);
        return new EventList(res);
    }

    private static String doubleOrNull(Double val) {
        return (val != null) ? val.toString() : "";
    }

    private static String instantOrNull(Instant val) {
        return (val != null) ? val.toString() : "";
    }

    private static String offsetDateTimeOrNull(OffsetDateTime val) {
        return (val != null) ? val.toString() : "";
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"events.csv"}, produces = {"text/csv; charset=utf-8"})
    public String getEventsAsCSV(@RequestParam Map<String, String> allParams) throws IOException {
        List<Event> events = this.eventService.advancedFind(allParams);

        List<String> header = new ArrayList<>(Arrays.asList("Time stamp", "Actor", "Program", "Principal Investigator", "Tool category", "Tool category code", "Tool", "Tool code", "Process", "Action",
                "Acquisition Timestamp", "Latitude", "Longitude", "Depth", "Heading", "Course over Ground", "Speed over Ground"));
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

        header.addAll(Arrays.asList("Surface water temperature", "Salinity", "Conductivity", "Sigma T", "Wind speed", "Wind direction",
                "Air temperature", "Air pressure", "Solar Radiation"));

        String[] entry = new String[header.size()];
        entry = header.toArray(entry);

        Writer writer = new StringBuilderWriter();
        CSVWriter csvWriter = null;

        csvWriter = new CSVWriter(writer, ',');
        csvWriter.writeNext(entry, true);

        for (Event event : events) {
            Navigation nav = (event.getNavigation().size() > 0 ? event.getNavigation().iterator().next() : null);
            Thermosal tss = (event.getThermosal().size() > 0 ? event.getThermosal().iterator().next() : null);
            Weather met = (event.getWeather().size() > 0 ? event.getWeather().iterator().next() : null);
            IProgram program = event.getProgram();
            String niceProgram = null;
            if (program != null) { //can't be null but test anyway
                niceProgram = program.getIdentifier() + (program.getName() != null && !program.getName().isEmpty() ? " (" + program.getName() + ")" : "");
            }
            List<String> elements = new ArrayList<>(Arrays.asList(
                    event.getTimeStamp().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    event.getActor().getFirstName() + " " + event.getActor().getLastName(),
                    niceProgram,
                    event.getPrincipalInvestigators(),
                    event.getToolCategory().getName(),
                    event.getToolCategory().getUrn(),
                    event.getTool().getTerm().getName(),
                    event.getTool().getTerm().getUrn(),
                    event.getProcess().getName(),
                    event.getAction().getName()
            ));
            if (nav != null) {
                elements.addAll(Arrays.asList(
                        offsetDateTimeOrNull(nav.getTime()),
                        doubleOrNull(nav.getLat()),
                        doubleOrNull(nav.getLon()),
                        doubleOrNull(nav.getDepth()),
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
                        doubleOrNull(tss.getTemperature()),
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
}
