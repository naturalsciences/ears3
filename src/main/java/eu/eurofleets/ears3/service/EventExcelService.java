package eu.eurofleets.ears3.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.eurofleets.ears3.domain.Acquisition;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.Property;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.domain.Weather;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import eu.eurofleets.ears3.dto.PropertyDTO;
import eu.eurofleets.ears3.utilities.DatagramUtilities;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.IterableUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

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

    static String DEFAULT_PROGRAM = "11BU_operations";
    static Map<String, LinkedDataTermDTO> DEFS = new HashMap<>();

    static {
        LinkedDataTermDTO station = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3", null,
                "Station");
        DEFS.put("Station", station);
        // DEFS.put("Topas", "https://vocab.nerc.ac.uk/collection/L22/current/TOOL0859/");
    }

    static Map<String, PropertyDTO> DEFS_PROPS = new HashMap<>();

    static {
        PropertyDTO dist = new PropertyDTO(
                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_2nn6546541", null,
                        "Distance travelled"),
                null, "nm");
        DEFS_PROPS.put(SpreadsheetEvent.FIELDS.Dist.name(), dist);
    }

    public EventDTO convert(SpreadsheetEvent spreadsheetEvent) {
        EventDTO eventDTO = new EventDTO();
        String processName = spreadsheetEvent.process;
        LinkedDataTermDTO linkedDataTerm = DEFS.get(processName);
        eventDTO.setProcess(linkedDataTerm);
    }

}
