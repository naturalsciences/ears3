package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.utilities.DatagramUtilities;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.EventList;
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
import eu.eurofleets.ears3.dto.EventDTOList;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.PropertyDTO;
import eu.eurofleets.ears3.dto.ToolDTO;
import eu.eurofleets.ears3.scheduler.vocabulary.SyncScheduler;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
    public EventService(EventRepository eventRepository, Environment env) {
        this.eventRepository = eventRepository;
        this.env = env;
        try {
            navUtil = new DatagramUtilities<>(Navigation.class, env.getProperty("ears.navigation.server"));
            thermosalUtil = new DatagramUtilities<>(Thermosal.class, env.getProperty("ears.navigation.server"));
            weatherUtil = new DatagramUtilities<>(Weather.class, env.getProperty("ears.navigation.server"));
        } catch (MalformedURLException e) {
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

    public List<Event> findAllByPlatformCode(String code) {
        Assert.notNull(code, "Platform code must not be null");
        return this.eventRepository.findByPlatformCode(code);
    }

    public List<Event> findAllByPlatformActorAndProgram(String platformIdentifier, String personEmail, String programIdentifier) {
        return this.eventRepository.findByPlatformActorAndProgram(platformIdentifier, personEmail, programIdentifier);
    }

    public List<Event> findCreatedOrModifiedAfter(OffsetDateTime after) {
        return this.eventRepository.findByCreatedOrModifiedAfter(after);
    }

    public void save(Event event) {
        this.eventRepository.save(event);
    }

    public Event save(EventDTO eventDTO) {
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
        if (eventDTO.timeStamp == null) {
            throw new IllegalArgumentException("Event must have a timeStamp.");
        }
        if (eventDTO.platform == null) {
            throw new IllegalArgumentException("Event must have a platform.");
        }
        try {
            Event event = new Event();
            event.setEventDefinitionId(eventDTO.eventDefinitionId);
            String identifier = eventDTO.identifier;
            if (identifier == null) { //it is brand new
                identifier = UUID.randomUUID().toString();
                event.setCreationTime(Instant.now().atOffset(ZoneOffset.UTC));
                try {
                    Navigation last = navUtil.last();
                    if (last != null) {
                        event.setTimeStamp(last.getInstrumentTime().atOffset(ZoneOffset.UTC));
                    }
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Couldn't reach the acquisition server. Timestamp not changed.", e);
                }

            } else { //it might already exist
                Event existingEvent = eventRepository.findByIdentifier(identifier);
                if (existingEvent != null) {
                    event.setId(existingEvent.getId());
                    event.setModificationTime(Instant.now().atOffset(ZoneOffset.UTC));
                } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You tried modifying an event with identifier " + identifier + " but no such event exists.");
                }
            }
            event.setIdentifier(identifier);

            LinkedDataTerm action = ldtService.findOrCreate(eventDTO.action);
            LinkedDataTerm process = ldtService.findOrCreate(eventDTO.process);
            LinkedDataTerm subject = ldtService.findOrCreate(eventDTO.subject);
            LinkedDataTerm toolCategory = ldtService.findOrCreate(eventDTO.toolCategory);
            LinkedDataTerm toolLdTerm = ldtService.findOrCreate(eventDTO.tool.tool);
            LinkedDataTerm parentToolLdTerm = ldtService.findOrCreate(eventDTO.tool.parentTool);
            Tool tool = new Tool(eventDTO.tool); //create a tool from the DTO
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
                    LinkedDataTerm propertyLdTerm = new LinkedDataTerm(propertyDTO.key.identifier, null, propertyDTO.key.name);
                    propertyLdTerm = ldtService.findOrCreate(propertyLdTerm); //replace it with a managed one, either new or selected.
                    Property property = new Property(propertyLdTerm, propertyDTO.value, propertyDTO.uom);
                    propertyService.save(property);
                    properties.add(property);
                }
            }

            Program program = programService.findByIdentifier(eventDTO.program);
            event.setAction(action);
            event.setActor(actor);
            event.setProcess(process);
            event.setProgram(program);
            event.setProperties(properties);
            event.setSubject(subject);
            event.setTimeStamp(eventDTO.timeStamp);
            event.setTool(tool);
            event.setToolCategory(toolCategory);

            Collection<Navigation> navigations = new ArrayList<>();
            Collection<Weather> weathers = new ArrayList<>();
            Collection<Thermosal> thermosals = new ArrayList<>();
            try {
                Navigation nearestNav = navUtil.nearest(event.getTimeStamp());
                navigations.add(nearestNav);
                navigationService.saveAll(navigations);

                Weather nearestWeather = weatherUtil.nearest(event.getTimeStamp());
                weathers.add(nearestWeather);
                weatherService.saveAll(weathers);

                Thermosal nearestThermosal = thermosalUtil.nearest(event.getTimeStamp());
                thermosals.add(nearestThermosal);
                thermosalService.saveAll(thermosals);
            } catch (IOException e) {
                log.log(Level.SEVERE, "Couldn't reach the acquisition server", e);
            }

            event.setNavigation(navigations);
            event.setWeather(weathers);
            event.setThermosal(thermosals);
            return this.eventRepository.save(event);
        } catch (Exception e) {
            log.log(Level.SEVERE, "An exception was thrown while creating/modifying this event", e);
            throw e;
        }
    }

    public void deleteById(String id) {
        this.eventRepository.deleteById(Long.valueOf(id));
    }

    public void deleteByIdentifier(String identifier) {
        this.eventRepository.deleteByIdentifier(identifier);
    }

    public void deleteByTimeStampBetween(Date startDate, Date endDate) {
        this.eventRepository.deleteByTimeStampBetween(startDate, endDate);
    }
}
