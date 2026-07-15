package eu.eurofleets.ears3.controller.rest;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProperty;
import com.opencsv.CSVWriter;
import eu.eurofleets.ears3.domain.*;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.service.EventService;

import java.io.IOException;
import java.io.Writer;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import eu.eurofleets.ears3.utilities.Constants;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController()
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private EventService eventService;

    @Value("${ears.platform}")
    public String platformUrn;

    @GetMapping(value = {"events"}, produces = {MediaType.APPLICATION_JSON_VALUE,
            Constants.APPLICATION_XML_UTF8_VALUE})
    public EventPage getEvents(
            @RequestParam Map<String, String> allParams,
            @PageableDefault(size = 50, sort = "id") Pageable pageable) {
        Page<Event> events = eventService.advancedFind(allParams, pageable);
        return new EventPage(events);
    }

    @GetMapping(value = {"event/{id}"}, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Event getEventById(@PathVariable(value = "id") String id) {
        return this.eventService.findById(Long.parseLong(id));

    }

    @GetMapping(value = {"event"}, params = {"identifier"}, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Event getEventByIdentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        return this.eventService.findByIdentifier(identifier);
    }

    @PostMapping(value = {"event"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Message<EventDTO>> createEvent(@RequestBody EventDTO eventDTO) {
        if (eventDTO.getPlatform() == null) {
            eventDTO.setPlatform(platformUrn);
        }
        Event event = this.eventService.save(eventDTO);
        if (event != null) {
            eventDTO = new EventDTO(event);
            return new ResponseEntity<Message<EventDTO>>(
                    new Message<EventDTO>(HttpStatus.CREATED.value(), event.getIdentifier(), eventDTO),
                    HttpStatus.CREATED);
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Could not create Event.");
        }
    }

    @DeleteMapping(value = {"event"}, params = {"identifier"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String removeEventByIdentifier(@RequestParam(required = true) String identifier) {
        this.eventService.deleteByIdentifier(identifier);
        return "";
    }
}
