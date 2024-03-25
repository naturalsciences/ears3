package eu.eurofleets.ears3.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.domain.Message;
import eu.eurofleets.ears3.dto.ErrorDTO;
import eu.eurofleets.ears3.dto.ErrorDTOList;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.excel.SpreadsheetEvent;
import eu.eurofleets.ears3.service.EventExcelService;
import io.github.rushuat.ocell.document.Document;
import io.github.rushuat.ocell.document.Documents;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventExcelInputController {

    public static final String DEFAULT_VALUE = "_";
    public static final String SHEETNAME = "events";

    @Autowired
    private EventExcelService eventExcelService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = { "excelImport" }, produces = { "application/xml; charset=utf-8", "application/json" }, consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ErrorDTOList> createEvent(@RequestParam("file") MultipartFile mpFile,
                                                    @RequestParam("person") String actorName) {

        ErrorDTOList errorList = new ErrorDTOList();

        PersonDTO actor;
        try {
            actor = objectMapper.readValue(actorName, PersonDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        try (Document document = Documents.OOXML().create()) {
            byte[] byteArr = mpFile.getBytes();
            InputStream inputStream = new ByteArrayInputStream(byteArr);
            document.fromStream(inputStream);
            inputStream = new ByteArrayInputStream(byteArr);
            Workbook poiWb = WorkbookFactory.create(inputStream);
            boolean areTabsOk = eventExcelService.validateAllTabs(poiWb, errorList);
            boolean areHeadersOk = eventExcelService.validateHeaders(poiWb, SHEETNAME, errorList);
            if (!areHeadersOk || !areTabsOk ) {
                Message<ErrorDTOList> msg = new Message<>(HttpStatus.CONFLICT.value(), "Error Creating Excel Event, CSV content does not comply with expectations. (ea: missing headers, sheets, tabs)",
                        errorList);
                return new ResponseEntity<>(errorList, HttpStatus.CONFLICT);
            }
            List<SpreadsheetEvent> data = document.getSheet(SHEETNAME, SpreadsheetEvent.class);
            List<EventDTO> events = new ArrayList<>();
            boolean processProblems = eventExcelService.processSpreadsheetEvents(errorList, data, events, actor);
            boolean saveProblems = false;
            if (!processProblems) {
                saveProblems = eventExcelService.saveSpreadsheetEvents(errorList, events);
            }
            if (processProblems || saveProblems) {
                Message<ErrorDTOList> msg = new Message<>(HttpStatus.CONFLICT.value(), "Error Creating Excel Event",
                        errorList);
                return new ResponseEntity<>(errorList, HttpStatus.CONFLICT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ErrorDTO msg = new ErrorDTO("Error Creating Excel Event", null);
            errorList.addError(msg);
            return new ResponseEntity<>(errorList, HttpStatus.CONFLICT);
        }

        ErrorDTO msg = new ErrorDTO("Successfully read Excel and created all events", null);
        errorList.addError(msg);

        return new ResponseEntity<>(errorList, HttpStatus.CREATED);
    }

}
