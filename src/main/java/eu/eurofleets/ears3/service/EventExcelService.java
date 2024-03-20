package eu.eurofleets.ears3.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eurofleets.ears3.Exceptions.ImportException;
import eu.eurofleets.ears3.controller.rest.EventExcelInputController;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Weather;
import eu.eurofleets.ears3.dto.*;
import eu.eurofleets.ears3.excel.converters.DateHelper;
import eu.eurofleets.ears3.utilities.DatagramUtilities;

import java.net.MalformedURLException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
//import org.glassfish.json.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import eu.eurofleets.ears3.excel.SpreadsheetEvent;

@Service
public class EventExcelService {

    private Validator validator;

    private final EventRepository eventRepository;

    @Autowired
    private LinkedDataTermService ldtService;
    @Autowired
    private ToolService toolService;
    @Autowired
    private ProgramService programService;
    @Autowired
    private PersonService personService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private PlatformService platformService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private NavigationService navigationService;
    @Autowired
    private ThermosalService thermosalService;
    @Autowired
    private WeatherService weatherService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private DatagramUtilities<Navigation> navUtil;
    private DatagramUtilities<Thermosal> thermosalUtil;
    private DatagramUtilities<Weather> weatherUtil;
    public static Logger log = Logger.getLogger(EventService.class.getSimpleName());

    @Autowired
    private final Environment env;

    private static List<String> allowedTabs = Arrays.asList("events");
    private List<String> requiredHeaders = Arrays.stream(SpreadsheetEvent.FIELDS.values()).map(Enum::name)
            .collect(Collectors.toList());
    private static String DEFAULT_PROGRAM = "11BU_operations";
    // private static String PLATFORM = "SDN:C17::11BU";

    @Value("${ears.platform}")
    public String platformUrn;

    private static Map<String, LinkedDataTermDTO> DEFS = new HashMap<>();
    private static Map<String, LinkedDataTermDTO> CATMAP = new HashMap<>();

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
        } catch (MalformedURLException ex) {
            Logger.getLogger(EventService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static {
        /**TODO: Ask Thomas, in EventDTO Station is a string?*/
        //LinkedDataTermDTO station = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3", null, "Station");
        //DEFS.put("Station", station);
        // DEFS.put("Topas", "https://vocab.nerc.ac.uk/collection/L22/current/TOOL0859/");

        /*LinkedDataTermDTO tool = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyTool", null, "Tool");
        DEFS.put("Tool", tool);*/
        LinkedDataTermDTO human = new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L06/current/71/",
                null, "human");
        DEFS.put("Human", human);

        LinkedDataTermDTO all = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_100000", null,
                "All persons");
        DEFS.put("All persons", all);
        CATMAP.put("All persons", human);
        LinkedDataTermDTO command = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_100001", null,
                "Command");
        DEFS.put("Command", command);
        CATMAP.put("Command", human);
        LinkedDataTermDTO crew = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_1000002", null,
                "Crew");
        DEFS.put("Crew", crew);
        CATMAP.put("Crew", human);
        LinkedDataTermDTO scientists = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_100003", null,
                "Scientists");
        DEFS.put("Scientists", scientists);
        CATMAP.put("Scientists", human);
        LinkedDataTermDTO sparker = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#ctg_123",
                null,
                "Sparker");
        DEFS.put("Sparker", sparker);
        CATMAP.put("Sparker", sparker);
        LinkedDataTermDTO unknownSparker = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_100004",
                null,
                "Unknown sparker");
        DEFS.put("Unknown sparker", unknownSparker);
        CATMAP.put("Unknown sparker", sparker);

        LinkedDataTermDTO topas = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_3292", null, "Topas");
        DEFS.put("Topas", topas);

        LinkedDataTermDTO rv = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#ctg_26", null,
                "Research vessel");
        DEFS.put("Research vessel", rv);
        LinkedDataTermDTO belgica = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#ves_1792", null,
                "Belgica");
        DEFS.put("Belgica", belgica);
        CATMAP.put("Belgica", rv);

        LinkedDataTermDTO calibration = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_6", null,
                "Calibration");
        DEFS.put("Calibration", calibration);
        LinkedDataTermDTO cruise = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_20", null, "Cruise");
        DEFS.put("Cruise", cruise);
        LinkedDataTermDTO deployment = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_22", null,
                "Deployment");
        DEFS.put("Deployment", deployment);
        LinkedDataTermDTO line = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_12", null, "Line");
        DEFS.put("Line", line);

        LinkedDataTermDTO operation = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_28", null,
                "Operation");
        DEFS.put("Operation", operation);
        LinkedDataTermDTO recovery = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_23", null,
                "Recovery");
        DEFS.put("Recovery", recovery);
        LinkedDataTermDTO transit = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_14", null,
                "Transit");
        DEFS.put("Transit", transit);
        LinkedDataTermDTO station = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3", null,
                "Station");
        DEFS.put("Station", station);

        LinkedDataTermDTO testing = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_100001", null,
                "Testing");
        DEFS.put("Testing", testing);
        LinkedDataTermDTO exercise = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_100002", null,
                "Exercise");
        DEFS.put("Exercise", exercise);
        LinkedDataTermDTO meeting = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_100003", null,
                "Meeting");
        DEFS.put("Meeting", meeting);
        LinkedDataTermDTO recreation = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_100004", null,
                "Recreation");
        DEFS.put("Recreation", recreation);
        LinkedDataTermDTO sheltering = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_100005", null,
                "Sheltering");
        DEFS.put("Sheltering", sheltering);
        LinkedDataTermDTO demobilisation = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_100006",
                null, "Demobilisation");
        DEFS.put("Demobilisation", demobilisation);
        LinkedDataTermDTO mobilisation = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_100007", null,
                "Mobilisation");
        DEFS.put("Mobilisation", mobilisation);

        LinkedDataTermDTO start = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#act_1", null, "Start");
        DEFS.put("Start", start);
        LinkedDataTermDTO end = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#act_2", null, "End");
        DEFS.put("End", end);
    }

    private String loweredCapitalize(String input){ return StringUtils.capitalize(input.toLowerCase()); }

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
    private static ZonedDateTime createZonedDateTime(SpreadsheetEvent spreadsheetEvent, int rowNb) throws  ImportException{
        ZonedDateTime zdt = null;
        String date = spreadsheetEvent.getDate();
        String hour = spreadsheetEvent.getHour();
        try {
            zdt = DateHelper.dateTimeStringToZonedDateTime(date, hour);
        } catch (Exception e) {
            int a=5;//break point opportunity
            throw new ImportException(date + " " + hour, rowNb, String.format("Problem with %s%n", spreadsheetEvent.toString()), null);
        }
        return zdt;
    }

    private EventDTO processSpreadsheetEvent(SpreadsheetEvent spreadsheetEvent, int rowNb) throws ImportException {
        Set<ConstraintViolation<SpreadsheetEvent>> errors = validator.validate(spreadsheetEvent);
        if (!errors.isEmpty()) {
            System.out.printf("Problem with %s%n", spreadsheetEvent.toString());
            throw new ImportException(EventExcelInputController.SHEETNAME, rowNb,
                    String.format("Problem with %s%n", spreadsheetEvent.toString()), null);
        }

        EventDTO eventDTO = new EventDTO();
        eventDTO.setIdentifier(null);

        ZonedDateTime zdt = createZonedDateTime(spreadsheetEvent, rowNb);
        eventDTO.setTimeStamp(zdt.toOffsetDateTime());


        /**TODO ADD TO EARS*///eventDTO.setRemarks(spreadsheetEvent.getRemarks());

        eventDTO.setPlatform(platformUrn);

        String uuid = eventService.findUuidByToolActionProc("", spreadsheetEvent.getTool(),
                spreadsheetEvent.getProcess(),
                spreadsheetEvent.getAction());
        eventDTO.setEventDefinitionId(uuid);

        Program program = programService.findOrCreateProgram(spreadsheetEvent.getProgram());
        if (program != null) {
            eventDTO.setProgram(program.getIdentifier());
        } else {
            throw new ImportException( EventExcelInputController.SHEETNAME, rowNb,
                    String.format("Error setting the Program [%s]%n.", spreadsheetEvent.getProgram()), null);
        }

        String toolName = spreadsheetEvent.getTool();
//        eventDTO.setTool(new ToolDTO(DEFS.get(loweredCapitalize(toolName)), null));
        eventDTO.setTool(new ToolDTO(extractLDT(toolName, rowNb), null));
        /*Double rndNmbr = Math.random();
        LinkedDataTermDTO toolCategory = new LinkedDataTermDTO(
                "http://vocab.nerc.ac.uk/collection/L05/current/50/Johnny_Temp_ToolCategory" + rndNmbr, null,
                "Johnny_Temp_ToolCategory");*/
        LinkedDataTermDTO toolCategory = extractToolCategory(toolName, rowNb);
        eventDTO.setToolCategory(toolCategory);



        String processName = spreadsheetEvent.getProcess();
//        LinkedDataTermDTO process = DEFS.get(loweredCapitalize(processName));
        LinkedDataTermDTO process = extractLDT(processName, rowNb);
        //if (process == null) System.out.println(processName);
        eventDTO.setProcess(process);

        String actionName = spreadsheetEvent.getAction();
        //*testing with existing one*/eventDTO.setAction(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#act_2", null, "End"));
//        eventDTO.setAction(DEFS.get(loweredCapitalize(actionName)));
        eventDTO.setAction(extractLDT(actionName, rowNb));

        eventDTO.setLabel(spreadsheetEvent.getLabel());

        String stationName = spreadsheetEvent.getStation();
        //eventDTO.setStation(DEFS.get(stationName)); /**Station seems to be a string in EventDTO*/
        eventDTO.setStation(stationName);

        eventDTO.setDescription(spreadsheetEvent.getDescription());
        eventDTO.setSubject(new LinkedDataTermDTO("https://vocab.nerc.ac.uk/collection/C77/current/M06", null,
                "Routine standard measurements"));

        Map<String, PropertyDTO> props = new HashMap<>();
        createPropertiesIfAvailable(spreadsheetEvent, props);
        eventDTO.setProperties(props.values());

        return eventDTO;
    }

    /*private Program findOrCreateProgram(String programName) {
        Program program = programService.findByIdentifier(programName);
        if( program == null ){
            if( programName.equalsIgnoreCase(DEFAULT_PROGRAM) ){
                program = new Program();
                program.setIdentifier(String.format("%s_operations", platformUrn.replace("SDN:C17::", "")));
                program.setName("General Belgica Operations");
                program = programService.save(program);
            } else {
                String programWithYear = programName + "_" + ZonedDateTime.now().getYear();
                program = programService.findByIdentifier(programWithYear);
            }
        }
        return program;
    }*/

    private static void createPropertiesIfAvailable(SpreadsheetEvent spreadsheetEvent, Map<String, PropertyDTO> props) {
        if ((spreadsheetEvent.getDistance()) != null && !(spreadsheetEvent.getDistance()).isEmpty()) {
        PropertyDTO dist = new PropertyDTO(
                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_100000", null, "Distance travelled"),
                spreadsheetEvent.getDistance(), "nm");
        props.put(SpreadsheetEvent.FIELDS.Dist.name(), dist);
        }
        if ((spreadsheetEvent.getDistance()) != null && !(spreadsheetEvent.getTime()).isEmpty()) {
        PropertyDTO time = new PropertyDTO(
                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_100001", null, "Time"),
                spreadsheetEvent.getTime(), "h");
        props.put(SpreadsheetEvent.FIELDS.Time.name(), time);
        }
        if ((spreadsheetEvent.getStatus()) != null && !(spreadsheetEvent.getStatus()).isEmpty()) {
        PropertyDTO status = new PropertyDTO(
                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_100002", null, "Status"),
                spreadsheetEvent.getStatus(), null);
        props.put(SpreadsheetEvent.FIELDS.Status.name(), status);
        }
        if ((spreadsheetEvent.getRegion()) != null && !(spreadsheetEvent.getRegion()).isEmpty()) {
        PropertyDTO region = new PropertyDTO(
                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_100003", null, "Region"),
                spreadsheetEvent.getRegion(), null);
        props.put(SpreadsheetEvent.FIELDS.Region.name(), region);
        }
        if ((spreadsheetEvent.getWeather()) != null && !(spreadsheetEvent.getWeather()).isEmpty()) {
        PropertyDTO weather = new PropertyDTO(
                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_100004", null, "Weather"),
                spreadsheetEvent.getWeather(), null);
        props.put(SpreadsheetEvent.FIELDS.Weather.name(), weather);
        }
        if ((spreadsheetEvent.getNavigation()) != null && !(spreadsheetEvent.getNavigation()).isEmpty()) {
        PropertyDTO navigation = new PropertyDTO(
                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_100005", null, "Navigation"),
                spreadsheetEvent.getNavigation(), null);
        props.put(SpreadsheetEvent.FIELDS.Navigation.name(), navigation);
        }
    }

    //public boolean validateAllTabs(Document document) {
    public boolean validateAllTabs(Workbook document, ErrorDTOList errorList) {
        boolean areTabsOk = true;
        for (String sheetName : getAllowedTabs()) {
            Sheet sheet = document.getSheet(sheetName);
            //List<SpreadsheetEvent> sheet = document.getSheet(sheetName, SpreadsheetEvent.class);
            if (sheet == null) {
                areTabsOk = false;
                errorList.addError(new ErrorDTO(0, String.format("Problem in sheet %s: %s%n", sheetName, "Missing sheet: " + sheetName), null));
            }
        }
        return areTabsOk;
    }

    private List<String> getAllowedTabs() {
        return allowedTabs;
    }

    /**@TODO    */
    //    public boolean validateHeaders(Document document, String sheetName) {
    public boolean validateHeaders(Workbook document, String sheetName, ErrorDTOList errorList) {
        boolean areHeadersOk = true;
        List<String> requiredHeaders = getRequiredHeaders();
        Sheet sheet = document.getSheet(sheetName);
        Set<String> sheetHeaders = findColumnHeadersForSheet(sheet);
        for (String requiredHeader : requiredHeaders) {
            if ( !sheetHeaders.contains(requiredHeader) ){
                areHeadersOk = false;
                errorList.addError(new ErrorDTO(0, String.format("Problem in sheet %s: %s%n", sheetName, "Missing header: " + requiredHeader), null));
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
            } catch (DataIntegrityViolationException dve ) {
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
