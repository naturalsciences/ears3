package eu.eurofleets.ears3.service;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.ontology.entities.IToolCategory;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eurofleets.ears3.Exceptions.ImportException;
import eu.eurofleets.ears3.controller.rest.EventExcelInputController;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.domain.Weather;
import eu.eurofleets.ears3.dto.*;
import eu.eurofleets.ears3.excel.converters.DateHelper;
import eu.eurofleets.ears3.utilities.DatagramUtilities;

import java.net.MalformedURLException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import io.github.rushuat.ocell.document.Document;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.aspectj.apache.bcel.classfile.Unknown;
//import org.glassfish.json.JsonUtil;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
        LinkedDataTermDTO all = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_100000", null,
                "All persons");
        DEFS.put("All persons", all);
        LinkedDataTermDTO command = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_100001", null,
                "Command");
        DEFS.put("Command", command);
        LinkedDataTermDTO crew = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_1000002", null,
                "Crew");
        DEFS.put("Crew", crew);
        LinkedDataTermDTO scientists = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_100003", null,
                "Scientists");
        DEFS.put("Scientists", scientists);
        LinkedDataTermDTO unknownSparker = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_100004",
                null,
                "Unknown sparker");
        DEFS.put("Unknown sparker", unknownSparker);

        LinkedDataTermDTO topas = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#dev_3292", null, "Topas");
        DEFS.put("Topas", topas);
        LinkedDataTermDTO belgica = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#ves_1792", null,
                "Belgica");
        DEFS.put("Belgica", belgica);

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

    //private static Map<String, PropertyDTO> DEFS_PROPS = new HashMap<>();

    /*static {
        PropertyDTO dist = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_2nn6546541", null,"Distance travelled"), null, "nm");
        DEFS_PROPS.put(SpreadsheetEvent.FIELDS.Dist.name(), dist);
        PropertyDTO time = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#johnny_time", null,"Distance travelled"), null, "u");
        DEFS_PROPS.put(SpreadsheetEvent.FIELDS.Time.name(), time);
        PropertyDTO status = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#johnny_status", null,"Distance travelled"), null, "nm");
        DEFS_PROPS.put(SpreadsheetEvent.FIELDS.Status.name(), status);
        PropertyDTO region = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#johnny_region", null,"Distance travelled"), null, "nm");
        DEFS_PROPS.put(SpreadsheetEvent.FIELDS.Region.name(), region);
        PropertyDTO weather = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#johnny_weather", null,"Distance travelled"), null, "nm");
        DEFS_PROPS.put(SpreadsheetEvent.FIELDS.Weather.name(), weather);
        PropertyDTO navigation = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#johnny_navigation", null,"Distance travelled"), null, "nm");
        DEFS_PROPS.put(SpreadsheetEvent.FIELDS.Navigation.name(), navigation);
    }*/

    private EventDTO processSpreadsheetEvent(SpreadsheetEvent spreadsheetEvent, int rowNb) throws ImportException {
        Set<ConstraintViolation<SpreadsheetEvent>> errors = validator.validate(spreadsheetEvent);
        if (!errors.isEmpty()) {
            System.out.printf("Problem with %s%n", spreadsheetEvent.toString());
            throw new ImportException(EventExcelInputController.SHEETNAME, rowNb,
                    String.format("Problem with %s%n", spreadsheetEvent.toString()), null);
        }

        EventDTO eventDTO = new EventDTO();
        eventDTO.setIdentifier(null);

        String date = spreadsheetEvent.getDate();
        String hour = spreadsheetEvent.getHour();
        try {
            ZonedDateTime zdt = DateHelper.dateTimeStringToZonedDateTime(date, hour);
            eventDTO.setTimeStamp(zdt.toOffsetDateTime());
        } catch (Exception e) {
            int a=5;//break point opportunity
            throw new ImportException(date + " " + hour, rowNb, String.format("Problem with %s%n", spreadsheetEvent.toString()), null);
        }

        /**TODO ADD TO EARS*///eventDTO.setRemarks(spreadsheetEvent.getRemarks());
        /**TODO Verify ... currently Time, Status, Region, Weather, Navigation in the properties collection*/
        /**TODO Date, Hour and Actor still not in the EventDTO
         *
         *
         * Date+ Hour =>LocalDateTime met zone Brussel -> offsetDateTime -> wordt in eventDto opgeslagen
         * OffsetDateTime  waar offset UTC is
         * */
        //eventDTO.setEventDefinitionId(spreadsheetEvent.getEventDefinitionId());
        /**@TODO: Ask Thomas, how do we determine toolCategory And PLATFORM ... I just called it Belgica ... */
        Double rndNmbr = Math.random();
        LinkedDataTermDTO toolCategory = new LinkedDataTermDTO(
                "http://vocab.nerc.ac.uk/collection/L05/current/50/Johnny_Temp_ToolCategory" + rndNmbr, null,
                "Johnny_Temp_ToolCategory");
        eventDTO.setToolCategory(toolCategory);
        eventDTO.setPlatform(platformUrn);

        String uuid = eventService.findUuidByToolActionProc("", spreadsheetEvent.getTool(),
                spreadsheetEvent.getProcess(),
                spreadsheetEvent.getAction());
        eventDTO.setEventDefinitionId(uuid);
        eventDTO.setProgram(DEFAULT_PROGRAM);

        String toolName = spreadsheetEvent.getTool();
        eventDTO.setTool(new ToolDTO(DEFS.get(toolName), null));

        String processName = spreadsheetEvent.getProcess();
        LinkedDataTermDTO process = DEFS.get(processName);
        //if (process == null) System.out.println(processName);
        eventDTO.setProcess(process);

        String actionName = spreadsheetEvent.getAction();
        //*testing with existing one*/eventDTO.setAction(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#act_2", null, "End"));
        eventDTO.setAction(DEFS.get(actionName));

        eventDTO.setLabel(spreadsheetEvent.getLabel());

        String stationName = spreadsheetEvent.getStation();
        //eventDTO.setStation(DEFS.get(stationName)); /**Station seems to be a string in EventDTO*/
        eventDTO.setStation(stationName);

        eventDTO.setDescription(spreadsheetEvent.getDescription());
        eventDTO.setSubject(new LinkedDataTermDTO("https://vocab.nerc.ac.uk/collection/C77/current/M06", null,
                "Routine standard measurements"));

        Map<String, PropertyDTO> props = new HashMap<>();

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

        eventDTO.setProperties(props.values());

        return eventDTO;
    }

    //public boolean validateAllTabs(Document document) {
    public boolean validateAllTabs(Workbook document) {
        boolean areTabsOk = true;
        for (String sheetName : getAllowedTabs()) {
            Sheet sheet = document.getSheet(sheetName);
            //List<SpreadsheetEvent> sheet = document.getSheet(sheetName, SpreadsheetEvent.class);
            if (sheet == null) {
                areTabsOk = false;
            }
        }
        return areTabsOk;
    }

    private List<String> getAllowedTabs() {
        return allowedTabs;
    }

    /**@TODO    */
    //    public boolean validateHeaders(Document document, String sheetName) {
    public boolean validateHeaders(Workbook document, String sheetName) {
        boolean areHeadersOk = true;
        //@Todo validation
        //*
        List<String> requiredHeaders = getRequiredHeaders();
        //List<SpreadsheetEvent> sheets = document.getSheet(sheetName, SpreadsheetEvent.class);
        Sheet sheet = document.getSheet(sheetName);
        Set<String> sheetHeaders = findColumnHeadersForSheet(sheet);
        for (String requiredHeader : requiredHeaders) {
            /*if (!actualHeaders.containsKey(correctHeader)) {
                throw new WrongTemplateException("Sheet '%s' should contain mandatory header '%s'.",
                        converter.getSheetName(), correctHeader);
            }*/
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

    public boolean processSpreadsheetEvents(List<ErrorDTO> errorList, List<SpreadsheetEvent> data,
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
                        .add(new ErrorDTO(rowNb,
                                String.format("Problem on row %s in sheet %s: %s%n", e.lineNb, e.sheetName, e.message),
                                e));
            }
            rowNb++;
        }
        return problems;
    }

    public boolean saveSpreadsheetEvents(List<ErrorDTO> errorList, List<EventDTO> events) {
        boolean problems = false;
        int i = 1;
        for (EventDTO dto : events) {
            try {
                eventService.save(dto);
            } catch (Exception e) {
                problems = true;
                errorList.add(new ErrorDTO(i, "Error saving SpreadsheetEventDTO's", e));
            }
            i++;
        }
        return problems;
    }
}
