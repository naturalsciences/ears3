/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.dto.EventDTOList;
import eu.eurofleets.ears3.service.CountryRepository;
import eu.eurofleets.ears3.service.CountryService;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.EarsService;
import eu.eurofleets.ears3.service.HarbourRepository;
import eu.eurofleets.ears3.service.HarbourService;
import eu.eurofleets.ears3.service.LinkedDataTermRepository;
import eu.eurofleets.ears3.service.LinkedDataTermService;
import eu.eurofleets.ears3.service.OrganisationRepository;
import eu.eurofleets.ears3.service.OrganisationService;
import eu.eurofleets.ears3.service.PlatformRepository;
import eu.eurofleets.ears3.service.PlatformService;
import eu.eurofleets.ears3.service.ProjectRepository;
import eu.eurofleets.ears3.service.ProjectService;
import eu.eurofleets.ears3.service.SeaAreaRepository;
import eu.eurofleets.ears3.service.SeaAreaService;
import eu.eurofleets.ears3.service.ToolRepository;
import eu.eurofleets.ears3.service.ToolService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.codehaus.jackson.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thomas
 */
@Service(value = "databaseSyncScheduler")
@Configuration
@EnableScheduling
@RestController()
@RequestMapping("sync")
public class DatabaseSyncScheduler {

    public static Logger log = Logger.getLogger(DatabaseSyncScheduler.class.getSimpleName());

    @Autowired
    private LinkedDataTermService linkedDataTermService;

    @Autowired
    private HarbourService harbourService;
    @Autowired
    private SeaAreaService seaAreaService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private PlatformService platformService;
    @Autowired
    private ToolService toolService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private LinkedDataTermRepository linkedDataTermRepo;

    @Autowired
    private Environment env;

    @Bean(name = "asyncExec")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(3);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("AsynchThread-");
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(5 * 60);
        executor.initialize();

        return executor;
    }
    
    
    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(method = {RequestMethod.GET}, path = "database")
    @Async("asyncExec")
    public CompletableFuture<String> syncDatabase() throws JsonProcessingException, MalformedURLException, IOException {
        log.log(Level.INFO, "Syncing database");
        String vesselAddress = env.getProperty("ears.vessel-address");
        OffsetDateTime after=Instant.now().minus(1, ChronoUnit.DAYS).atOffset(ZoneOffset.UTC);
        EventDTOList events = objectMapper.readValue(new URL(vesselAddress+"/dto/events?after="+after.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)), EventDTOList.class);

        return CompletableFuture.completedFuture("Finished syncing database");
    }

    @Scheduled(cron = "0 15 15 * * MON-FRI")
    @Transactional() //used to be Propagation.REQUIRED
    public void synchronizeExternal() throws Exception {

    }
}
