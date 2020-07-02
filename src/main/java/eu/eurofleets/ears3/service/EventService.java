package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.Property;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.PropertyDTO;
import eu.eurofleets.ears3.scheduler.SyncScheduler;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public static Logger log = Logger.getLogger(EventService.class.getSimpleName());

    @Autowired
    public EventService(EventRepository eventRepository, ToolRepository toolRepository) {
        this.eventRepository = eventRepository;
    }

    public void save(Event event) {
        //   this.toolRepository.save(event.getTool());
        this.eventRepository.save(event);
    }

    public Event save(EventDTO eventDTO) {
        try {
            Event event = new Event();
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

            Person actor = null;
            if (eventDTO.actor != null) {
                actor = new Person(eventDTO.actor.firstName, eventDTO.actor.lastName, null, null, null, null);
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

            Program program = programService.findById(eventDTO.program);

            event.setAction(action);
            event.setActor(actor);
            event.setEventDefinitionId(eventDTO.eventDefinitionId);
            event.setIdentifier(eventDTO.identifier);
            event.setProcess(process);
            event.setProgram(program);
            event.setProperties(properties);
            event.setSubject(subject);
            event.setTimeStamp(eventDTO.timeStamp);
            event.setTool(tool);
            event.setToolCategory(toolCategory);
            return this.eventRepository.save(event);
        } catch (Exception e) {
            log.log(Level.SEVERE, "exception!", e);
            throw e;
        }
    }

    public Event findById(Long id) {
        return (Event) this.eventRepository.findById(id).orElse(null);
    }

    public Event findByIdentifier(String identifier) {
        return (Event) this.eventRepository.findByIdentifier(identifier);
    }

    public List<Event> findAll() {
        return IterableUtils.toList(this.eventRepository.findAll());
    }

    public Event findById(long eventId) {
        return this.eventRepository.findById(eventId).orElse(null);
    }

    /* public Event getEventByDate(Date date) {
        return this.eventRepository.findFirstByTimeStampBeforeOrderByTimeStampDesc(date);
    }

    public List<Event> getEventBetweenDates(Date startDate, Date endDate) {
        return this.eventRepository.findByTimeStampBetween(startDate, endDate);
    }*/
    public void deleteById(String id) {
        this.eventRepository.deleteById(Long.valueOf(id));
    }

    public void deleteByTimeStampBetween(Date startDate, Date endDate) {
        this.eventRepository.deleteByTimeStampBetween(startDate, endDate);
    }

    public List<Event> findByTimeStampBetween(OffsetDateTime startDate, OffsetDateTime endDate) {
        return this.eventRepository.getByTimeStampBetween(startDate, endDate);
    }

    public List<Event> findByCruise(Cruise cruise) {
        return findByTimeStampBetween(cruise.getStartDate(), cruise.getEndDate());
    }

    public List<Event> findByTool(Tool tool) {
        return this.eventRepository.findByTool(tool.getTerm().getIdentifier());
    }

    public void save(String uuid, Event event) {

        this.eventRepository.save(event);
    }

    public void insertTool(Tool tool) {
//        this.toolRepository.save(tool);
    }
}
