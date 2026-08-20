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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final String navServer;
    private final Boolean readOnly;

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
    public EventService(EventRepository eventRepository,
                        @Value("${app.navigation.server}") String navServer,
                        @Value("${app.read-only}") Boolean readOnly) {
        this.eventRepository = eventRepository;
        this.navServer = navServer;
        this.readOnly = readOnly;
        try {
            navUtil = new DatagramUtilities<>(Navigation.class, navServer);
            thermosalUtil = new DatagramUtilities<>(Thermosal.class, navServer);
            weatherUtil = new DatagramUtilities<>(Weather.class, navServer);
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
        if (get == null || get.trim().isEmpty()) {
            return null;
        } else {
            return get;
        }
    }

    // Sentinel bounds used when only one of startDate/endDate is given.
// OffsetDateTime.MIN/MAX overflow the DB column's range and throw - see resolveDateRange.
// Fixed at midnight UTC rather than "now" - the exact time-of-day has no
// bearing on a "far enough in the past/future" bound, so there's no reason
// for it to vary per-request.
    private static final OffsetDateTime EARLY_SENTINEL =
            OffsetDateTime.of(LocalDate.parse("1900-01-01"), LocalTime.MIDNIGHT, ZoneOffset.UTC);
    private static final OffsetDateTime LATE_SENTINEL =
            OffsetDateTime.of(LocalDate.parse("2100-01-01"), LocalTime.MIDNIGHT, ZoneOffset.UTC);

    private record DateRange(OffsetDateTime start, OffsetDateTime end) {
    }

    /**
     * Parses the optional startDate/endDate params. If only one is given, the
     * other is filled in with a sentinel far enough in the past/future to be a
     * no-op bound. Returns (null, null) if neither is given.
     */
    private DateRange resolveDateRange(String startDate, String endDate) {
        if (startDate == null && endDate == null) {
            return new DateRange(null, null);
        }
        OffsetDateTime start = startDate != null ? OffsetDateTime.parse(startDate) : EARLY_SENTINEL;
        OffsetDateTime end = endDate != null ? OffsetDateTime.parse(endDate) : LATE_SENTINEL;
        return new DateRange(start, end);
    }

    public Page<Event> advancedFind(Map<String, String> allParams, Pageable pageable) {
        if (allParams.isEmpty()) {
            return this.findAll(pageable);
        }
        String platformIdentifier = sanitizeParam(allParams, "platformIdentifier");
        String cruiseIdentifier = sanitizeParam(allParams, "cruiseIdentifier");
        String programIdentifier = sanitizeParam(allParams, "programIdentifier");
        String actorEmail = sanitizeParam(allParams, "actorEmail");

        String station = sanitizeParam(allParams, "station");
        String freeSearch = sanitizeParam(allParams, "search");

        String label = freeSearch;
        String description = freeSearch;

        if (platformIdentifier == null) {
            platformIdentifier = "SDN:C17::11BU"; //TODO get from settings
        }

        DateRange dateRange = resolveDateRange(
                sanitizeParam(allParams, "startDate"),
                sanitizeParam(allParams, "endDate"));
        OffsetDateTime start = dateRange.start();
        OffsetDateTime end = dateRange.end();

        boolean hasCruise = cruiseIdentifier != null;

        boolean hasProgram = programIdentifier != null;
        boolean hasActor = actorEmail != null;
        boolean hasDates = start != null;

        boolean hasText = station != null || freeSearch != null;

        if (hasCruise) {
            // A platform and time period are implicit when searching by cruise.
            return this.findAllByCruiseProgramActor(cruiseIdentifier, programIdentifier, actorEmail, label, station, description, pageable);
        }
        if (hasText && !hasProgram && !hasActor && !hasDates) {
            return this.findByText(freeSearch, station, freeSearch, pageable);
        }
        if (hasDates) {
            return this.findAllByPlatformActorProgramDates(platformIdentifier, actorEmail, programIdentifier, start, end, label, station, description, pageable);
        } else {
            return this.findAllByPlatformActorProgram(platformIdentifier, actorEmail, programIdentifier, label, station, description, pageable);
        }
    }

    private Page<Event> findByText(String label, String station, String description, Pageable pageable) {
        return this.eventRepository.findByText(label, station, description, pageable);
    }

    public Page<Event> findAll(Pageable pageable) {
        return this.eventRepository.findAll(pageable);
    }

    public Page<Event> findByTimeStampBetween(OffsetDateTime startDate, OffsetDateTime endDate, Pageable pageable) {
        return this.eventRepository.findByTimeStampBetween(startDate, endDate, pageable);
    }

    public Page<Event> findAllByPlatformActorProgram(String platformIdentifier, String personEmail,
                                                     String programIdentifier, String label, String station, String description, Pageable pageable) {
        return this.eventRepository.findAllByPlatformActorProgram(platformIdentifier, personEmail,
                programIdentifier, label, station, description, pageable);
    }

    public Page<Event> findAllByPlatformActorProgramDates(String platformIdentifier, String personEmail,
                                                          String programIdentifier, OffsetDateTime start, OffsetDateTime end, String label, String station, String description, Pageable pageable) {
        return this.eventRepository.findAllByPlatformActorProgramDates(platformIdentifier, personEmail,
                programIdentifier, start, end, label, station, description, pageable);
    }


    public Page<Event> findAllByCruiseProgramActor(String cruiseIdentifier, String programIdentifier,
                                                   String actorEmail, String label, String station, String description, Pageable pageable) {
//        for (Event e : result.getContent()) {
//
//            Optional<Cruise> cruise = cruiseService.findAtDate(e.getTimeStamp(), platformUrn)
//                    .stream()
//                    .findFirst();
//            if (cruise.isPresent()) {
//                e.setCruise(cruise.get());
//            }
//
//        }
        return this.eventRepository.findAllByCruiseProgramActor(cruiseIdentifier, programIdentifier, actorEmail, label, station, description, pageable);
    }

    public String findUuidByToolActionProc(String toolCategory, String tool, String process, String action) {
        //String result = eventRepository.findUUIDByToolActionProc(toolCategory, tool, process, action);
        String result = eventRepository.findUUIDByToolActionProc(tool, process, action);
        String prefix = "ears:sev::";
        return (result == null) ? prefix + UUID.randomUUID().toString() : result;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Event save(EventDTO eventDTO) {
        OffsetDateTime serverTime = Instant.now().atOffset(ZoneOffset.UTC);
        if (readOnly == null || readOnly) {
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
                // instance.
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

            Tool tool = new Tool(eventDTO.getTool()); // create a tool from the DTO

            toolLdTerm.setTransitiveIdentifier(eventDTO.getTool().tool.transitiveIdentifier);
            if (parentToolLdTerm != null && eventDTO.getTool().parentTool != null) {
                parentToolLdTerm.setTransitiveIdentifier(eventDTO.getTool().parentTool.transitiveIdentifier);
            }
            tool.setTerm(toolLdTerm); // add the linkeddataterm to it
            tool.setParentTool(parentToolLdTerm); // add the parent linkeddataterm to it
            Logger.getLogger(EventService.class.getName()).log(Level.INFO, "NOW trying to retrieve the tool itself");
            tool = toolService.findOrCreate(tool); // replace it with a managed entity, either by finding it or creating it

            Platform platform = platformService.findByIdentifier(eventDTO.getPlatform());
            if (platform == null) {
                throw new IllegalArgumentException("Provided platform " + eventDTO.getPlatform()
                        + " not found in EARS. Please use the appropriate identifier from the C17 vocabulary, eg. SDN:C17::11BU");
            }
            event.setPlatform(platform);
            Person actor = null;
            if (eventDTO.getActor() != null) {
                Organisation organisation = organisationService.findByIdentifier(eventDTO.getActor().getOrganisation());
                actor = new Person(eventDTO.getActor().getFirstName(), eventDTO.getActor().getLastName(),
                        organisation, null, null, eventDTO.getActor().getEmail());
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
            event.setRemarks(eventDTO.getRemarks() != null && eventDTO.getRemarks().isEmpty() ? null
                    : eventDTO.getRemarks());
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
            return event;

        } catch (DataIntegrityViolationException dve) {
            Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, dve);
            String message = (dve.getMessage() != null) ? dve.getMessage() : dve.toString();
            throw new DataIntegrityViolationException(message, dve.getMostSpecificCause());
        } /*catch (Exception ex) {
            Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
            String message = (ex.getMessage() != null) ? ex.getMessage() : ex.toString();
            return null;
        }*/
    }

    /* private void sendToRemoteServer(Event event) {
        //TODO
        String remoteServer = env.getProperty("app.send-events-to") + "/ears3/api/event";
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
    } */

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
        if (readOnly == null || readOnly) {
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
        if (readOnly == null || readOnly) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        Event event = this.eventRepository.findByIdentifier(identifier);
        if (event != null) {
            this.eventRepository.deleteByIdentifier(identifier);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no event with identifier " + identifier);
        }

    }

    /*No longer used*/
    public void deleteByTimeStampBetween(Date startDate, Date endDate) {
        if (readOnly == null || readOnly) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        this.eventRepository.deleteByTimeStampBetween(startDate, endDate);
    }

    public Page<Event> findCreatedOrModifiedAfter(OffsetDateTime after, Pageable pageable) {
        return this.eventRepository.findByCreatedOrModifiedAfter(after, pageable);
    }

    public Page<Event> findByCruise(Cruise cruise, Pageable pageable) {
        return findByTimeStampBetween(cruise.getStartDate(), cruise.getEndDate(), pageable);
    }

    public Page<Event> findByCruise(String cruiseIdentifier, Pageable pageable) {
        Assert.notNull(cruiseIdentifier, "Cruise identifier code must not be null");
        return this.eventRepository.findByCruise(cruiseIdentifier, pageable);
    }

    public Page<Event> findByTool(Tool tool, Pageable pageable) {
        return this.eventRepository.findByTool(tool.getTerm().getIdentifier(), pageable);
    }

    public Page<Event> findAllByPlatformCode(String platformIdentifier, Pageable pageable) {
        Assert.notNull(platformIdentifier, "Platform code must not be null");
        return this.eventRepository.findByPlatformCode(platformIdentifier, pageable);
    }
}
