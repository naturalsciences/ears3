package eu.eurofleets.ears3.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Weather;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import eu.eurofleets.ears3.dto.PropertyDTO;
import eu.eurofleets.ears3.utilities.DatagramUtilities;

import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import io.github.rushuat.ocell.document.Document;
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
    static String DEFAULT_PROGRAM = "11BU_operations";
    static Map<String, LinkedDataTermDTO> DEFS = new HashMap<>();

    static {
        LinkedDataTermDTO station = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3", null, "Station");
        DEFS.put("Station", station);
        // DEFS.put("Topas", "https://vocab.nerc.ac.uk/collection/L22/current/TOOL0859/");
    }

    private static Map<String, PropertyDTO> DEFS_PROPS = new HashMap<>();

    static {
        PropertyDTO dist = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_2nn6546541", null,"Distance travelled"), null, "nm");
        DEFS_PROPS.put(SpreadsheetEvent.FIELDS.Dist.name(), dist);

        /*PropertyDTO dist = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_2nn6546541", null,"Distance travelled"), null, "nm");
        DEFS_PROPS.put(SpreadsheetEvent.FIELDS.Dist.name(), dist);
        PropertyDTO dist = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_2nn6546541", null,"Distance travelled"), null, "nm");
        DEFS_PROPS.put(SpreadsheetEvent.FIELDS.Dist.name(), dist);
        PropertyDTO dist = new PropertyDTO( new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_2nn6546541", null,"Distance travelled"), null, "nm");
        DEFS_PROPS.put(SpreadsheetEvent.FIELDS.Dist.name(), dist);*/
    }

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

    public EventDTO convert(SpreadsheetEvent spreadsheetEvent) {
        EventDTO eventDTO = new EventDTO();
        String processName = spreadsheetEvent.getProcess();
        LinkedDataTermDTO linkedDataTerm = DEFS.get(processName);
        eventDTO.setProcess(linkedDataTerm);






        return eventDTO;
    }

    public boolean validateAllTabs(Document document) {
        boolean areTabsOk = true;
        for (String sheetName : getAllowedTabs()) {
            List<SpreadsheetEvent> sheet = document.getSheet(sheetName, SpreadsheetEvent.class);
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
    public boolean validateHeaders(Document document, String sheetName) {
        boolean areHeadersOk = true;
        //@Todo validation
        /*
        List<String> requiredHeaders = getRequiredHeaders();
        List<SpreadsheetEvent> sheets = document.getSheet(sheetName, SpreadsheetEvent.class);
        sheets.forEach(sheet ->{
            System.out.println(sheet);

        });
        */

        return areHeadersOk;
    }

    private List<String> getRequiredHeaders() {
        return requiredHeaders;
    }

    public boolean processSpreadsheetEvents(List<ErrorRow> errorList, List<SpreadsheetEvent> data, List<EventDTO> events) {
        boolean problems = false;
        int i = 1;
        for (SpreadsheetEvent row : data) {
           try {
               EventDTO event = convert(row);
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
