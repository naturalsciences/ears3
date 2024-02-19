package eu.eurofleets.ears3.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.domain.*;
import eu.eurofleets.ears3.dto.ErrorDTO;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.excel.SpreadsheetEvent;
import eu.eurofleets.ears3.service.EventExcelService;
import eu.eurofleets.ears3.service.EventService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import eu.eurofleets.ears3.service.PersonService;
import io.github.rushuat.ocell.document.Documents;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.github.rushuat.ocell.document.Document;

@RestController()
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventExcelInputController {

    public static final String DEFAULT_VALUE = "_";
    public static final String SHEETNAME = "events";
    @Autowired
    private EventService eventService;

    @Autowired
    private PersonService personService;

    @Autowired
    private EventExcelService eventExcelService;

    @Autowired
    private Environment env;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = { "excelImport" }, produces = { "application/xml; charset=utf-8", "application/json" }, consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    //    public ResponseEntity<Message<List<ErrorRow>>> createEvent(@RequestParam("file") MultipartFile mpFile, @RequestHeader("person") PersonDTO actor) {
    public ResponseEntity<Message> createEvent(@RequestParam("file") MultipartFile mpFile,
            @RequestHeader("person") String actorName) {
        List<ErrorDTO> errorList = new ArrayList<>();

        PersonDTO actor;
        try {
            actor = objectMapper.readValue(actorName, PersonDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        final List<Person> byName = personService.findByName(actor.getFirstName(), actor.getLastName());
        if( byName.size() != 1 ){
            Message<List<ErrorDTO>> msg = new Message<>(HttpStatus.EXPECTATION_FAILED.value(),"Error, Invalid or Unknown Person ! ", errorList);
            return new ResponseEntity<>(msg, HttpStatus.EXPECTATION_FAILED );
        }

        try (Document document = Documents.OOXML().create()) {
            byte[] byteArr = mpFile.getBytes();
            InputStream inputStream = new ByteArrayInputStream(byteArr);
            document.fromStream(inputStream);
            //validateAllTabs();
            inputStream = new ByteArrayInputStream(byteArr);
            Workbook poiWb = WorkbookFactory.create(inputStream);
            boolean areTabsOk = eventExcelService.validateAllTabs(poiWb, errorList);
            boolean areHeadersOk = eventExcelService.validateHeaders(poiWb, SHEETNAME, errorList);
            if (!areHeadersOk || !areTabsOk ) {
                Message<List<ErrorDTO>> msg = new Message<>(HttpStatus.CONFLICT.value(), "Error Creating Excel Event",
                        errorList);
                return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
            }
            List<SpreadsheetEvent> data = document.getSheet(SHEETNAME, SpreadsheetEvent.class);
            List<EventDTO> events = new ArrayList<>();
            boolean processProblems = eventExcelService.processSpreadsheetEvents(errorList, data, events, actor);
            boolean saveProblems = false;
            if (!processProblems) {
                saveProblems = eventExcelService.saveSpreadsheetEvents(errorList, events);
            }
            if (processProblems || saveProblems) {
                Message<List<ErrorDTO>> msg = new Message<>(HttpStatus.CONFLICT.value(), "Error Creating Excel Event",
                        errorList);
                return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //errorList.add();
            Message<List<ErrorDTO>> msg = new Message<>(HttpStatus.CONFLICT.value(), "Error Creating Excel Event",
                    errorList);
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        Message<String> msg = new Message<>(HttpStatus.CREATED.value(),
                "Successfully read Excel and created all events", mpFile.getOriginalFilename());
        return new ResponseEntity<Message>(msg, HttpStatus.CREATED);
    }

}
