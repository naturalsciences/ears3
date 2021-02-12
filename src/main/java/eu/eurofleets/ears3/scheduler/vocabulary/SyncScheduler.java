/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler.vocabulary;

import be.naturalsciences.bmdc.cruise.model.IConcept;
import eu.eurofleets.ears3.domain.Country;
import eu.eurofleets.ears3.domain.Harbour;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.domain.Project;
import eu.eurofleets.ears3.domain.SeaArea;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.service.CountryService;
import eu.eurofleets.ears3.service.EarsService;
import eu.eurofleets.ears3.service.HarbourService;
import eu.eurofleets.ears3.service.LinkedDataTermRepository;
import eu.eurofleets.ears3.service.LinkedDataTermService;
import eu.eurofleets.ears3.service.OrganisationService;
import eu.eurofleets.ears3.service.PlatformService;
import eu.eurofleets.ears3.service.ProjectService;
import eu.eurofleets.ears3.service.SeaAreaService;
import eu.eurofleets.ears3.service.ToolService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    private static Map<String, String> VESSEL_OPERATORS = new HashMap<>();

    static {
        VESSEL_OPERATORS.put("SDN:C17::45CE", "SDN:EDMO::396");
        VESSEL_OPERATORS.put("SDN:C17::26MH", "SDN:EDMO::3084");
        VESSEL_OPERATORS.put("SDN:C17::34A3", "SDN:EDMO::1104");
        VESSEL_OPERATORS.put("SDN:C17::11SS", "SDN:EDMO::422");
        VESSEL_OPERATORS.put("SDN:C17::77SK", "SDN:EDMO::1334");
        VESSEL_OPERATORS.put("SDN:C17::36AE", "SDN:EDMO::3051");
        VESSEL_OPERATORS.put("SDN:C17::11BE", "SDN:EDMO::3327");
        VESSEL_OPERATORS.put("SDN:C17::68UJ", "SDN:EDMO::3288");
        VESSEL_OPERATORS.put("SDN:C17::48AZ", "SDN:EDMO::2276");
        VESSEL_OPERATORS.put("SDN:C17::487A", "SDN:EDMO::2276");
        VESSEL_OPERATORS.put("SDN:C17::89XL", "SDN:EDMO::217");
        VESSEL_OPERATORS.put("SDN:C17::73AA", "SDN:EDMO::850");
        VESSEL_OPERATORS.put("SDN:C17::29RM", "SDN:EDMO::353");
        VESSEL_OPERATORS.put("SDN:C17::29AJ", "SDN:EDMO::353");
        VESSEL_OPERATORS.put("SDN:C17::GLSW", "SDN:EDMO::2807");
        VESSEL_OPERATORS.put("SDN:C17::46AS", "SDN:EDMO::4766");
        VESSEL_OPERATORS.put("SDN:C17::26D4", "SDN:EDMO::2195");
        VESSEL_OPERATORS.put("SDN:C17::35HT", "SDN:EDMO::486");
        VESSEL_OPERATORS.put("SDN:C17::35EU", "SDN:EDMO::486");
        VESSEL_OPERATORS.put("SDN:C17::58G2", "SDN:EDMO::612");
        VESSEL_OPERATORS.put("SDN:C17::29AH", "SDN:EDMO::2489");
        VESSEL_OPERATORS.put("SDN:C17::64PE", "SDN:EDMO::630");
        VESSEL_OPERATORS.put("SDN:C17::06A4", "SDN:EDMO::4514");
        VESSEL_OPERATORS.put("SDN:C17::06A6", "SDN:EDMO::2947");
        VESSEL_OPERATORS.put("SDN:C17::61TG", "SDN:EDMO::3191");
        VESSEL_OPERATORS.put("SDN:C17::29SO", "SDN:EDMO::3410");
        VESSEL_OPERATORS.put("SDN:C17::18OL", "SDN:EDMO::3361");
        VESSEL_OPERATORS.put("SDN:C17::33H4", "SDN:EDMO::3706");
    }

    @RequestMapping(method = {RequestMethod.GET}, path = "ships")
    @Async("asyncExecutor")
    public CompletableFuture<String> syncShips() {
        log.log(Level.INFO, "Syncing ships");
        try {
            ExternalHelper sujParPlatformClassLoader = new ExternalHelper(LinkedDataTerm.class, null);

            Map<String, LinkedDataTerm> sujParPlatforms = sujParPlatformClassLoader.retrieve();
            List<LinkedDataTerm> ldts = new ArrayList<>();
            for (Map.Entry<String, LinkedDataTerm> entry : sujParPlatforms.entrySet()) {
                LinkedDataTerm term = entry.getValue();
                ldts.add(term);
            }

            ExternalHelper shipLoader = new ExternalHelper(Platform.class, new PlatformCopyAssistant(linkedDataTermService));

            Map<String, Platform> platforms = shipLoader.retrieve();
            for (Map.Entry<String, Platform> entry : platforms.entrySet()) {
                Platform platform = entry.getValue();
                String organisationIdentifier = VESSEL_OPERATORS.get(platform.getTerm().getUrn());
                if (organisationIdentifier != null) {
                    Organisation vesselOperator = organisationService.findByIdentifier(organisationIdentifier);
                    platform.setVesselOperator(vesselOperator);
                    if (vesselOperator != null) {
                        System.out.println("set vessel operator " + vesselOperator.getTerm().getIdentifier());
                    }
                }

                ldts.add((LinkedDataTerm) platform.getTerm());
            }
            linkedDataTermService.saveAll(ldts);
            platformService.saveAll(platforms.values());
        } catch (Exception e) {
            log.log(Level.SEVERE, "exception!", e);
            throw e;
        }
        return CompletableFuture.completedFuture("Finished syncing ships");
    }

    @RequestMapping(method = {RequestMethod.GET}, path = "organisations")
    @Async("asyncExecutor")
    public CompletableFuture<String> syncOrganisations() throws Exception {
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
        return CompletableFuture.completedFuture("Finished syncing organisations");
    }

    @RequestMapping(method = {RequestMethod.GET}, path = "harbours")
    @Async("asyncExecutor")
    public CompletableFuture<String> syncHarbours() {
        log.log(Level.INFO, "Syncing harbours");
        try {
            ExternalHelper loader = new ExternalHelper(Harbour.class, new HarbourCopyAssistant(countryService));
            Map<String, Harbour> harbours = loader.retrieve();
            List<LinkedDataTerm> ldts = new ArrayList<>();
            for (Map.Entry<String, Harbour> entry : harbours.entrySet()) {
                Harbour harbour = entry.getValue();
                ldts.add((LinkedDataTerm) harbour.getTerm());
                Country country = null;
                /*if (harbour._getCountry() != null) {
                    country = countryService.findByIdentifier(country.getTerm().getIdentifier()); //replace the country with a managed entity
                    harbour._setCountry(country);
                }*/
            }
            linkedDataTermService.saveAll(ldts);
            harbourService.saveAll(harbours.values());
        } catch (Exception e) {
            log.log(Level.SEVERE, "exception!", e);
            throw e;
        }
        return CompletableFuture.completedFuture("Finished syncing harbours");
    }

    @RequestMapping(method = {RequestMethod.GET}, path = "seas")
    @Async("asyncExecutor")
    public CompletableFuture<String> syncSeas() {
        sync(SeaArea.class, new ExternalHelper(SeaArea.class, new SeaAreaCopyAssistant()));
        return CompletableFuture.completedFuture("Finished syncing sea areas");
    }

    @RequestMapping(method = {RequestMethod.GET}, path = "countries")
    @Async("asyncExecutor")
    public CompletableFuture<String> syncCountries() {
        sync(Country.class, new ExternalHelper(Country.class, null));
        return CompletableFuture.completedFuture("Finished syncing countries");
    }

    @RequestMapping(method = {RequestMethod.GET}, path = "tools")
    @Async("asyncExecutor")
    public CompletableFuture<String> syncTools() {
        sync(Tool.class, new ExternalHelper(Tool.class, null));
        return CompletableFuture.completedFuture("Finished syncing tools");
    }

    @RequestMapping(method = {RequestMethod.GET}, path = "projects")
    @Async("asyncExecutor")
    public CompletableFuture<String> syncProjects() {
        sync(Project.class, new ExternalProjectHelper());
        return CompletableFuture.completedFuture("Finished syncing projects");
    }

    @RequestMapping(method = {RequestMethod.GET}, path = "all")
    @Async("asyncExecutor")
    public CompletableFuture<String> sync() throws Exception {
        synchronizeExternal();
        return CompletableFuture.completedFuture("Finished syncing all external vocabularies");
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

    @Scheduled(cron = "0 15 15 * * MON-FRI")
    @Transactional() //used to be Propagation.REQUIRED
    public void synchronizeExternal() throws Exception {
        syncShips();
        syncSeas();
        syncCountries();
        syncHarbours();
        syncTools();
        syncOrganisations();
        syncProjects();
        syncSubjects();
    }

    private void syncSubjects() {
    }
}
