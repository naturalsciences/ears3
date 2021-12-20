package eu.eurofleets.ears3.service;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Harbour;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.SeaArea;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class CruiseService {

    private final CruiseRepository cruiseRepository;
    private final HarbourRepository harbourRepository;
    private final SeaAreaRepository seaAreaRepository;
    private final OrganisationRepository organisationRepository;
    private final PlatformRepository platformRepository;

    @Autowired
    public ProgramService programService;
    @Autowired
    public PersonService personService;
    @Autowired
    public LinkedDataTermService linkedDataTermService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Environment env;

    public static Logger log = Logger.getLogger(CruiseService.class.getSimpleName());

    @Autowired
    public CruiseService(CruiseRepository cruiseRepository, SeaAreaRepository seaAreaRepository, HarbourRepository harbourRepository, OrganisationRepository organisationRepository, PlatformRepository platformRepository) {
        this.cruiseRepository = cruiseRepository;
        this.harbourRepository = harbourRepository;
        this.seaAreaRepository = seaAreaRepository;
        this.organisationRepository = organisationRepository;
        this.platformRepository = platformRepository;
    }

    public List<Cruise> findAll() {
        return this.cruiseRepository.findAll();
    }

    public Cruise findById(long id) {
        Assert.notNull(id, "Cruise Id must not be null");
        return this.cruiseRepository.findById(id).orElse(null);
    }

    public Cruise findByIdentifier(String identifier) {
        Assert.notNull(identifier, "Cruise identifier must not be null");
        return this.cruiseRepository.findByIdentifier(ILinkedDataTerm.cleanUrl(identifier));
    }

    public Set<Cruise> findAllByPlatformIdentifier(String platformIdentifier) {
        Assert.notNull(platformIdentifier, "Platform code must not be null");
        return this.cruiseRepository.findByPlatformCode(platformIdentifier);
    }

    public Set<Cruise> findAllBetweenDate(OffsetDateTime startDate, OffsetDateTime endDate, String platformIdentifier) {
        if (startDate == null) {
            throw new IllegalArgumentException("startDate cannot be null");
        }
        if (platformIdentifier == null) {
            return this.cruiseRepository.findBetweenDate(startDate, endDate);
        } else {
            return this.cruiseRepository.findBetweenDate(startDate, endDate, platformIdentifier);
        }
    }

    public Set<Cruise> findAtDate(OffsetDateTime at, String platformIdentifier) {
        if (platformIdentifier == null) {
            return this.cruiseRepository.findAtDate(at);
        } else {
            return this.cruiseRepository.findAtDate(at, platformIdentifier);
        }
    }

    public Set<Cruise> findCurrent() {
        return this.cruiseRepository.findAtDate(Instant.now().atOffset(ZoneOffset.UTC));
    }

    public Cruise save(CruiseDTO dto) {
        if (env.getProperty("ears.read-only") == null || !env.getProperty("ears.read-only").equals("false")) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        String json;
        try {
            json = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(CruiseService.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (dto.identifier == null) {
            log.log(Level.SEVERE, "Can't further process cruise " + dto.identifier + ". identifier is missing.");
            throw new IllegalArgumentException("Cruise must have an identifier.");
        }
        if (dto.name == null) {
            log.log(Level.SEVERE, "Can't further process cruise " + dto.identifier + ". name is missing.");
            throw new IllegalArgumentException("Cruise must have a name.");
        }
        if (dto.startDate == null) {
            log.log(Level.SEVERE, "Can't further process cruise " + dto.identifier + ". startDate is missing.");
            throw new IllegalArgumentException("Cruise must have a start date.");
        }
        if (dto.endDate == null) {
            log.log(Level.SEVERE, "Can't further process cruise " + dto.identifier + ". endDate is missing.");
            throw new IllegalArgumentException("Cruise must have an end date.");
        }
        if (dto.endDate.isBefore(dto.startDate)) {
            log.log(Level.SEVERE, "Can't further process cruise " + dto.identifier + ". endDate is before startDate.");
            throw new IllegalArgumentException("Cruise's endDate is before startDate.");
        }
        if (dto.objectives == null) {
            log.log(Level.SEVERE, "Can't further process cruise " + dto.identifier + ". objectives is missing.");
            throw new IllegalArgumentException("Cruise must have an objective.");
        }
        if (dto.departureHarbour == null) {
            log.log(Level.SEVERE, "Can't further process cruise " + dto.identifier + ". departureHarbour is missing.");
            throw new IllegalArgumentException("Cruise must have a departure harbour.");
        }
        if (dto.arrivalHarbour == null) {
            log.log(Level.SEVERE, "Can't further process cruise " + dto.identifier + ". arrivalHarbour is missing.");
            throw new IllegalArgumentException("Cruise must have a arrival harbour.");
        }
        if (dto.platform == null) {
            log.log(Level.SEVERE, "Can't further process cruise " + dto.identifier + ". platform is missing.");
            throw new IllegalArgumentException("Cruise must have a platform.");
        }
        if (dto.chiefScientists == null || dto.chiefScientists.isEmpty()) {
            log.log(Level.SEVERE, "Can't further process cruise " + dto.identifier + ". chiefScientists is missing.");
            throw new IllegalArgumentException("Cruise must have at least one Chief Scientist.");
        }
        try {
            Cruise cruise = new Cruise();
            cruise.setIdentifier(dto.identifier);
            cruise.setName(dto.name);
            cruise.setStartDate(dto.startDate);
            cruise.setEndDate(dto.endDate);
            cruise.setObjectives(dto.objectives);
            cruise.setPurpose(dto.purpose);
            cruise.setDataUrl(dto.dataUrl);
            cruise.setFinalReportUrl(dto.finalReportUrl);
            cruise.setPlanningUrl(dto.planningUrl);
            cruise.setTrackImageUrl(dto.trackImageUrl);
            cruise.setTrackGmlUrl(dto.trackGmlUrl);
            Cruise foundCruise = cruiseRepository.findByIdentifier(dto.identifier);
            if (foundCruise != null) {
                cruise.setId(foundCruise.getId());
            }
            Harbour arrivalHarbour = harbourRepository.findByIdentifier(ILinkedDataTerm.cleanUrl(dto.arrivalHarbour));
            Harbour departureHarbour = harbourRepository.findByIdentifier(ILinkedDataTerm.cleanUrl(dto.departureHarbour));
            Collection<Person> chiefScientists = new ArrayList<>();
            if (dto.chiefScientists != null) {
                for (PersonDTO chiefScientistDTO : dto.chiefScientists) {
                    Person p = new Person(chiefScientistDTO);
                    Organisation organisation = organisationRepository.findByIdentifier(ILinkedDataTerm.cleanUrl(chiefScientistDTO.organisation));
                    p.setOrganisation(organisation);
                    p = personService.findOrCreate(p);
                    chiefScientists.add(p);
                }
            }
            Organisation collateCentre = organisationRepository.findByIdentifier(ILinkedDataTerm.cleanUrl(dto.collateCentre));
            Platform platform = platformRepository.findByIdentifier(ILinkedDataTerm.cleanUrl(dto.platform));

            Collection<Program> programs = new ArrayList<>();
            if (dto.programs != null) {
                for (String program : dto.programs) {
                    if (program != null) {
                        Program p = programService.findByIdentifier(program);
                        programs.add(p);
                        //p.addCruise(cruise);
                        //this.programService.save(p);
                    }
                }
            }
            Collection<SeaArea> seaAreas = new ArrayList<>();
            if (dto.seaAreas != null) {
                for (String sea : dto.seaAreas) {
                    if (sea != null) {
                        SeaArea s = seaAreaRepository.findByIdentifier(ILinkedDataTerm.cleanUrl(sea));
                        seaAreas.add(s);
                    }
                }
            }
            Collection<LinkedDataTerm> PO2s = new ArrayList<>();
            if (dto.P02 != null) {
                for (String po2 : dto.P02) {
                    if (po2 != null) {
                        LinkedDataTerm p = linkedDataTermService.findByIdentifier(po2);
                        PO2s.add(p);
                    }
                }
            }
            cruise.setArrivalHarbour(arrivalHarbour);
            cruise.setDepartureHarbour(departureHarbour);
            cruise.addChiefScientists(chiefScientists);
            cruise.setCollateCentre(collateCentre);
            cruise.setPlatform(platform);
            cruise.addPrograms(programs);
            cruise.addSeaAreas(seaAreas);
            cruise.addP02(PO2s);
            return this.cruiseRepository.save(cruise); //.getId(); } catch (Exception e) {
        } catch (Exception e) {
            log.log(Level.SEVERE, "exception!", e);
            throw e;
        }
    }

    /* public void setCruise(String cruiseId, Cruise cruise) throws DuplicateIdException {
        Cruise newCruise = new Cruise();
        newCruise.setCruiseId(cruiseId);
        newCruise.setCruiseName(cruise.getCruiseName());
        newCruise.setStartDate(cruise.getStartDate());
        newCruise.setEndDate(cruise.getEndDate());
        newCruise.setChiefScientist(cruise.getChiefScientist());
        newCruise.setChiefScientistOrganisation(cruise.getChiefScientistOrganisation());
        newCruise.setPlatformCode(cruise.getPlatformCode());
        newCruise.setPlatformClass(cruise.getPlatformClass());
        newCruise.setStartingHarbor(cruise.getStartingHarbor());
        newCruise.setArrivalHarbor(cruise.getArrivalHarbor());
        newCruise.setObjectives(cruise.getObjectives());
        newCruise.setCollateCenter(cruise.getCollateCenter());

        Set<SeaArea> seaAreasSet = new HashSet();
        for (SeaArea seaArea : cruise.getSeaAreas()) {
            seaAreasSet.add(getSeaArea(seaArea.getSeaAreaId()));
        }
        newCruise.setSeaAreas(seaAreasSet);
        try {
            this.cruiseRepository.save(newCruise);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateIdException("This register already exists in DataBase. Try another ;-)");
        } catch (ConstraintViolationException e) {
            throw new DuplicateIdException("This register already exists in DataBase. Try another ;-)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    public void deleteById(String id) {
        this.cruiseRepository.deleteById(Long.valueOf(id));
    }

    public void deleteByDate(Date startDate, Date endDate) {
        this.cruiseRepository.deleteByDate(startDate, endDate);
    }

    public void deleteByIdentifier(String identifier) {
        this.cruiseRepository.deleteByIdentifier(identifier);
    }

}
