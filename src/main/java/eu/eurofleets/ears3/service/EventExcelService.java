package eu.eurofleets.ears3.service;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.ontology.entities.IToolCategory;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.domain.Weather;
import eu.eurofleets.ears3.dto.*;
import eu.eurofleets.ears3.utilities.DatagramUtilities;

import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import io.github.rushuat.ocell.document.Document;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.glassfish.json.JsonUtil;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import eu.eurofleets.ears3.excel.SpreadsheetEvent;

@Service
public class EventExcelService {

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

    @Autowired
    public EventExcelService(EventRepository eventRepository, Environment env) {

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

    private static List<String> allowedTabs = Arrays.asList("events");
    private List<String> requiredHeaders = Arrays.stream(SpreadsheetEvent.FIELDS.values()).map(Enum::name).collect(Collectors.toList());
    private static String DEFAULT_PROGRAM = "11BU_operations";
    private static String PLATFORM = "Belgica";
    private static Map<String, LinkedDataTermDTO> DEFS = new HashMap<>();

    static {
        /**TODO: Ask Thomas, in EventDTO Station is a string?*/
        //LinkedDataTermDTO station = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3", null, "Station");
        //DEFS.put("Station", station);
        // DEFS.put("Topas", "https://vocab.nerc.ac.uk/collection/L22/current/TOOL0859/");

        /*LinkedDataTermDTO tool = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyTool", null, "Tool");
        DEFS.put("Tool", tool);*/
        LinkedDataTermDTO all = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyTool_All", null,"All");
        DEFS.put("All", all);
        LinkedDataTermDTO belgica = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyTool_Belgica", null,"Belgica");
        DEFS.put("Belgica", belgica);
        LinkedDataTermDTO command = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyTool_Command", null,"Command");
        DEFS.put("Command", command);
        LinkedDataTermDTO commandTeam = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyTool_Command team", null,"Command team");
        DEFS.put("Command team", commandTeam);
        LinkedDataTermDTO crew = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyTool_Crew", null,"Crew");
        DEFS.put("Crew", crew);
        LinkedDataTermDTO everyone = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyTool_Everyone is welcome", null,"Everyone is welcome");
        DEFS.put("Everyone is welcome", everyone);
        LinkedDataTermDTO scientists = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyTool_Scientists", null,"Scientists");
        DEFS.put("Scientists", scientists);
        LinkedDataTermDTO topas = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyTool_Topas", null,"Topas");
        DEFS.put("Topas", topas);

        /*LinkedDataTermDTO process = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess", null, "Process");
        DEFS.put("Process", process);*/
        LinkedDataTermDTO calibration = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Calibration", null,"Calibration");
        DEFS.put("Calibration", calibration);
        LinkedDataTermDTO cruise = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Cruise", null,"Cruise");
        DEFS.put("Cruise", cruise);
        LinkedDataTermDTO demobilisation = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Demobilisation", null,"Demobilisation");
        DEFS.put("Demobilisation", demobilisation);
        LinkedDataTermDTO deployment = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Deployment", null,"Deployment");
        DEFS.put("Deployment", deployment);
        LinkedDataTermDTO exercise = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Exercise", null,"Exercise");
        DEFS.put("Exercise", exercise);
        LinkedDataTermDTO line = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Line", null,"Line");
        DEFS.put("Line", line);
        LinkedDataTermDTO meeting = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Meeting", null,"Meeting");
        DEFS.put("Meeting", meeting);
        LinkedDataTermDTO mobilisation = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Mobilisation", null,"Mobilisation");
        DEFS.put("Mobilisation", mobilisation);
        LinkedDataTermDTO operation = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Operation", null,"Operation");
        DEFS.put("Operation", operation);
        LinkedDataTermDTO recovery = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Recovery", null,"Recovery");
        DEFS.put("Recovery", recovery);
        LinkedDataTermDTO recreation = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Recreation", null,"Recreation");
        DEFS.put("Recreation", recreation);
        LinkedDataTermDTO sheltering = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Sheltering", null,"Sheltering");
        DEFS.put("Sheltering", sheltering);
        LinkedDataTermDTO station = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Station", null,"Station");
        DEFS.put("Station", station);
        LinkedDataTermDTO testing = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Testing", null,"Testing");
        DEFS.put("Testing", testing);
        LinkedDataTermDTO transit = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyProcess_Transit", null,"Transit");
        DEFS.put("Transit", transit);

        /*LinkedDataTermDTO action = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyAction", null, "Action");
        DEFS.put("Action", action);*/
        LinkedDataTermDTO end = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyAction_End", null,"End");
        DEFS.put("End", end);
        LinkedDataTermDTO start = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyAction_Start", null,"Start");
        DEFS.put("Start", start);

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

    public static class ErrorRow {
        int row;
        String msg;
        Exception e;

        public ErrorRow(int row, String msg, Exception e) {
            this.row = row;
            this.msg = msg;
            this.e = e;
        }
    }

    private EventDTO convert(SpreadsheetEvent spreadsheetEvent) {
        EventDTO eventDTO = new EventDTO();
        eventDTO.setIdentifier(null);
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
        /**/LinkedDataTermDTO toolCategory = new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L05/current/50/Johnny_Temp_ToolCategory"+rndNmbr, null, "Johnny_Temp_ToolCategory");
        eventDTO.setToolCategory(toolCategory);
        eventDTO.setPlatform(PLATFORM);


        String uuid =  eventService.findUuidByToolActionProc("", spreadsheetEvent.getTool(), spreadsheetEvent.getProcess(), spreadsheetEvent.getAction());
        eventDTO.setEventDefinitionId(uuid);
        eventDTO.setProgram(DEFAULT_PROGRAM);

        String toolName = spreadsheetEvent.getTool();
        eventDTO.setTool(new ToolDTO(DEFS.get(toolName),null));

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

        Map<String, PropertyDTO> props = new HashMap<>();

        PropertyDTO dist = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_2nn6546541", null,"Distance travelled"), spreadsheetEvent.getDistance(), "nm");
        props.put(SpreadsheetEvent.FIELDS.Dist.name(), dist);
        PropertyDTO time = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#johnny_time", null,"Time"), spreadsheetEvent.getTime(), "u");
        props.put(SpreadsheetEvent.FIELDS.Time.name(), time);
        PropertyDTO status = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#johnny_status", null,"Status"), spreadsheetEvent.getStatus(), null);
        props.put(SpreadsheetEvent.FIELDS.Status.name(), status);
        PropertyDTO region = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#johnny_region", null,"Region"), spreadsheetEvent.getRegion(), null);
        props.put(SpreadsheetEvent.FIELDS.Region.name(), region);
        PropertyDTO weather = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#johnny_weather", null,"Weather"), spreadsheetEvent.getWeather(), null);
        props.put(SpreadsheetEvent.FIELDS.Weather.name(), weather);
        PropertyDTO navigation = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#johnny_navigation", null,"Navigation"), spreadsheetEvent.getNavigation(), null);
        props.put(SpreadsheetEvent.FIELDS.Navigation.name(), navigation);

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

    public boolean processSpreadsheetEvents(List<ErrorRow> errorList, List<SpreadsheetEvent> data, List<EventDTO> events, PersonDTO actor) {
        boolean problems = false;
        int i = 1;
        for (SpreadsheetEvent row : data) {
           try {
               EventDTO event = convert(row);
               event.setActor(actor);
               events.add(event);
           } catch (Exception e) {
               problems = true;
               errorList.add(new ErrorRow(i, "Error processing SpreadsheetEvents", e));
           }
           i++;
        }
        return problems;
    }

    public boolean saveSpreadsheetEvents(List<ErrorRow> errorList, List<EventDTO> events) {
        boolean problems = false;
        int i = 1;
        for (EventDTO dto : events) {
            try {
                eventService.save(dto);
            } catch (Exception e) {
                problems = true;
                errorList.add(new ErrorRow(i, "Error saving SpreadsheetEventDTO's", e));
            }
            i++;
        }
        return problems;
    }
}
