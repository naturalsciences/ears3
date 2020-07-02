/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler;

import be.naturalsciences.bmdc.cruise.model.IConcept;
import eu.eurofleets.ears3.domain.Country;
import eu.eurofleets.ears3.domain.Harbour;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.domain.Project;
import eu.eurofleets.ears3.domain.SeaArea;
import eu.eurofleets.ears3.domain.Tool;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thomas
 */
@Service(value = "syncScheduler")
@Configuration
@EnableScheduling
@RestController()
@RequestMapping("sync")
public class SyncScheduler {

    public static Logger log = Logger.getLogger(SyncScheduler.class.getSimpleName());

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

    private Map<Class, EarsService> REPOS = new HashMap<>();

    @PostConstruct
    private void init() {
        REPOS.put(Harbour.class, harbourService);
        REPOS.put(SeaArea.class, seaAreaService);
        REPOS.put(Country.class, countryService);
        REPOS.put(Tool.class, toolService);
        REPOS.put(Project.class, projectService);
    }

    @RequestMapping(method = {RequestMethod.POST}, path = "ships")
    public void syncShips() {
        log.log(Level.INFO, "Syncing ships");
        try {
            ExternalHelper sujParPlatformClassLoader = new ExternalHelper(LinkedDataTerm.class, null);

            Map<String, LinkedDataTerm> sujParPlatforms = sujParPlatformClassLoader.retrieve();
            List<LinkedDataTerm> ldts = new ArrayList<>();
            for (Map.Entry<String, LinkedDataTerm> entry : sujParPlatforms.entrySet()) {
                LinkedDataTerm term = entry.getValue();
                ldts.add(term);
            }

            ExternalHelper shipLoader = new ExternalHelper(Platform.class, new PlatformCopyAssistant());

            Map<String, Platform> platforms = shipLoader.retrieve();
            for (Map.Entry<String, Platform> entry : platforms.entrySet()) {
                Platform platform = entry.getValue();
                String platformClassIdentifier = null;
                if (platform.getPlatformClass() != null) {
                    platformClassIdentifier = platform.getPlatformClass().getIdentifier();
                }
                if (platformClassIdentifier != null) {
                    LinkedDataTerm platformClass = linkedDataTermService.findByIdentifier(platformClassIdentifier);
                    platform.setPlatformClass(platformClass);

                }
                ldts.add((LinkedDataTerm) platform.getTerm());
            }
            linkedDataTermService.saveAll(ldts);
            platformService.saveAll(platforms.values());
        } catch (Exception e) {
            log.log(Level.SEVERE, "exception!", e);
            throw e;
        }
    }

    @RequestMapping(method = {RequestMethod.POST}, path = "organisations")
    private void syncOrganisations() throws Exception {
        log.log(Level.INFO, "Syncing organisations");
        try {
            ExternalOrganisationHelper loader = new ExternalOrganisationHelper();

            Map<String, Organisation> organisations = loader.retrieve();
            List<LinkedDataTerm> ldts = new ArrayList<>();
            for (Map.Entry<String, Organisation> entry : organisations.entrySet()) {
                Organisation organisation = entry.getValue();
                ldts.add((LinkedDataTerm) organisation.getTerm());
                Country country = (Country) organisation._getCountry();  //country without an identifier!
                if (country != null) {
                    country = countryService.findByName(country.getTerm().getName()); //replace the country with a managed entity; search by name
                    organisation._setCountry(country);
                }
            }
            linkedDataTermService.saveAll(ldts);
            organisationService.saveAll(organisations.values());
        } catch (Exception e) {
            log.log(Level.SEVERE, "exception!", e);
            throw e;
        }
    }

    @RequestMapping(method = {RequestMethod.POST}, path = "harbours")
    private void syncHarbours() {
        log.log(Level.INFO, "Syncing harbours");
        try {
            ExternalHelper loader = new ExternalHelper(Harbour.class, null);
            Map<String, Harbour> harbours = loader.retrieve();
            List<LinkedDataTerm> ldts = new ArrayList<>();
            for (Map.Entry<String, Harbour> entry : harbours.entrySet()) {
                Harbour harbour = entry.getValue();
                ldts.add((LinkedDataTerm) harbour.getTerm());
                Country country = null;
                if (harbour._getCountry() != null) {
                    country = countryService.findByIdentifier(country.getTerm().getIdentifier()); //replace the country with a managed entity
                    harbour._setCountry(country);
                }
            }
            linkedDataTermService.saveAll(ldts);
            harbourService.saveAll(harbours.values());
        } catch (Exception e) {
            log.log(Level.SEVERE, "exception!", e);
            throw e;
        }
    }

    @RequestMapping(method = {RequestMethod.POST}, path = "seas")
    private void syncSeas() {
        sync(SeaArea.class, new ExternalHelper(SeaArea.class, null));
    }

    @RequestMapping(method = {RequestMethod.POST}, path = "countries")
    private void syncCountries() {
        sync(Country.class, new ExternalHelper(Country.class, null));
    }

    @RequestMapping(method = {RequestMethod.POST}, path = "tools")
    private void syncTools() {
        sync(Tool.class, new ExternalHelper(Tool.class, null));
    }

    @RequestMapping(method = {RequestMethod.POST}, path = "projects")
    private void syncProjects() {
        sync(Project.class, new ExternalProjectHelper());
    }

    @RequestMapping(method = {RequestMethod.POST}, path = "all")
    public void sync() throws Exception {

        synchronizeExternal();
    }

    private <C extends IConcept> void sync(Class<C> cls, IExternalHelper loader) {
        log.log(Level.INFO, "Syncing " + cls.getSimpleName());
        try {
            Map<String, C> things = loader.retrieve();
            List<LinkedDataTerm> ldts = new ArrayList<>();
            for (Map.Entry<String, C> entry : things.entrySet()) {
                C thing = entry.getValue();
                ldts.add((LinkedDataTerm) thing.getTerm());
            }
            linkedDataTermService.saveAll(ldts);
            REPOS.get(cls).saveAll(things.values());
        } catch (Exception e) {
            log.log(Level.SEVERE, "exception!", e);
            throw e;
        }
    }

    @Scheduled(cron = "0 10 0 * * MON-FRI")
    @Transactional() //used to be Propagation.REQUIRED
    public void synchronizeExternal() throws Exception {
        syncShips();
        syncSeas();
        syncCountries();
        syncHarbours();
        syncTools();
        syncOrganisations();
        syncProjects();
    }
}
