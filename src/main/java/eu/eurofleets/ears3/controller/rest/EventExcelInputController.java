package eu.eurofleets.ears3.controller.rest;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProperty;

import com.opencsv.CSVWriter;
import eu.eurofleets.ears3.domain.*;
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

import eu.eurofleets.ears3.service.PersonService;
import io.github.rushuat.ocell.document.Documents;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
    private PersonService personService;

    @Autowired
    private EventExcelService eventExcelService;

    @Autowired
    private Environment env;

    @PostMapping(value = { "event" },
              produces = { "application/xml; charset=utf-8", "application/json" },
              consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<Message<List<ErrorRow>>> createEvent(@RequestParam("file") MultipartFile mpFile, @RequestHeader("person") PersonDTO actor) {
    public ResponseEntity<Message<List<ErrorRow>>> createEvent(@RequestParam("file") MultipartFile mpFile, @RequestHeader("person") String actorName) {
        List<ErrorRow> errorList = new ArrayList<>();

/**/        String[] nameParts = actorName.split("#");
/**/        PersonDTO actor = new PersonDTO();
/**/        actor.setFirstName(nameParts[0]);
/**/        actor.setLastName(nameParts[1]);
        /** //TODO: uncomment as this should be done, just commented out for testing purposes;
        final List<Person> byName = personService.findByName(actor.getFirstName(), actor.getLastName());
        if( byName.size() != 1 ){
            Message<List<ErrorRow>> msg = new Message<>(HttpStatus.EXPECTATION_FAILED.value(),"Error, Invalid or Unknown Person ! ", errorList);
            return new ResponseEntity<>(msg, HttpStatus.EXPECTATION_FAILED );
        } else {
            actor.setEmail(byName.get(0).getEmail());
        }*/
/**/        /**TODO: EDIT this line*/actor.setEmail("blah@fake.com");
/**/        actor.setOrganisation("SDN:EDMO::428");

        try (Document document = Documents.OOXML().create()) {
            byte [] byteArr = mpFile.getBytes();
            InputStream inputStream = new ByteArrayInputStream(byteArr);
            document.fromStream(inputStream);
            //validateAllTabs();
            inputStream = new ByteArrayInputStream(byteArr);
            Workbook poiWb = WorkbookFactory.create(inputStream);
            boolean areTabsOk = eventExcelService.validateAllTabs(poiWb);
            //validateHeaders();
            String sheetName = "events";
            ////////boolean areHeadersOk = eventExcelService.validateHeaders(document, sheetName);
            List<SpreadsheetEvent> data = document.getSheet(sheetName, SpreadsheetEvent.class);
            List<EventDTO> events = new ArrayList<>();
            boolean processProblems = eventExcelService.processSpreadsheetEvents(errorList, data, events, actor);
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

}
