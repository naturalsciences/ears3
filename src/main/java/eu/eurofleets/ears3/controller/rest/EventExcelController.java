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
import java.io.Writer;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    public ResponseEntity<Message> excelImport(@RequestPart("file") MultipartFile mpFile, @RequestPart("apd") ActorProgramTZDTO actorProgramDTO) {
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
                Message<ErrorDTOList> msg = new Message<>(HttpStatus.CONFLICT.value(), null, errorList, "Error Creating Excel Event (headers/tabs invalid)",
                        null);
                return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
            }
            List<SpreadsheetEvent> rawData = document.getSheet(SHEETNAME, SpreadsheetEvent.class);
            List<SpreadsheetEvent> data = rawData.stream()
                    .filter(e -> !isBlankRow(e))
                    .collect(Collectors.toList());
            List<EventDTO> events = new ArrayList<>();
            PersonDTO actor = actorProgramDTO.getActor();
            String program = actorProgramDTO.getProgram();
            String timezone = actorProgramDTO.getTimezone();
            boolean hasProblems = eventExcelService.processSpreadsheetEvents(errorList, data, events, actor, program, timezone);
            boolean hasSaveProblems = false;
            if (!hasProblems) {
                hasSaveProblems = eventExcelService.saveSpreadsheetEvents(errorList, events);
            }
            if (hasProblems) {
                Message<ErrorDTOList> msg = new Message<>(HttpStatus.CONFLICT.value(), null, errorList, "Error Creating Excel Event (row issue)",
                        null);
                return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
            }
            if (hasSaveProblems) {
                Message<ErrorDTOList> msg = new Message<>(HttpStatus.CONFLICT.value(), null, errorList, "Error Creating Excel Event (saving issue)",
                        null);
                return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
            }
        } catch (IOException e) {
            Message<ErrorDTOList> msg = new Message<>(HttpStatus.CONFLICT.value(), null, errorList, "Error Creating Excel Event",
                    "IOException");
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        Message<String> msg = new Message<>(HttpStatus.CREATED.value(), null, null, "Successfully read Excel and created all events",
                null);
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }

    private static boolean isBlankRow(SpreadsheetEvent e) {
        return isBlank(e.getDate()) && isBlank(e.getTime()) && isBlank(e.getTool())
                && isBlank(e.getProcess()) && isBlank(e.getAction()) && isBlank(e.getLabel())
                && isBlank(e.getStation()) && isBlank(e.getDescription());
        // extend with any other core fields as needed
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static String doubleOrNull(Double val) {
        return (val != null) ? val.toString() : "";
    }

    private static String offsetDateTimeOrNull(OffsetDateTime val) {
        return (val != null) ? val.toString() : "";
    }

    // properties but are not saved as properties
    @GetMapping(value = "events.csv", produces = "text/csv; charset=utf-8")
    public String getEventsAsCSV(@RequestParam Map<String, String> allParams) throws IOException {
        Page<Event> eventsPage = this.eventService.advancedFind(allParams, Pageable.unpaged());
        List<Event> events = eventsPage.stream().toList();
        List<String> header = new ArrayList<>(Arrays.asList("Date", "Time", "Actor", "Program", "Program name", "Principal Investigator",
                "Tool category", "Tool category code", "Tool", "Tool code", "Process", "Action", "Label", "Station",
                "Description", "Remarks"));
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
                String programName = null;
                String programId = null;
                if (program != null) { // can't be null but test anyway
                    programId = program.getIdentifier();
                    programName = program.getName();
                }
                List<String> elements = new ArrayList<>(Arrays.asList(
                        event.getTimeStamp().toLocalDate().format(DateTimeFormatter.ISO_DATE), //yyyy-mm-dd
                        event.getTimeStamp().format(DateTimeFormatter.ofPattern("HH:mm:ss"))+"Z", //hh:mm:ss in UTC
                        event.getActor().getFirstName() + " " + event.getActor().getLastName(),
                        programId,
                        programName,
                        event.getPrincipalInvestigators(),
                        event.getToolCategory().getName(),
                        ILinkedDataTerm.getBodcUrnFromTerm(event.getToolCategory()),
                        // event.getToolCategory().getTransitiveUrn(),
                        event.getTool().getTerm().getName(),
                        ILinkedDataTerm.getBodcUrnFromTerm(event.getTool().getTerm()),
                        event.getProcess().getName(),
                        event.getAction().getName(),
                        event.getLabel(),
                        event.getStation(),
                        event.getDescription(),
                        event.getRemarks()));

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
