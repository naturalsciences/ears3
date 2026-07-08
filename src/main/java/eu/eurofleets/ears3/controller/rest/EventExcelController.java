package eu.eurofleets.ears3.controller.rest;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProperty;
import com.opencsv.CSVWriter;
import eu.eurofleets.ears3.domain.*;
import eu.eurofleets.ears3.dto.*;
import eu.eurofleets.ears3.service.EventService;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tools.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.excel.SpreadsheetEvent;
import eu.eurofleets.ears3.service.EventExcelService;
import eu.eurofleets.ears3.service.ProgramService;
import io.github.rushuat.ocell.document.Document;
import io.github.rushuat.ocell.document.Documents;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController()
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventExcelController {
    public static final String SHEETNAME = "events";

    @Autowired
    private EventExcelService eventExcelService;

    @Autowired
    private EventService eventService;

    @PostMapping(value = "event/import", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ErrorDTOList> excelImport(@RequestPart("file") MultipartFile mpFile, @RequestPart("apd") ActorProgramDTO actorProgramDTO) {
        ErrorDTOList errorList = new ErrorDTOList();
        try (Document document = Documents.OOXML().create()) {
            byte[] byteArr = mpFile.getBytes();
            InputStream inputStream = new ByteArrayInputStream(byteArr);
            document.fromStream(inputStream);
            inputStream = new ByteArrayInputStream(byteArr);
            Workbook poiWb = WorkbookFactory.create(inputStream);
            boolean areTabsOk = eventExcelService.validateAllTabs(poiWb, errorList);
            boolean areHeadersOk = eventExcelService.validateHeaders(poiWb, SHEETNAME, errorList);
            if (!areHeadersOk || !areTabsOk) {
                return new ResponseEntity<>(errorList, HttpStatus.CONFLICT);
            }
            List<SpreadsheetEvent> data = document.getSheet(SHEETNAME, SpreadsheetEvent.class);
            List<EventDTO> events = new ArrayList<>();
            PersonDTO actor = actorProgramDTO.getActor();
            String program = actorProgramDTO.getProgram();
            boolean hasProblems = eventExcelService.processSpreadsheetEvents(errorList, data, events, actor, program);
            boolean hasSaveProblems = false;
            if (!hasProblems) {
                hasSaveProblems = eventExcelService.saveSpreadsheetEvents(errorList, events);
            }
            if (hasProblems || hasSaveProblems) {
                Message<ErrorDTOList> msg = new Message<>(HttpStatus.CONFLICT.value(), "Error Creating Excel Event",
                        errorList);
                return new ResponseEntity<>(errorList, HttpStatus.CONFLICT);
            }
        } catch (IOException e) {
            ErrorDTO msg = new ErrorDTO("Error Creating Excel Event", null);
            errorList.addError(msg);
            return new ResponseEntity<>(errorList, HttpStatus.CONFLICT);
        }

        ErrorDTO msg = new ErrorDTO("Successfully read Excel and created all events", null);
        errorList.addError(msg);
        return new ResponseEntity<>(errorList, HttpStatus.CREATED);
    }

    private static String doubleOrNull(Double val) {
        return (val != null) ? val.toString() : "";
    }

    private static String offsetDateTimeOrNull(OffsetDateTime val) {
        return (val != null) ? val.toString() : "";
    }

    // properties but are not saved as properties
    @RequestMapping(method = RequestMethod.GET, value = "events.csv", produces = "text/csv; charset=utf-8")
    public String getEventsAsCSV(@RequestParam Map<String, String> allParams) throws IOException {
        Page<Event> eventsPage = this.eventService.advancedFind(allParams, Pageable.unpaged());
        List<Event> events = eventsPage.stream().toList();
        List<String> header = new ArrayList<>(Arrays.asList("Time stamp", "Actor", "Program", "Principal Investigator",
                "Tool category", "Tool category code", "Tool", "Tool code", "Process", "Action", "Station", "Label",
                "Description"));
        Map<String, String> properties = new TreeMap<>();
        for (Event event : events) {
            for (IProperty property : event.getProperties()) {
                properties.put(property.getKey().getIdentifier(), property.getKey().getName());
            }
        }
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String propertyName = entry.getValue();
            header.add(propertyName);
        }

        header.addAll(Arrays.asList("Acquisition Timestamp", "Latitude", "Longitude", "Depth", "Heading",
                "Course over Ground", "Speed over Ground"));
        header.addAll(Arrays.asList("Surface water temperature", "Salinity", "Conductivity", "Sigma T", "Wind speed",
                "Wind direction", "Air temperature", "Humidity", "Air pressure", "Solar Radiation"));

        String[] entry = new String[header.size()];
        entry = header.toArray(entry); // convert list to array

        Writer writer = new StringBuilderWriter();
        // CSVWriter csvWriter = null;

        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(entry, true);
            for (Event event : events) {
                IProgram program = event.getProgram();
                String niceProgram = null;
                if (program != null) { // can't be null but test anyway
                    niceProgram = program.getIdentifier() + (program.getName() != null && !program.getName().isEmpty()
                            ? " (" + program.getName() + ")"
                            : "");
                }
                List<String> elements = new ArrayList<>(Arrays.asList(
                        event.getTimeStamp().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                        event.getActor().getFirstName() + " " + event.getActor().getLastName(),
                        niceProgram,
                        event.getPrincipalInvestigators(),
                        event.getToolCategory().getName(),
                        ILinkedDataTerm.getBodcUrnFromTerm(event.getToolCategory()),
                        // event.getToolCategory().getTransitiveUrn(),
                        event.getTool().getTerm().getName(),
                        ILinkedDataTerm.getBodcUrnFromTerm(event.getTool().getTerm()),
                        event.getProcess().getName(),
                        event.getAction().getName(),
                        event.getStation(),
                        event.getLabel(),
                        event.getDescription()));

                for (String propertyUrl : properties.keySet()) {
                    List<String> propertyValues = event.getPropertyValues(propertyUrl);
                    if (propertyValues != null) {
                        elements.add(StringUtils.join(propertyValues, ","));
                    } else {
                        elements.add("");
                    }
                }
                Navigation nav = (!event.getNavigation().isEmpty() ? event.getNavigation().iterator().next() : null);
                Thermosal tss = (!event.getThermosal().isEmpty() ? event.getThermosal().iterator().next() : null);
                Weather met = (!event.getWeather().isEmpty() ? event.getWeather().iterator().next() : null);
                if (nav != null) {
                    elements.addAll(Arrays.asList(
                            offsetDateTimeOrNull(nav.getTime()),
                            doubleOrNull(nav.getLat()),
                            doubleOrNull(nav.getLon()),
                            doubleOrNull(nav.getDepth()),
                            doubleOrNull(nav.getHeading()),
                            doubleOrNull(nav.getCog()),
                            doubleOrNull(nav.getSog())));
                } else {
                    elements.addAll(Arrays.asList("", "", "", "", "", "", ""));
                }

                if (tss != null) {
                    elements.addAll(Arrays.asList(
                            doubleOrNull(tss.getTemperature()),
                            doubleOrNull(tss.getSalinity()),
                            doubleOrNull(tss.getConductivity()),
                            doubleOrNull(tss.getSigmat())));
                } else {
                    elements.addAll(Arrays.asList("", "", "", ""));
                }
                if (met != null) {
                    elements.addAll(Arrays.asList(
                            doubleOrNull(met.getWindSpeedAverage()),
                            doubleOrNull(met.getWindDirection()),
                            doubleOrNull(met.getAtmosphericTemperature()),
                            doubleOrNull(met.getHumidity()),
                            doubleOrNull(met.getAtmosphericPressure()),
                            doubleOrNull(met.getSolarRadiation())));
                } else {
                    elements.addAll(Arrays.asList("", "", "", "", "", ""));
                }
                entry = new String[elements.size()];
                entry = elements.toArray(entry);

                csvWriter.writeNext(entry, true);
            }
        }
        writer.flush();
        writer.close();
        return writer.toString();
    }
}
