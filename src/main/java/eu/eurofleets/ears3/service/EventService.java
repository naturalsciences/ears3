package eu.eurofleets.ears3.service;

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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.IterableUtils;
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

    private DatagramUtilities<Navigation> navUtil;
    private DatagramUtilities<Thermosal> thermosalUtil;
    private DatagramUtilities<Weather> weatherUtil;
    public static Logger log = Logger.getLogger(EventService.class.getSimpleName());

    @Autowired
    private final Environment env;

    @Autowired
    public EventService(EventRepository eventRepository, Environment env) throws MalformedURLException {
        this.eventRepository = eventRepository;
        this.env = env;
        String navigationServer = env.getProperty("ears.navigation.server");

        navUtil = new DatagramUtilities<>(Navigation.class, navigationServer);
        thermosalUtil = new DatagramUtilities<>(Thermosal.class, navigationServer);
        weatherUtil = new DatagramUtilities<>(Weather.class, navigationServer);
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
        if (startDate != null) {
            start = OffsetDateTime.parse(startDate);
        }
        if (endDate != null) {
            end = OffsetDateTime.parse(endDate);
        }

        List<Event> res = null;
        if (platformIdentifier == null && programIdentifier == null && actorEmail == null && start == null && end == null && cruiseIdentifier == null) {
            res = this.findAll();
        } else if (programIdentifier == null && actorEmail == null && start == null && end == null && cruiseIdentifier == null) {
            res = this.findAllByPlatformCode(platformIdentifier);
        } else if (cruiseIdentifier == null) {
            if (start != null && end != null) {
                res = this.findAllByPlatformActorProgramAndDates(platformIdentifier, actorEmail, programIdentifier, start, end);
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

    public List<Event> findAllByPlatformActorAndProgram(String platformIdentifier, String personEmail, String programIdentifier) {
        return this.eventRepository.findAllByPlatformActorAndProgram(platformIdentifier, personEmail, programIdentifier);
    }

    public List<Event> findAllByPlatformActorProgramAndDates(String platformIdentifier, String personEmail, String programIdentifier, OffsetDateTime start, OffsetDateTime end) {
        return this.eventRepository.findAllByPlatformActorProgramAndDates(platformIdentifier, personEmail, programIdentifier, start, end);
    }

    public List<Event> findCreatedOrModifiedAfter(OffsetDateTime after) {
        return this.eventRepository.findByCreatedOrModifiedAfter(after);
    }

    public Event save(EventDTO eventDTO) {
        if (env.getProperty("ears.read-only") == null || !env.getProperty("ears.read-only").equals("false")) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        if (eventDTO.actor == null) {
            throw new IllegalArgumentException("Event must have an actor.");
        }
        if (eventDTO.actor.email == null) {
            throw new IllegalArgumentException("Actor must have an email adress.");
        }
        if (eventDTO.eventDefinitionId == null) {
            throw new IllegalArgumentException("Event must have an eventDefinitionId.");
        }
        if (eventDTO.toolCategory == null) {
            throw new IllegalArgumentException("Event must have a toolCategory.");
        }
        if (eventDTO.tool == null) {
            throw new IllegalArgumentException("Event must have a tool.");
        }
        if (eventDTO.action == null) {
            throw new IllegalArgumentException("Event must have an action.");
        }
        if (eventDTO.process == null) {
            throw new IllegalArgumentException("Event must have a process.");
        }
        if (eventDTO.identifier != null && eventDTO.timeStamp == null) {
            throw new IllegalArgumentException("An event that will be modified must have a timeStamp.");
        }
        if (eventDTO.platform == null) {
            throw new IllegalArgumentException("Event must have a platform.");
        }
        if (eventDTO.program == null) {
            throw new IllegalArgumentException("Event must have a program.");
        }
        try {
            Event event = new Event();
            event.setEventDefinitionId(eventDTO.eventDefinitionId);
            String identifier = eventDTO.identifier;
            OffsetDateTime serverTime = Instant.now().atOffset(ZoneOffset.UTC);
            boolean drift = false;
            if (identifier == null) { //it is brand new
                identifier = UUID.randomUUID().toString();
                event.setCreationTime(Instant.now().atOffset(ZoneOffset.UTC));
                OffsetDateTime dtoTime = eventDTO.getTimeStamp();
                if (dtoTime == null) { //if it has no time, we add the one from the acquisition
                    Navigation last = navUtil != null ? navUtil.findLast() : null;
                    if (last != null) {
                        OffsetDateTime acquisitionTime = last.getTime();//.atOffset(ZoneOffset.UTC);
                        System.out.println("acquisition time:" + acquisitionTime.toString());
                        System.out.println("server time: " + serverTime.toString());
                        System.out.println("event timestamp: none given");
                        Duration acquisitiondrift = Duration.between(acquisitionTime, serverTime); //positive if server ahead of acquisition, negative if acquisition ahead of server
                        long acquisitionDiff = acquisitiondrift.toMinutes();
                        if (acquisitionTime == null || acquisitionDiff > 2) { //if the acquisition is null or lagging behind server for more than 2 minutes, take the server time
                            eventDTO.setTimeStamp(serverTime);
                            drift = true;
                        } else {//if the server is lagging behind acquisition, or equal, take the acquisition
                            eventDTO.setTimeStamp(acquisitionTime);
                        }
                    } else {
                        System.out.println("acquisition time: null (last=null)");
                        System.out.println("server time: " + serverTime.toString());
                        System.out.println("event timestamp: none given");
                        eventDTO.setTimeStamp(serverTime);
                    }
                }
            } else { //it might already exist, and is a modification
                Event existingEvent = eventRepository.findByIdentifier(identifier);
                if (existingEvent != null) {
                    event.setId(existingEvent.getId());
                    event.setModificationTime(Instant.now().atOffset(ZoneOffset.UTC));
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You tried modifying an event with identifier " + identifier + " but no such event exists.");
                }
            }
            event.setTimeStamp(eventDTO.getTimeStamp());
            event.setIdentifier(identifier);

            //Navigation last = navigationService.findLast();
            // last.getTimeStamp()
            LinkedDataTerm action = ldtService.findOrCreate(eventDTO.action);
            LinkedDataTerm process = ldtService.findOrCreate(eventDTO.process);
            LinkedDataTerm subject = ldtService.findOrCreate(eventDTO.subject);
            LinkedDataTerm toolCategory = ldtService.findOrCreate(eventDTO.toolCategory);
            LinkedDataTerm toolLdTerm = ldtService.findOrCreate(eventDTO.tool.tool);
            LinkedDataTerm parentToolLdTerm = ldtService.findOrCreate(eventDTO.tool.parentTool);
            Tool tool = new Tool(eventDTO.tool); //create a tool from the DTO
            toolLdTerm.setTransitiveIdentifier(eventDTO.tool.tool.transitiveIdentifier);
            if (parentToolLdTerm != null && eventDTO.tool.parentTool != null) {
                parentToolLdTerm.setTransitiveIdentifier(eventDTO.tool.parentTool.transitiveIdentifier);
            }
            tool.setTerm(toolLdTerm); //add the linkeddataterm to it
            tool.setParentTool(parentToolLdTerm); //add the parent linkeddataterm to it
            tool = toolService.findOrCreate(tool); //replace it with a managed entity, either by finding it or creating it.

            Platform platform = platformService.findByIdentifier(eventDTO.platform);
            event.setPlatform(platform);
            Person actor = null;
            if (eventDTO.actor != null) {
                Organisation organisation = organisationService.findByIdentifier(eventDTO.actor.organisation);
                actor = new Person(eventDTO.actor.firstName, eventDTO.actor.lastName, organisation, null, null, eventDTO.actor.email);
                actor = personService.findOrCreate(actor);
            }

            Collection<Property> properties = new ArrayList<>();
            if (eventDTO.properties != null) {
                for (PropertyDTO propertyDTO : eventDTO.properties) {
                    LinkedDataTerm propertyLdTerm = new LinkedDataTerm(propertyDTO.key.identifier, propertyDTO.key.transitiveIdentifier, propertyDTO.key.name);
                    propertyLdTerm = ldtService.findOrCreate(propertyLdTerm); //replace it with a managed one, either new or selected.
                    Property property = new Property(propertyLdTerm, propertyDTO.value, propertyDTO.uom);
                    propertyService.save(property);
                    properties.add(property);
                }
            }

            Program program = programService.findByIdentifier(eventDTO.program);
            event.setLabel(eventDTO.label != null && eventDTO.label.equals("") ? null : eventDTO.label);
            event.setAction(action);
            event.setActor(actor);
            event.setProcess(process);
            event.setProgram(program);
            event.setProperties(properties);
            event.setSubject(subject);
            event.setTool(tool);
            event.setToolCategory(toolCategory);
            //enrichEventWithAcquisition(event);
            new Thread() {
                @Override
                public void run() {
                    try {
                        enrichEventWithAcquisition(event);
                    } catch (IOException ex) {
                        Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();

            return this.eventRepository.save(event);
        } catch (Exception ex) {
            Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static final int STALE_DATA_THRESHOLD = 15; //15 minutes is too old

    private boolean dataIsTooOldOrUncomparable(Acquisition data, Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event may not be null");
        }
        if (data == null) {
            return true;
        }
        Duration res = Duration.between(data.getTimeStamp(), event.getTimeStamp());
        return Math.abs(res.toMinutes()) > STALE_DATA_THRESHOLD;
    }

    private void enrichEventWithAcquisition(Event event) throws IOException {
        Collection<Navigation> navigations = new ArrayList<>();
        Collection<Weather> weathers = new ArrayList<>();
        Collection<Thermosal> thermosals = new ArrayList<>();

        boolean tooOld;
        boolean persistAcquisition = false;
        Navigation nearestNav = navigationService.findNearest(event.getTimeStamp());
        if (dataIsTooOldOrUncomparable(nearestNav, event)) {//if we don't find it directly via the database, or if we found it but it is too old, look in the ears3Nav webservice itself
            nearestNav = navUtil.findNearest(event.getTimeStamp());
            persistAcquisition = true;
        }
        if (nearestNav != null) {
            tooOld = dataIsTooOldOrUncomparable(nearestNav, event);
            System.out.println("Found for event " + event.toString() + " nearest navigation" + (tooOld ? " (too old):" : ":") + nearestNav.toString());
            if (!tooOld) {
                if (persistAcquisition) {
                    navigationService.save(nearestNav);
                }
                navigations.add(nearestNav);
                event.setNavigation(navigations);
            }
        }
        persistAcquisition = false;
        Weather nearestWeather = weatherService.findNearest(event.getTimeStamp());
        if (dataIsTooOldOrUncomparable(nearestWeather, event)) {//if we don't find it directly via the database, or if we found it but it is too old, look in the ears3Nav webservice itself
            nearestWeather = weatherUtil.findNearest(event.getTimeStamp());
            persistAcquisition = true;
        }
        if (nearestWeather != null) {
            tooOld = dataIsTooOldOrUncomparable(nearestWeather, event);
            System.out.println("Found for event " + event.toString() + " nearest weather" + (tooOld ? " (too old):" : ":") + nearestWeather.toString());
            if (!tooOld) {
                if (persistAcquisition) {
                    weatherService.save(nearestWeather);
                }
                weathers.add(nearestWeather);
                event.setWeather(weathers);
            }
        }
        persistAcquisition = false;
        Thermosal nearestThermosal = thermosalService.findNearest(event.getTimeStamp());
        if (dataIsTooOldOrUncomparable(nearestThermosal, event)) {//if we don't find it directly via the database, or if we found it but it is too old, look in the ears3Nav webservice itself
            nearestThermosal = thermosalUtil.findNearest(event.getTimeStamp());
            persistAcquisition = true;
        }
        if (nearestThermosal != null) {
            tooOld = dataIsTooOldOrUncomparable(nearestThermosal, event);
            System.out.println("Found for event " + event.toString() + " nearest thermosal" + (tooOld ? " (too old):" : ":") + nearestThermosal.toString());
            if (!tooOld) {
                if (persistAcquisition) {
                    thermosalService.save(nearestThermosal);
                }
                thermosals.add(nearestThermosal);
                event.setThermosal(thermosals);
            }
        }
        this.eventRepository.save(event);
    }

    public void deleteById(Long id) {
        Event event = this.eventRepository.findById(id).orElse(null);
        if (event != null) {
            this.eventRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no event with id " + id);
        }
    }

    public void deleteByIdentifier(String identifier) {
        Event event = this.eventRepository.findByIdentifier(identifier);
        if (event != null) {
            this.eventRepository.deleteByIdentifier(identifier);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no event with identifier " + identifier);
        }

    }

    public void deleteByTimeStampBetween(Date startDate, Date endDate) {
        this.eventRepository.deleteByTimeStampBetween(startDate, endDate);
    }

    public List<Event> findAllByCruiseProgramAndActor(String cruiseIdentifier, String programIdentifier, String actorEmail) {
        return this.eventRepository.findAllByCruiseProgramAndActor(cruiseIdentifier, programIdentifier, actorEmail);
    }
}
