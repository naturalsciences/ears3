package eu.eurofleets.ears3.controller.rest;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
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
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.excel.SpreadsheetEvent;
import eu.eurofleets.ears3.service.EventExcelService;
import eu.eurofleets.ears3.service.EventService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.github.rushuat.ocell.document.Documents;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import io.github.rushuat.ocell.document.Document;
import eu.eurofleets.ears3.service.EventExcelService.ErrorRow;

@RestController()
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventExcelInputController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private EventService eventService;

    @Autowired
    private EventExcelService eventExcelService;

    @Autowired
    private Environment env;

    @PostMapping(value = { "event" },
              produces = { "application/xml; charset=utf-8", "application/json" },
              consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Message<List<ErrorRow>>> createEvent(@RequestHeader("person") PersonDTO actor,
                                                               @RequestParam("file") MultipartFile mpFile) {
        List<ErrorRow> errorList = new ArrayList<>();
        try (Document document = Documents.OOXML().create()) {
            //MultipartFile->Stream stream
            byte [] byteArr = mpFile.getBytes();
            InputStream inputStream = new ByteArrayInputStream(byteArr);
            //Document document=io.github.rushuat.ocell.document.Document.fromStream(stream);
            document.fromStream(inputStream);
            //validateAllTabs();
            boolean areTabsOk = eventExcelService.validateAllTabs(document);
            //validateHeaders();
            String sheetName = "events";
            boolean areHeadersOk = eventExcelService.validateHeaders(document, sheetName);
            List<SpreadsheetEvent> data = document.getSheet(sheetName, SpreadsheetEvent.class);
            List<EventDTO> events = new ArrayList<>();
            boolean processProblems = eventExcelService.processSpreadsheetEvents(errorList, data, events);
            boolean saveProblems = false;
            if (!processProblems) { saveProblems = eventExcelService.saveSpreadsheetEvents(errorList, events); }
            if ( processProblems || saveProblems ) {
                Message<List<ErrorRow>> msg = new Message<>(HttpStatus.CONFLICT.value(),"Error Creating Excel Event", errorList);
                return new ResponseEntity<>(msg, HttpStatus.CONFLICT );
            }
        } catch (IOException e) {
            e.printStackTrace();
            Message<List<ErrorRow>> msg = new Message<>(HttpStatus.CONFLICT.value(),"Error Creating Excel Event", errorList);
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT );
        }
        Message<List<ErrorRow>> msg = new Message<>(HttpStatus.CREATED.value(),"Successfully created", errorList);
        return new ResponseEntity<>(msg, HttpStatus.CREATED) ;
    }

    /* @PostMapping(value = { "event" }, produces = { "application/xml; charset=utf-8", "application/json" })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Message<EventDTO>> createEvent(@RequestBody EventDTO eventDTO) {
    if (eventDTO.getPlatform() == null) {
        String property = env.getProperty("ears.platform");
        if (property != null) {
            eventDTO.setPlatform(property);
        } else {
            throw new IllegalArgumentException(
                    "No platform has been provided in the POST body and no platform has been set in the web service configuration.");
        }
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
    // return new ResponseEntity<Event>(, HttpStatus.CREATED);event
    } */

}
