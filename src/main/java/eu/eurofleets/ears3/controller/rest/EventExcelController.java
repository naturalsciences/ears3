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
import java.time.Instant;
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

    private static String instantOrNull(Instant val) {
        return (val != null) ? val.toString() : "";
    }

    private static String fieldHeader(String key, String instrId) {
        return (instrId != null && !instrId.isEmpty()) ? key + "_" + instrId : key;
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

        Writer writer = new StringBuilderWriter();

        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            boolean headerWritten = false;
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
                        event.getTimeStamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "Z", //hh:mm:ss in UTC
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

                if (!headerWritten) {
                    String latId = nav != null ? nav.getLatInstrId() : null;
                    String lonId = nav != null ? nav.getLonInstrId() : null;
                    String depthId = nav != null ? nav.getDepthInstrId() : null;
                    String headingId = nav != null ? nav.getHeadingInstrId() : null;
                    String cogId = nav != null ? nav.getCogInstrId() : null;
                    String sogId = nav != null ? nav.getSogInstrId() : null;

                    String temperatureId = tss != null ? tss.getTemperatureInstrId() : null;
                    String salinityId = tss != null ? tss.getSalinityInstrId() : null;
                    String conductivityId = tss != null ? tss.getConductivityInstrId() : null;
                    String sigmatId = tss != null ? tss.getSigmatInstrId() : null;

                    String windSpeedAverageId = met != null ? met.getWindSpeedAverageInstrId() : null;
                    String windDirectionId = met != null ? met.getWindDirectionInstrId() : null;
                    String atmosphericTemperatureId = met != null ? met.getAtmosphericTemperatureInstrId() : null;
                    String humidityId = met != null ? met.getHumidityInstrId() : null;
                    String atmosphericPressureId = met != null ? met.getAtmosphericPressureInstrId() : null;
                    String solarRadiationId = met != null ? met.getSolarRadiationInstrId() : null;

                    header.add(fieldHeader("lat", latId));
                    header.add(fieldHeader("lat_timestamp", latId));
                    header.add(fieldHeader("lon", lonId));
                    header.add(fieldHeader("lon_timestamp", lonId));
                    header.add(fieldHeader("depth", depthId));
                    header.add(fieldHeader("depth_timestamp", depthId));
                    header.add(fieldHeader("heading", headingId));
                    header.add(fieldHeader("heading_timestamp", headingId));
                    header.add(fieldHeader("cog", cogId));
                    header.add(fieldHeader("cog_timestamp", cogId));
                    header.add(fieldHeader("sog", sogId));
                    header.add(fieldHeader("sog_timestamp", sogId));

                    header.add(fieldHeader("temperature", temperatureId));
                    header.add(fieldHeader("temperature_timestamp", temperatureId));
                    header.add(fieldHeader("salinity", salinityId));
                    header.add(fieldHeader("salinity_timestamp", salinityId));
                    header.add(fieldHeader("conductivity", conductivityId));
                    header.add(fieldHeader("conductivity_timestamp", conductivityId));
                    header.add(fieldHeader("sigmat", sigmatId));
                    header.add(fieldHeader("sigmat_timestamp", sigmatId));

                    header.add(fieldHeader("wind_speed_average", windSpeedAverageId));
                    header.add(fieldHeader("wind_speed_average_timestamp", windSpeedAverageId));
                    header.add(fieldHeader("wind_direction", windDirectionId));
                    header.add(fieldHeader("wind_direction_timestamp", windDirectionId));
                    header.add(fieldHeader("atmospheric_temperature", atmosphericTemperatureId));
                    header.add(fieldHeader("atmospheric_temperature_timestamp", atmosphericTemperatureId));
                    header.add(fieldHeader("humidity", humidityId));
                    header.add(fieldHeader("humidity_timestamp", humidityId));
                    header.add(fieldHeader("atmospheric_pressure", atmosphericPressureId));
                    header.add(fieldHeader("atmospheric_pressure_timestamp", atmosphericPressureId));
                    header.add(fieldHeader("solar_radiation", solarRadiationId));
                    header.add(fieldHeader("solar_radiation_timestamp", solarRadiationId));

                    csvWriter.writeNext(header.toArray(new String[0]), true);
                    headerWritten = true;
                }

                if (nav != null) {
                    elements.add(doubleOrNull(nav.getLat()));
                    elements.add(instantOrNull(nav.getLatTimestamp()));
                    elements.add(doubleOrNull(nav.getLon()));
                    elements.add(instantOrNull(nav.getLonTimestamp()));
                    elements.add(doubleOrNull(nav.getDepth()));
                    elements.add(instantOrNull(nav.getDepthTimestamp()));
                    elements.add(doubleOrNull(nav.getHeading()));
                    elements.add(instantOrNull(nav.getHeadingTimestamp()));
                    elements.add(doubleOrNull(nav.getCog()));
                    elements.add(instantOrNull(nav.getCogTimestamp()));
                    elements.add(doubleOrNull(nav.getSog()));
                    elements.add(instantOrNull(nav.getSogTimestamp()));
                } else {
                    elements.addAll(Collections.nCopies(12, ""));
                }

                if (tss != null) {
                    elements.add(doubleOrNull(tss.getTemperature()));
                    elements.add(instantOrNull(tss.getTemperatureTimestamp()));
                    elements.add(doubleOrNull(tss.getSalinity()));
                    elements.add(instantOrNull(tss.getSalinityTimestamp()));
                    elements.add(doubleOrNull(tss.getConductivity()));
                    elements.add(instantOrNull(tss.getConductivityTimestamp()));
                    elements.add(doubleOrNull(tss.getSigmat()));
                    elements.add(instantOrNull(tss.getSigmatTimestamp()));
                } else {
                    elements.addAll(Collections.nCopies(8, ""));
                }
                if (met != null) {
                    elements.add(doubleOrNull(met.getWindSpeedAverage()));
                    elements.add(instantOrNull(met.getWindSpeedAverageTimestamp()));
                    elements.add(doubleOrNull(met.getWindDirection()));
                    elements.add(instantOrNull(met.getWindDirectionTimestamp()));
                    elements.add(doubleOrNull(met.getAtmosphericTemperature()));
                    elements.add(instantOrNull(met.getAtmosphericTemperatureTimestamp()));
                    elements.add(doubleOrNull(met.getHumidity()));
                    elements.add(instantOrNull(met.getHumidityTimestamp()));
                    elements.add(doubleOrNull(met.getAtmosphericPressure()));
                    elements.add(instantOrNull(met.getAtmosphericPressureTimestamp()));
                    elements.add(doubleOrNull(met.getSolarRadiation()));
                    elements.add(instantOrNull(met.getSolarRadiationTimestamp()));
                } else {
                    elements.addAll(Collections.nCopies(12, ""));
                }

                csvWriter.writeNext(elements.toArray(new String[0]), true);
            }

            if (!headerWritten) {
                // No events at all: still emit a header (no acquisition IDs available to append).
                header.add(fieldHeader("lat", null));
                header.add(fieldHeader("lat_timestamp", null));
                header.add(fieldHeader("lon", null));
                header.add(fieldHeader("lon_timestamp", null));
                header.add(fieldHeader("depth", null));
                header.add(fieldHeader("depth_timestamp", null));
                header.add(fieldHeader("heading", null));
                header.add(fieldHeader("heading_timestamp", null));
                header.add(fieldHeader("cog", null));
                header.add(fieldHeader("cog_timestamp", null));
                header.add(fieldHeader("sog", null));
                header.add(fieldHeader("sog_timestamp", null));

                header.add(fieldHeader("temperature", null));
                header.add(fieldHeader("temperature_timestamp", null));
                header.add(fieldHeader("salinity", null));
                header.add(fieldHeader("salinity_timestamp", null));
                header.add(fieldHeader("conductivity", null));
                header.add(fieldHeader("conductivity_timestamp", null));
                header.add(fieldHeader("sigmat", null));
                header.add(fieldHeader("sigmat_timestamp", null));

                header.add(fieldHeader("wind_speed_average", null));
                header.add(fieldHeader("wind_speed_average_timestamp", null));
                header.add(fieldHeader("wind_direction", null));
                header.add(fieldHeader("wind_direction_timestamp", null));
                header.add(fieldHeader("atmospheric_temperature", null));
                header.add(fieldHeader("atmospheric_temperature_timestamp", null));
                header.add(fieldHeader("humidity", null));
                header.add(fieldHeader("humidity_timestamp", null));
                header.add(fieldHeader("atmospheric_pressure", null));
                header.add(fieldHeader("atmospheric_pressure_timestamp", null));
                header.add(fieldHeader("solar_radiation", null));
                header.add(fieldHeader("solar_radiation_timestamp", null));

                csvWriter.writeNext(header.toArray(new String[0]), true);
            }
        }
        writer.flush();
        writer.close();
        return writer.toString();
    }
}