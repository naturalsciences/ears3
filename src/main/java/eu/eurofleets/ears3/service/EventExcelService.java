package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.Exceptions.ImportException;
import eu.eurofleets.ears3.controller.rest.EventExcelInputController;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Weather;
import eu.eurofleets.ears3.dto.*;
import eu.eurofleets.ears3.excel.SpreadsheetEvent;
import eu.eurofleets.ears3.excel.converters.DateHelper;
import eu.eurofleets.ears3.utilities.DatagramUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EventExcelService {

    private Validator validator;

    private final EventRepository eventRepository;
    @Autowired
    private ProgramService programService;

    @Autowired
    private EventService eventService;

    private DatagramUtilities<Navigation> navUtil;
    private DatagramUtilities<Thermosal> thermosalUtil;
    private DatagramUtilities<Weather> weatherUtil;
    public static Logger log = Logger.getLogger(EventService.class.getSimpleName());

    @Autowired
    private final Environment env;

    private static List<String> allowedTabs = Arrays.asList("events");
    private List<String> requiredHeaders = Arrays.stream(SpreadsheetEvent.FIELDS.values()).map(Enum::name)
            .collect(Collectors.toList());

    @Value("${ears.platform}")
    public String platformUrn;

    private static Map<String, LinkedDataTermDTO> DEFS = new HashMap<>();
    private static Map<String, LinkedDataTermDTO> CATMAP = new HashMap<>();
    private static Map<String, PropertyDTO> PROPMAPDEF = new HashMap<>();

    @Autowired
    public EventExcelService(EventRepository eventRepository, Environment env) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        this.eventRepository = eventRepository;
        this.env = env;
        String navigationServer = env.getProperty("ears.navigation.server");
        try {
            navUtil = new DatagramUtilities<>(Navigation.class, navigationServer);
            thermosalUtil = new DatagramUtilities<>(Thermosal.class, navigationServer);
            weatherUtil = new DatagramUtilities<>(Weather.class, navigationServer);
            initializeHashmaps();
        } catch (MalformedURLException ex) {
            Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void initializeHashmaps() throws IOException {

        JsonNode rootNode;
        ObjectMapper objectMapper;
        objectMapper = new ObjectMapper();
        File jsonFile = new ClassPathResource("static/json/my.json").getFile();
        rootNode = objectMapper.readTree(jsonFile);

        JsonNode defs = rootNode.get("defs");
        ArrayList<LinkedHashMap<String, String>> defList = objectMapper.convertValue(defs, ArrayList.class);

        JsonNode catmap = rootNode.get("catmap");
        ArrayList<LinkedHashMap<String, String>> cmList = objectMapper.convertValue(catmap, ArrayList.class);

        JsonNode properties = rootNode.get("properties");
        ArrayList<LinkedHashMap<String, String>> propList = objectMapper.convertValue(properties, ArrayList.class);

        for (LinkedHashMap<String, String> item : defList) {
            LinkedDataTermDTO ldtDTO = new LinkedDataTermDTO(item.get("identifier"), item.get("transitveldidentifier"),
                    item.get("name"));
            String key = StringUtils.capitalize(StringUtils.lowerCase(item.get("name")));
            DEFS.put(key, ldtDTO);
        }

        for (LinkedHashMap<String, String> item : cmList) {
            String key = StringUtils.capitalize(StringUtils.lowerCase(item.get("name")));
            String prop = StringUtils.capitalize(StringUtils.lowerCase(item.get("prop")));
            LinkedDataTermDTO ldtDTO = DEFS.get(prop);
            CATMAP.put(key, ldtDTO);
        }

        for (LinkedHashMap<String, String> item : propList) {
            LinkedDataTermDTO ldtDTO = new LinkedDataTermDTO(item.get("identifier"), item.get("transitveldidentifier"),
                    item.get("name"));
            PropertyDTO pDTO = new PropertyDTO(ldtDTO, item.get("value"), item.get("uom"));
            String key = StringUtils.capitalize(StringUtils.lowerCase(item.get("name")));
            PROPMAPDEF.put(key, pDTO);
        }

    }

    private String loweredCapitalize(String input) {
        return StringUtils.capitalize(input.toLowerCase());
    }

    public LinkedDataTermDTO extractLDT(String synonym, int rowNb) throws ImportException {
        synonym = loweredCapitalize(synonym);
        LinkedDataTermDTO targetLDT = DEFS.get(synonym);
        if (targetLDT == null) {
            throw new ImportException(EventExcelInputController.SHEETNAME, rowNb,
                    String.format("Unknown Linked Data Term [ %s ]", synonym), null);
        } else {
            return targetLDT;
        }
    }

    public LinkedDataTermDTO extractToolCategory(String synonym, int rowNb) throws ImportException {
        synonym = loweredCapitalize(synonym);
        LinkedDataTermDTO targetLDT = CATMAP.get(synonym);
        if (targetLDT == null) {
            throw new ImportException(EventExcelInputController.SHEETNAME, rowNb,
                    String.format("Unknown ToolCategory [ %s ]", synonym), null);
        } else {
            return targetLDT;
        }
    }

    private static ZonedDateTime createZonedDateTime(SpreadsheetEvent spreadsheetEvent, int rowNb)
            throws ImportException {
        ZonedDateTime zdt = null;
        String date = spreadsheetEvent.getDate();
        String hour = spreadsheetEvent.getHour();
        try {
            zdt = DateHelper.dateTimeStringToZonedDateTime(date, hour);
        } catch (Exception e) {
            throw new ImportException(EventExcelInputController.SHEETNAME, rowNb,
                    String.format("Problem with [%s]%n", e.getMessage()), null);
        }
        return zdt;
    }

    private EventDTO processSpreadsheetEvent(SpreadsheetEvent spreadsheetEvent, int rowNb) throws ImportException {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setIdentifier(null);
        //Bail out early if we already know all the constraints that have been violated.
        ArrayList<String> errorSummaryForRow = new ArrayList<>();
        Set<ConstraintViolation<SpreadsheetEvent>> errors = validator.validate(spreadsheetEvent);
        if (!errors.isEmpty()) {
            System.out.printf("Problem with %s%n", spreadsheetEvent.toString());
            errors.forEach(error -> {
                errorSummaryForRow.add(error.getPropertyPath() + " " + error.getMessage());
            });
        }

        Program program = programService.findOrCreateProgram(spreadsheetEvent.getProgram());
        if (program != null) {
            eventDTO.setProgram(program.getIdentifier());
        } else {
            errorSummaryForRow.add("\nError setting the Program [" + spreadsheetEvent.getProgram() + "].\n");
        }

        if (!errorSummaryForRow.isEmpty()) {
            System.out.println(errorSummaryForRow.toString());
            throw new ImportException(EventExcelInputController.SHEETNAME, rowNb,
                    String.format("Problem with %s%n", errorSummaryForRow.toString()), null);
        }

        ZonedDateTime zdt = createZonedDateTime(spreadsheetEvent, rowNb);
        eventDTO.setTimeStamp(zdt.toOffsetDateTime());
        eventDTO.setRemarks(spreadsheetEvent.getRemarks());
        eventDTO.setPlatform(platformUrn);

        String uuid = eventService.findUuidByToolActionProc("", spreadsheetEvent.getTool(),
                spreadsheetEvent.getProcess(),
                spreadsheetEvent.getAction());
        eventDTO.setEventDefinitionId(uuid);

        String toolName = spreadsheetEvent.getTool();
        eventDTO.setTool(new ToolDTO(extractLDT(toolName, rowNb), null));
        LinkedDataTermDTO toolCategory = extractToolCategory(toolName, rowNb);
        eventDTO.setToolCategory(toolCategory);

        String processName = spreadsheetEvent.getProcess();
        LinkedDataTermDTO process = extractLDT(processName, rowNb);
        eventDTO.setProcess(process);

        String actionName = spreadsheetEvent.getAction();
        eventDTO.setAction(extractLDT(actionName, rowNb));

        eventDTO.setLabel(spreadsheetEvent.getLabel());

        String stationName = spreadsheetEvent.getStation();
        eventDTO.setStation(stationName);

        eventDTO.setDescription(spreadsheetEvent.getDescription());
        eventDTO.setSubject(new LinkedDataTermDTO("https://vocab.nerc.ac.uk/collection/C77/current/M06", null,
                "Routine standard measurements"));

        Map<String, PropertyDTO> props = new HashMap<>();
        createPropertiesIfAvailable(spreadsheetEvent, props);
        eventDTO.setProperties(props.values());

        return eventDTO;
    }

    private static void createPropertiesIfAvailable(SpreadsheetEvent spreadsheetEvent, Map<String, PropertyDTO> props) {
        if ((spreadsheetEvent.getDistance()) != null && !(spreadsheetEvent.getDistance()).isEmpty()) {
            PropertyDTO dist = PROPMAPDEF.get("Distance travelled");
            dist.setValue(spreadsheetEvent.getDistance());
            props.put(SpreadsheetEvent.FIELDS.Dist.name(), dist);
        }
        if ((spreadsheetEvent.getTime()) != null && !(spreadsheetEvent.getTime()).isEmpty()) {
            PropertyDTO time = PROPMAPDEF.get("Time");
            time.setValue(spreadsheetEvent.getTime());
            props.put(SpreadsheetEvent.FIELDS.Time.name(), time);
        }
        if ((spreadsheetEvent.getStatus()) != null && !(spreadsheetEvent.getStatus()).isEmpty()) {
            PropertyDTO status = PROPMAPDEF.get("Status");
            status.setValue(spreadsheetEvent.getStatus());
            props.put(SpreadsheetEvent.FIELDS.Status.name(), status);
        }
        if ((spreadsheetEvent.getRegion()) != null && !(spreadsheetEvent.getRegion()).isEmpty()) {
            PropertyDTO region = PROPMAPDEF.get("Region");
            region.setValue(spreadsheetEvent.getRegion());
            props.put(SpreadsheetEvent.FIELDS.Region.name(), region);
        }
        if ((spreadsheetEvent.getWeather()) != null && !(spreadsheetEvent.getWeather()).isEmpty()) {
            PropertyDTO weather = PROPMAPDEF.get("Weather");
            weather.setValue(spreadsheetEvent.getWeather());
            props.put(SpreadsheetEvent.FIELDS.Weather.name(), weather);
        }
        if ((spreadsheetEvent.getNavigation()) != null && !(spreadsheetEvent.getNavigation()).isEmpty()) {
            PropertyDTO navigation = PROPMAPDEF.get("Navigation");
            navigation.setValue(spreadsheetEvent.getNavigation());
            props.put(SpreadsheetEvent.FIELDS.Navigation.name(), navigation);
        }
    }

    //public boolean validateAllTabs(Document document) {
    public boolean validateAllTabs(Workbook document, ErrorDTOList errorList) {
        boolean areTabsOk = true;
        for (String sheetName : getAllowedTabs()) {
            Sheet sheet = document.getSheet(sheetName);
            if (sheet == null) {
                areTabsOk = false;
                errorList.addError(new ErrorDTO(0,
                        String.format("Problem in sheet %s: %s%n", sheetName, "Missing sheet: " + sheetName), null));
            }
        }
        return areTabsOk;
    }

    private List<String> getAllowedTabs() {
        return allowedTabs;
    }

    public boolean validateHeaders(Workbook document, String sheetName, ErrorDTOList errorList) {
        boolean areHeadersOk = true;
        List<String> requiredHeaders = getRequiredHeaders();
        Sheet sheet = document.getSheet(sheetName);
        Set<String> sheetHeaders = findColumnHeadersForSheet(sheet);
        for (String requiredHeader : requiredHeaders) {
            if (!sheetHeaders.contains(requiredHeader)) {
                areHeadersOk = false;
                errorList.addError(new ErrorDTO(0,
                        String.format("Problem in sheet %s: %s%n", sheetName, "Missing header: " + requiredHeader),
                        null));
            }
        }
        return areHeadersOk;
    }

    private Set<String> findColumnHeadersForSheet(Sheet sheet) {
        Set<String> headers = new HashSet<>();
        /*TMP*/int nbCol = 50;
        if (sheet != null) {
            Row row = sheet.getRow(0); //First row should contain the headers
            for (int i = 0; i < nbCol; i++) {
                Cell cell = row.getCell(i);
                if (cell != null) {
                    headers.add(cell.getStringCellValue());
                }
            }
        }
        return headers;
    }

    private List<String> getRequiredHeaders() {
        return requiredHeaders;
    }

    public boolean processSpreadsheetEvents(ErrorDTOList errorList, List<SpreadsheetEvent> data,
            List<EventDTO> events, PersonDTO actor) {
        boolean problems = false;
        int rowNb = 1;
        for (SpreadsheetEvent row : data) {
            try {
                EventDTO event = processSpreadsheetEvent(row, rowNb);
                event.setActor(actor);
                events.add(event);
            } catch (ImportException e) {
                problems = true;
                errorList
                        .addError(new ErrorDTO(rowNb,
                                String.format("Problem on row %s in sheet %s: %s%n", e.lineNb, e.sheetName, e.message),
                                e));
            }
            rowNb++;
        }
        return problems;
    }

    public boolean saveSpreadsheetEvents(ErrorDTOList errorList, List<EventDTO> events) {
        boolean problems = false;
        int i = 1;
        for (EventDTO dto : events) {
            try {
                eventService.save(dto);
            } catch (DataIntegrityViolationException dve) {
                problems = true;
                errorList.addError(new ErrorDTO(i, dve.getMessage(), dve));
            } catch (Exception e) {
                problems = true;
                errorList.addError(new ErrorDTO(i, "General error saving SpreadsheetEventDTO's", e));
            }
            i++;
        }
        return problems;
    }
}
