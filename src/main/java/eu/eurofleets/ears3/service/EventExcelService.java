package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.Exceptions.ImportException;
import eu.eurofleets.ears3.controller.rest.EventExcelController;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Weather;
import eu.eurofleets.ears3.dto.*;
import eu.eurofleets.ears3.excel.SpreadsheetEvent;
import eu.eurofleets.ears3.excel.converters.DateHelper;
import eu.eurofleets.ears3.rdf.OntologySparqlService;
import eu.eurofleets.ears3.utilities.DatagramUtilities;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.type.CollectionType;

@Service
public class EventExcelService {

    private Validator validator;

    private final EventRepository eventRepository;
    private final Boolean readOnly;
    private final String navServer;
    private final String platformUrn;

    @Autowired
    private ProgramService programService;

    @Autowired
    private EventService eventService;

    private final OntologySparqlService sparqlService;

    private DatagramUtilities<Navigation> navUtil;
    private DatagramUtilities<Thermosal> thermosalUtil;
    private DatagramUtilities<Weather> weatherUtil;

    public static Logger log = Logger.getLogger(EventService.class.getSimpleName());

    private static final List<String> ALLOWED_TABS = List.of("events");

    private static final List<String> OPTIONAL_HEADERS = List.of(
            "Dist", "Elapsed Time", "Status", "Region", "Weather", "Navigation");

    private static final List<String> REQUIRED_HEADERS = Arrays.stream(SpreadsheetEvent.FIELDS.values())
            .map(f -> f.name().replace('_', ' '))
            .filter(name -> !OPTIONAL_HEADERS.contains(name))
            .collect(Collectors.toList());

    private static final Map<String, LinkedDataTermDTO> DEFS = new HashMap<>();
    private static final Map<String, LinkedDataTermDTO> CATMAP = new HashMap<>();
    private static final Map<String, PropertyDTO> PROPMAPDEF = new HashMap<>();

    @Autowired
    public EventExcelService(EventRepository eventRepository,
                             @Value("${app.navigation.server}") String navServer,
                             @Value("${app.read-only}") Boolean readOnly,
                             @Value("${app.platform}") String platformUrn,
                             OntologySparqlService sparqlService) {
        this.eventRepository = eventRepository;
        this.navServer = navServer;
        this.readOnly = readOnly;
        this.platformUrn = platformUrn;
        this.sparqlService = sparqlService;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        try {
            navUtil = new DatagramUtilities<>(Navigation.class, navServer);
            thermosalUtil = new DatagramUtilities<>(Thermosal.class, navServer);
            weatherUtil = new DatagramUtilities<>(Weather.class, navServer);
            initializeHashmaps();
            loadRdfBindingsIntoMaps(); // live ontology data, layered on top of the static JSON fallback
        } catch (MalformedURLException ex) {
            Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void loadRdfBindingsIntoMaps() {
        try {
            String json = sparqlService.executeBindings();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(json);
            JsonNode bindings = root.path("results").path("bindings");

            int toolCount = 0, processCount = 0, actionCount = 0;
            for (JsonNode binding : bindings) {
                String toolLabel = textOrNull(binding, "tl");
                String toolUri = textOrNull(binding, "tu");
                String toolTransitiveUri = textOrNull(binding, "ttu");

                String categoryLabel = textOrNull(binding, "cl");
                String categoryUri = textOrNull(binding, "cu");
                String categoryTransitiveUri = textOrNull(binding, "ctu");

                String processLabel = textOrNull(binding, "pl");
                String processUri = textOrNull(binding, "pu");

                String actionLabel = textOrNull(binding, "al");
                String actionUri = textOrNull(binding, "au");

                if (toolLabel != null && toolUri != null) {
                    String key = loweredCapitalize(toolLabel);
                    DEFS.put(key, new LinkedDataTermDTO(toolUri, toolTransitiveUri, toolLabel));
                    toolCount++;
                    if (categoryUri != null) {
                        CATMAP.put(key, new LinkedDataTermDTO(categoryUri, categoryTransitiveUri, categoryLabel));
                    }
                }
                if (processLabel != null && processUri != null) {
                    DEFS.put(loweredCapitalize(processLabel), new LinkedDataTermDTO(processUri, null, processLabel));
                    processCount++;
                }
                if (actionLabel != null && actionUri != null) {
                    DEFS.put(loweredCapitalize(actionLabel), new LinkedDataTermDTO(actionUri, null, actionLabel));
                    actionCount++;
                }
            }
            log.info(String.format("Loaded live ontology bindings for excel import: %d tools, %d processes, %d actions.",
                    toolCount, processCount, actionCount));
        } catch (Exception e) {
            log.log(Level.WARNING, "Could not load live ontology bindings for excel import - "
                    + "falling back to static custom_ldts.json mappings only.", e);
        }
    }

    private static String textOrNull(JsonNode binding, String field) {
        JsonNode node = binding.path(field).path("value");
        return node.isMissingNode() ? null : node.asText(null);
    }

    private void initializeHashmaps() throws IOException {

        JsonNode rootNode;
        ObjectMapper objectMapper;
        objectMapper = new ObjectMapper();
        File jsonFile = new ClassPathResource("static/json/custom_ldts.json").getFile();
        rootNode = objectMapper.readTree(jsonFile);

        CollectionType type = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, LinkedHashMap.class);

        JsonNode defs = rootNode.get("defs");
        ArrayList<LinkedHashMap<String, String>> defList = objectMapper.convertValue(defs, type);

        JsonNode catmap = rootNode.get("catmap");
        ArrayList<LinkedHashMap<String, String>> cmList = objectMapper.convertValue(catmap, type);

        JsonNode properties = rootNode.get("properties");
        ArrayList<LinkedHashMap<String, String>> propList = objectMapper.convertValue(properties, type);

        for (LinkedHashMap<String, String> item : defList) {
            LinkedDataTermDTO ldtDTO = new LinkedDataTermDTO(item.get("identifier"), item.get("transitiveLdIdentifier"),
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
            LinkedDataTermDTO ldtDTO = new LinkedDataTermDTO(item.get("identifier"), item.get("transitiveLdIdentifier"),
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
            throw new ImportException(EventExcelController.SHEETNAME, rowNb,
                    String.format("Unknown Linked Data Term [ %s ]", synonym), null);
        } else {
            return targetLDT;
        }
    }

    public LinkedDataTermDTO extractToolCategory(String synonym, int rowNb) throws ImportException {
        synonym = loweredCapitalize(synonym);
        LinkedDataTermDTO targetLDT = CATMAP.get(synonym);
        if (targetLDT == null) {
            throw new ImportException(EventExcelController.SHEETNAME, rowNb,
                    String.format("Unknown ToolCategory [ %s ]", synonym), null);
        } else {
            return targetLDT;
        }
    }

    private static ZonedDateTime createZonedDateTime(SpreadsheetEvent spreadsheetEvent, int rowNb, String timezone)
            throws ImportException {
        ZonedDateTime zdt = null;
        String date = spreadsheetEvent.getDate();
        String time = spreadsheetEvent.getTime();
        try {
            zdt = DateHelper.dateTimeStringToZonedDateTime(date, time, ZoneId.of(timezone));
        } catch (Exception e) {
            throw new ImportException(EventExcelController.SHEETNAME, rowNb,
                    String.format("Problem with [%s]%n", e.getMessage()), null);
        }
        return zdt;
    }

    private EventDTO processSpreadsheetEvent(SpreadsheetEvent spreadsheetEvent, int rowNb, PersonDTO actor, String programIdentifier, String timezone) throws ImportException {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setIdentifier(null);
        eventDTO.setActor(actor);
        ArrayList<String> errorSummaryForRow = new ArrayList<>();
        Set<ConstraintViolation<SpreadsheetEvent>> errors = validator.validate(spreadsheetEvent);
        if (!errors.isEmpty()) {
            System.out.printf("Problem with %s%n", spreadsheetEvent.toString());
            errors.forEach(error -> {
                errorSummaryForRow.add(error.getPropertyPath() + " " + error.getMessage());
            });
        }
        if (programIdentifier == null) { //overrides the program identifier set in the excel sheet
            programIdentifier = spreadsheetEvent.getProgram();
        }
        Program program = programService.findOrCreateProgram(programIdentifier);
        if (program != null) {
            eventDTO.setProgram(program.getIdentifier());
        } else {
            errorSummaryForRow.add("\nError setting the Program [" + spreadsheetEvent.getProgram() + "].\n");
        }

        if (!errorSummaryForRow.isEmpty()) {
            System.out.println(errorSummaryForRow.toString());
            throw new ImportException(EventExcelController.SHEETNAME, rowNb,
                    String.format("Problem with %s%n", errorSummaryForRow.toString()), null);
        }

        ZonedDateTime zdt = createZonedDateTime(spreadsheetEvent, rowNb,timezone);
        eventDTO.setTimeStamp(zdt.toOffsetDateTime());
        eventDTO.setRemarks(spreadsheetEvent.getRemarks());
        eventDTO.setPlatform(platformUrn);

        String uuid = eventService.findUuidByToolActionProc("", spreadsheetEvent.getTool(),
                spreadsheetEvent.getProcess(),
                spreadsheetEvent.getAction());
        eventDTO.setEventDefinitionId(uuid);

        String toolName = spreadsheetEvent.getTool();
        if (toolName.equals("Command")) {
            int a = 5;
        }
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
        } //this clashes with time
        /*if ((spreadsheetEvent.getTime()) != null && !(spreadsheetEvent.getTime()).isEmpty()) {
            PropertyDTO time = PROPMAPDEF.get("Time");
            time.setValue(spreadsheetEvent.getTime());
            props.put(SpreadsheetEvent.FIELDS.Time.name(), time);
        }*/
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
        for (String sheetName : ALLOWED_TABS) {
            Sheet sheet = document.getSheet(sheetName);
            if (sheet == null) {
                areTabsOk = false;
                errorList.addError(new ErrorDTO(0,
                        String.format("Problem in sheet %s: %s%n", sheetName, "Missing sheet: " + sheetName), null));
            }
        }
        return areTabsOk;
    }

    public boolean validateHeaders(Workbook document, String sheetName, ErrorDTOList errorList) {
        boolean areHeadersOk = true;
        Sheet sheet = document.getSheet(sheetName);
        Set<String> sheetHeaders = findColumnHeadersForSheet(sheet);
        for (String requiredHeader : REQUIRED_HEADERS) {
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
        /*TMP*/
        int nbCol = 50;
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

    private List<String> getREQUIRED_HEADERS() {
        return REQUIRED_HEADERS;
    }

    public boolean processSpreadsheetEvents(ErrorDTOList errorList, List<SpreadsheetEvent> data,
                                            List<EventDTO> events, PersonDTO actor, String program, String timezone) {
        boolean hasProblems = false;
        int rowNb = 1;
        for (SpreadsheetEvent spreadsheetEvent : data) {
            try {
                EventDTO event = processSpreadsheetEvent(spreadsheetEvent, rowNb, actor, program, timezone);
                events.add(event);
            } catch (ImportException e) {
                hasProblems = true;
                errorList.addError(new ErrorDTO(rowNb, String.format("Problem on row %s in sheet %s: %s%n", e.lineNb, e.sheetName, e.message), e));
            }
            rowNb++;
        }
        return hasProblems;
    }

    public boolean saveSpreadsheetEvents(ErrorDTOList errorList, List<EventDTO> events) {
        boolean problems = false;
        int i = 1;
        for (EventDTO dto : events) {
            try {
                eventService.save(dto);
            } catch (DataIntegrityViolationException dve) {
                problems = true;
                String msg = (dve.getMessage().split("\\] \\[")[0]) + "]";
                errorList.addError(new ErrorDTO(i, msg, dve));

            } catch (Exception e) {
                problems = true;
                errorList.addError(new ErrorDTO(i, e.getMessage() + "exception saving event row", e));
            }
            i++;
        }
        return problems;
    }
}
