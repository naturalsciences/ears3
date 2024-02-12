package eu.eurofleets.ears3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.domain.Acquisition;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.Property;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.domain.Weather;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.PropertyDTO;
import eu.eurofleets.ears3.utilities.DatagramUtilities;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.IterableUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    private LinkedDataTermService ldtService;
    @Autowired
    private ToolService toolService;
    @Autowired
    private ProgramService programService;
    @Autowired
    private PersonService personService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private PlatformService platformService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private NavigationService navigationService;
    @Autowired
    private ThermosalService thermosalService;
    @Autowired
    private WeatherService weatherService;

    @Autowired
    private ObjectMapper objectMapper;

    private DatagramUtilities<Navigation> navUtil;
    private DatagramUtilities<Thermosal> thermosalUtil;
    private DatagramUtilities<Weather> weatherUtil;
    public static Logger log = Logger.getLogger(EventService.class.getSimpleName());

    @Autowired
    private final Environment env;

    @Autowired
    public EventService(EventRepository eventRepository, Environment env) {

        this.eventRepository = eventRepository;
        this.env = env;
        String navigationServer = env.getProperty("ears.navigation.server");
        try {
            navUtil = new DatagramUtilities<>(Navigation.class, navigationServer);
            thermosalUtil = new DatagramUtilities<>(Thermosal.class, navigationServer);
            weatherUtil = new DatagramUtilities<>(Weather.class, navigationServer);
        } catch (MalformedURLException ex) {
            Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Event findById(Long id) {
        Event event = this.eventRepository.findById(id).orElse(null);
        if (event != null) {
            return event;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no event with id " + id);
        }
    }

    public Event findByIdentifier(String identifier) {
        Event event = this.eventRepository.findByIdentifier(identifier);
        if (event != null) {
            return event;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no event with identifier " + identifier);
        }
    }

    public static String sanitizeParam(Map<String, String> allParams, String name) {
        String get = allParams.get(name);
        if (get == null || get.equals("") || get.equals(" ")) {
            return null;
        } else {
            return get;
        }
    }

    public List<Event> advancedFind(Map<String, String> allParams) {
        String platformIdentifier = sanitizeParam(allParams, "platformIdentifier");
        String cruiseIdentifier = sanitizeParam(allParams, "cruiseIdentifier");
        String programIdentifier = sanitizeParam(allParams, "programIdentifier");
        String actorEmail = sanitizeParam(allParams, "actorEmail");
        String startDate = sanitizeParam(allParams, "startDate");
        String endDate = sanitizeParam(allParams, "endDate");
        OffsetDateTime start = null;
        OffsetDateTime end = null;

        DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        OffsetDateTime early = LocalDate.parse("1900-01-01", parser).atTime(OffsetTime.now());
        OffsetDateTime late = LocalDate.parse("2100-01-01", parser).atTime(OffsetTime.now());
        //OffsetDateTime.MAX  and OffsetDateTime.MIN lead to exceptions.

        if (startDate != null) {
            start = OffsetDateTime.parse(startDate);
            if (endDate == null) {
                end = late;
            }
        }
        if (endDate != null) {
            end = OffsetDateTime.parse(endDate);
            if (startDate == null) {
                start = early;
            }
        }

        List<Event> res = null;
        if (platformIdentifier == null && programIdentifier == null && actorEmail == null && start == null
                && end == null && cruiseIdentifier == null) {
            res = this.findAll();
        } else if (programIdentifier == null && actorEmail == null && start == null && end == null
                && cruiseIdentifier == null) {
            res = this.findAllByPlatformCode(platformIdentifier);
        } else if (cruiseIdentifier == null) {
            if (start != null && end != null) {
                res = this.findAllByPlatformActorProgramAndDates(platformIdentifier, actorEmail, programIdentifier,
                        start, end);
            } else {
                res = this.findAllByPlatformActorAndProgram(platformIdentifier, actorEmail, programIdentifier);
            }
        } else {
            res = this.findAllByCruiseProgramAndActor(cruiseIdentifier, programIdentifier, actorEmail);
        }
        return res;
    }

    public List<Event> findAll() {
        return IterableUtils.toList(this.eventRepository.findAll());
    }

    public List<Event> findByTimeStampBetween(OffsetDateTime startDate, OffsetDateTime endDate) {
        return this.eventRepository.findByTimeStampBetween(startDate, endDate);
    }

    public List<Event> findByCruise(Cruise cruise) {
        return findByTimeStampBetween(cruise.getStartDate(), cruise.getEndDate());
    }

    public List<Event> findByTool(Tool tool) {
        return this.eventRepository.findByTool(tool.getTerm().getIdentifier());
    }

    public List<Event> findAllByPlatformCode(String platformIdentifier) {
        Assert.notNull(platformIdentifier, "Platform code must not be null");
        return this.eventRepository.findByPlatformCode(platformIdentifier);
    }

    public List<Event> findByCruise(String cruiseIdentifier) {
        Assert.notNull(cruiseIdentifier, "Cruise identifier code must not be null");
        return this.eventRepository.findByCruise(cruiseIdentifier);
    }

    public List<Event> findAllByPlatformActorAndProgram(String platformIdentifier, String personEmail,
            String programIdentifier) {
        return this.eventRepository.findAllByPlatformActorAndProgram(platformIdentifier, personEmail,
                programIdentifier);
    }

    public List<Event> findAllByPlatformActorProgramAndDates(String platformIdentifier, String personEmail,
            String programIdentifier, OffsetDateTime start, OffsetDateTime end) {
        return this.eventRepository.findAllByPlatformActorProgramAndDates(platformIdentifier, personEmail,
                programIdentifier, start, end);
    }

    public List<Event> findCreatedOrModifiedAfter(OffsetDateTime after) {
        return this.eventRepository.findByCreatedOrModifiedAfter(after);
    }

    public String findUuidByToolActionProc(String toolCategory, String tool, String process, String action) {
        //String result = eventRepository.findUUIDByToolActionProc(toolCategory, tool, process, action);
        String result = eventRepository.findUUIDByToolActionProc(tool, process, action);
        String prefix = "ears:sev::";
        return (result == null) ? prefix + UUID.randomUUID().toString() : result;
    }

    public Event save(EventDTO eventDTO) {
        OffsetDateTime serverTime = Instant.now().atOffset(ZoneOffset.UTC);
        if (env.getProperty("ears.read-only") == null || !env.getProperty("ears.read-only").equals("false")) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        if (eventDTO.getActor() == null) {
            throw new IllegalArgumentException("Event must have an actor.");
        }
        if (eventDTO.getActor().getEmail() == null) {
            throw new IllegalArgumentException("Actor must have an email adress.");
        }
        if (eventDTO.getEventDefinitionId() == null || eventDTO.getEventDefinitionId().equals("")) {
            throw new IllegalArgumentException("Event must have an eventDefinitionId.");
        }
        if (eventDTO.getToolCategory() == null) {
            throw new IllegalArgumentException("Event must have a toolCategory.");
        }
        if (eventDTO.getTool() == null) {
            throw new IllegalArgumentException("Event must have a tool.");
        }
        if (eventDTO.getAction() == null) {
            throw new IllegalArgumentException("Event must have an action.");
        }
        if (eventDTO.getProcess() == null) {
            throw new IllegalArgumentException("Event must have a process.");
        }
        if (eventDTO.getIdentifier() != null && eventDTO.getTimeStamp() == null) {
            throw new IllegalArgumentException("An event that will be modified must have a timeStamp.");
        }
        if (eventDTO.getPlatform() == null || eventDTO.getPlatform().equals("")) {
            throw new IllegalArgumentException("Event must have a platform.");
        }
        if (eventDTO.getProgram() == null || eventDTO.getProgram().equals("")) {
            throw new IllegalArgumentException("Event must have a program.");
        }
        try {
            Event event = new Event();
            event.setEventDefinitionId(eventDTO.getEventDefinitionId());
            String identifier = eventDTO.getIdentifier();

            boolean drift = false;
            if (identifier == null) { // it is brand new
                identifier = UUID.randomUUID().toString();
                event.setCreationTime(serverTime);
                OffsetDateTime dtoTime = eventDTO.getTimeStamp();
                if (dtoTime == null) { // if it has no time, we add the one from the acquisition
                    Navigation last = null;
                    // Navigation last = navUtil != null ? navUtil.findLast() : null;
                    // for now, do not take the acquisition time, always the server time.
                    if (last != null) {
                        OffsetDateTime acquisitionTime = last.getTime();// .atOffset(ZoneOffset.UTC);
                        log.log(Level.INFO, "acquisition time:" + acquisitionTime.toString());
                        log.log(Level.INFO, "server time: " + serverTime.toString());
                        log.log(Level.INFO, "event timestamp: none given");
                        Duration acquisitiondrift = Duration.between(acquisitionTime, serverTime); // positive if server
                                                                                                   // ahead of
                                                                                                   // acquisition,
                                                                                                   // negative if
                                                                                                   // acquisition ahead
                                                                                                   // of server
                        long acquisitionDiff = acquisitiondrift.toMinutes();
                        if (acquisitionTime == null || acquisitionDiff > 2) { // if the acquisition is null or lagging
                                                                              // behind server for more than 2 minutes,
                                                                              // take the server time
                            eventDTO.setTimeStamp(serverTime);
                            drift = true;
                        } else {// if the server is lagging behind acquisition, or equal, take the acquisition
                            eventDTO.setTimeStamp(acquisitionTime);
                        }
                    } else {
                        // log.log(Level.INFO, "acquisition time: null (last=null)");
                        log.log(Level.INFO, "server time: " + serverTime.toString());
                        log.log(Level.INFO, "event timestamp: none given");
                        eventDTO.setTimeStamp(serverTime);
                    }
                }
            } else { // it has an identifier, so it might be a modification OR come from another EARS
                     // instqace.
                Event existingEvent = eventRepository.findByIdentifier(identifier); // it's an existing event, so a
                                                                                    // modification
                if (existingEvent != null) {
                    event.setId(existingEvent.getId());
                    event.setModificationTime(Instant.now().atOffset(ZoneOffset.UTC));
                } else {
                    // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You tried modifying
                    // an event with identifier " + identifier + " but no such event exists.");
                }
            }
            event.setTimeStamp(eventDTO.getTimeStamp());
            event.setIdentifier(identifier);

            // Navigation last = navigationService.findLast();
            // last.getTimeStamp()
            LinkedDataTerm action = ldtService.findOrCreate(eventDTO.getAction());
            LinkedDataTerm process = ldtService.findOrCreate(eventDTO.getProcess());
            LinkedDataTerm subject = ldtService.findOrCreate(eventDTO.getSubject());
            LinkedDataTerm toolCategory = ldtService.findOrCreate(eventDTO.getToolCategory());
            LinkedDataTerm toolLdTerm = ldtService.findOrCreate(eventDTO.getTool().tool);
            LinkedDataTerm parentToolLdTerm = ldtService.findOrCreate(eventDTO.getTool().parentTool);
            try {
                Tool tool = new Tool(eventDTO.getTool()); // create a tool from the DTO

                toolLdTerm.setTransitiveIdentifier(eventDTO.getTool().tool.transitiveIdentifier);
                if (parentToolLdTerm != null && eventDTO.getTool().parentTool != null) {
                    parentToolLdTerm.setTransitiveIdentifier(eventDTO.getTool().parentTool.transitiveIdentifier);
                }
                tool.setTerm(toolLdTerm); // add the linkeddataterm to it
                tool.setParentTool(parentToolLdTerm); // add the parent linkeddataterm to it
                tool = toolService.findOrCreate(tool); // replace it with a managed entity, either by finding it or creating
                                                       // it.

                Platform platform = platformService.findByIdentifier(eventDTO.getPlatform());
                if (platform == null) {
                    throw new IllegalArgumentException("Provided platform " + eventDTO.getPlatform()
                            + " not found in EARS. Please use the appropriate identifier from the C17 vocabulary, eg. SDN:C17::11BU");
                }
                event.setPlatform(platform);
                Person actor = null;
                if (eventDTO.getActor() != null) {
                    Organisation organisation = organisationService
                            .findByIdentifier(eventDTO.getActor().getOrganisation());
                    actor = new Person(eventDTO.getActor().getFirstName(), eventDTO.getActor().getLastName(),
                            organisation,
                            null, null, eventDTO.getActor().getEmail());
                    actor = personService.findOrCreate(actor);
                }

                Collection<Property> properties = new ArrayList<>();
                if (eventDTO.getProperties() != null) {
                    for (PropertyDTO propertyDTO : eventDTO.getProperties()) {
                        LinkedDataTerm propertyLdTerm = new LinkedDataTerm(propertyDTO.key.identifier,
                                propertyDTO.key.transitiveIdentifier, propertyDTO.key.name);
                        propertyLdTerm = ldtService.findOrCreate(propertyLdTerm); // replace it with a managed one, either
                                                                                  // new or selected.
                        Property property = new Property(propertyLdTerm, propertyDTO.value, propertyDTO.uom);
                        try {
                            propertyService.save(property);
                        } catch (Exception e) {
                            int a = 5;
                        }
                        properties.add(property);
                    }
                }

                Program program = programService.findByIdentifier(eventDTO.getProgram());
                if (program == null) {
                    throw new IllegalArgumentException(
                            "Provided program " + eventDTO.getProgram()
                                    + " not found in EARS. Please create it first.");
                }
                event.setLabel(
                        eventDTO.getLabel() != null && eventDTO.getLabel().equals("") ? null : eventDTO.getLabel());
                event.setStation(
                        eventDTO.getStation() != null && eventDTO.getStation().equals("") ? null
                                : eventDTO.getStation());
                event.setDescription(eventDTO.getDescription() != null && eventDTO.getDescription().equals("") ? null
                        : eventDTO.getDescription());
                event.setAction(action);
                event.setActor(actor);
                event.setProcess(process);
                event.setProgram(program);
                event.setProperties(properties);
                event.setSubject(subject);
                event.setTool(tool);
                event.setToolCategory(toolCategory);
                this.eventRepository.save(event);
                // enrichEventWithAcquisition(event);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            enrichEventWithAcquisition(event);
                            // sendToRemoteServer(event); //TODO: add this to program automated vessel to
                            // shore sending automation
                        } catch (IOException ex) {
                            Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }.start();
            } catch (NullPointerException e) {
                int a = 5;
            }
            return event;

        } catch (Exception ex) {
            Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private void sendToRemoteServer(Event event) {
        String remoteServer = env.getProperty("ears.send-events-to") + "/ears3/api/event";
        if (remoteServer != null && !remoteServer.equals("")) {
            try {
                String json = objectMapper.writeValueAsString(new EventDTO(event));
                HttpClient httpClient = HttpClientBuilder.create().build();

                HttpPost request = new HttpPost(remoteServer);
                StringEntity postingString = new StringEntity(json, "UTF-8");// gson.tojson() converts your pojo to json
                request.setHeader("Content-type", "application/json");
                request.setEntity(postingString);
                HttpResponse response = httpClient.execute(request);
                String body = EntityUtils.toString(response.getEntity(), "UTF-8");
                int status = response.getStatusLine().getStatusCode();
                if (status != 201) {
                    System.out.println("Failure:" + body);
                }

            } catch (IOException ex) {
                Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static final int STALE_DATA_THRESHOLD = 15; // 15 minutes is too old

    private boolean acqDataIsNullTooOldOrUncomparable(Acquisition data, Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event may not be null");
        }
        if (data == null) {
            return true;
        }
        Duration res = Duration.between(data.getTime(), event.getTimeStamp());
        return Math.abs(res.toMinutes()) > STALE_DATA_THRESHOLD;
    }

    private void enrichEventWithAcquisition(Event event) throws IOException {
        Collection<Navigation> navigations = new ArrayList<>();
        Collection<Weather> weathers = new ArrayList<>();
        Collection<Thermosal> thermosals = new ArrayList<>();

        /// boolean tooOld;
        // boolean persistAcquisition = false;
        // Navigation nearestNav = navigationService.findNearest(event.getTimeStamp());
        // log.log(Level.INFO, "Enriching " + event.toString() + ": nearest nav in db: "
        /// + nearestNav);
        // if (acqDataIsNullTooOldOrUncomparable(nearestNav, event)) {//if we don't find
        /// it directly via the database, or if we found it but it is too old, look in
        /// the ears3Nav webservice itself
        Navigation nearestNav = navUtil.findNearest(event.getTimeStamp());
        // log.log(Level.INFO, "Enriching " + event.toString() + ": nearest nav in ws: "
        // + nearestNav);
        // persistAcquisition = true;
        // }
        if (nearestNav != null) {
            // tooOld = acqDataIsNullTooOldOrUncomparable(nearestNav, event);
            // log.log(Level.INFO, "Nearest nav " + (tooOld ? " (too old):" : ":") +
            // nearestNav.toString());
            // if (!tooOld) {
            // if (persistAcquisition) {
            Collection<Event> events = new ArrayList<>();
            events.add(event);
            nearestNav.setEvents(events);
            navigationService.save(nearestNav);
            // }
            navigations.add(nearestNav);
            event.setNavigation(navigations);
        }
        // }
        // persistAcquisition = false;
        // Weather nearestWeather = weatherService.findNearest(event.getTimeStamp());
        // if (acqDataIsNullTooOldOrUncomparable(nearestWeather, event)) {//if we don't
        // find it directly via the database, or if we found it but it is too old, look
        // in the ears3Nav webservice itself
        Weather nearestWeather = weatherUtil.findNearest(event.getTimeStamp()); // find it via the webservices
        // persistAcquisition = true;
        // }
        if (nearestWeather != null) {
            // tooOld = acqDataIsNullTooOldOrUncomparable(nearestWeather, event);
            // log.log(Level.INFO, "Enriching " + event.toString() + ": nearest met" +
            // (tooOld ? " (too old):" : ":") + nearestWeather.toString());
            // if (!tooOld) {
            // if (persistAcquisition) {
            weatherService.save(nearestWeather);
            // }
            weathers.add(nearestWeather);
            event.setWeather(weathers);
        }
        // }
        // persistAcquisition = false;
        // Thermosal nearestThermosal =
        // thermosalService.findNearest(event.getTimeStamp());
        // if (acqDataIsNullTooOldOrUncomparable(nearestThermosal, event)) {//if we
        // don't find it directly via the database, or if we found it but it is too old,
        // look in the ears3Nav webservice itself
        Thermosal nearestThermosal = thermosalUtil.findNearest(event.getTimeStamp());
        // persistAcquisition = true;
        // }
        if (nearestThermosal != null) {
            // tooOld = acqDataIsNullTooOldOrUncomparable(nearestThermosal, event);
            // log.log(Level.INFO, "Enriching " + event.toString() + ": nearest tss" +
            // (tooOld ? " (too old):" : ":") + nearestThermosal.toString());
            // if (!tooOld) {
            // if (persistAcquisition) {
            thermosalService.save(nearestThermosal);
            // }
            thermosals.add(nearestThermosal);
            event.setThermosal(thermosals);
        }
        // }
        this.eventRepository.save(event);
    }

    public void deleteById(Long id) {
        if (env.getProperty("ears.read-only") == null || !env.getProperty("ears.read-only").equals("false")) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        Event event = this.eventRepository.findById(id).orElse(null);
        if (event != null) {
            this.eventRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no event with id " + id);
        }
    }

    public void deleteByIdentifier(String identifier) {
        if (env.getProperty("ears.read-only") == null || !env.getProperty("ears.read-only").equals("false")) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        Event event = this.eventRepository.findByIdentifier(identifier);
        if (event != null) {
            this.eventRepository.deleteByIdentifier(identifier);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no event with identifier " + identifier);
        }

    }

    public void deleteByTimeStampBetween(Date startDate, Date endDate) {
        if (env.getProperty("ears.read-only") == null || !env.getProperty("ears.read-only").equals("false")) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        this.eventRepository.deleteByTimeStampBetween(startDate, endDate);
    }

    public List<Event> findAllByCruiseProgramAndActor(String cruiseIdentifier, String programIdentifier,
            String actorEmail) {
        return this.eventRepository.findAllByCruiseProgramAndActor(cruiseIdentifier, programIdentifier, actorEmail);
    }
}
