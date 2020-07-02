package eu.eurofleets.ears3.service;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
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
import eu.eurofleets.ears3.dto.ProgramDTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ProgramService programService;
    @Autowired
    private PersonService personService;

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
        return IterableUtils.toList(this.cruiseRepository.findAll());
    }

    public List<Cruise> findAllByPlatformCode(String code) {
        Assert.notNull(code, "Platform code must not be null");
        return null;
    }

    public Cruise findById(long id) {
        Assert.notNull(id, "Cruise Id must not be null");
        return this.cruiseRepository.findById(id).orElse(null);
    }

    public Cruise findByIdentifier(String identifier) {
        Assert.notNull(identifier, "Identifier must not be null");
        return this.cruiseRepository.findByIdentifier(ILinkedDataTerm.cleanUrl(identifier));
    }

    public Cruise save(CruiseDTO dto) {
        try {
            Cruise cruise = new Cruise();
            cruise.setIdentifier(dto.identifier);
            cruise.setName(dto.name);
            cruise.setStartDate(dto.startDate);
            cruise.setEndDate(dto.endDate);
            cruise.setObjectives(dto.objectives);

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
                for (Integer i : dto.programs) {
                    Program p = programService.findById(Long.valueOf(i));
                    programs.add(p);
                }
            }
            Collection<SeaArea> seaAreas = new ArrayList<>();
            if (dto.seaAreas != null) {
                for (String sea : dto.seaAreas) {
                    SeaArea s = seaAreaRepository.findByIdentifier(ILinkedDataTerm.cleanUrl(sea));
                    seaAreas.add(s);
                }
            }

            cruise.setArrivalHarbour(arrivalHarbour);
            cruise.setDepartureHarbour(departureHarbour);
            cruise.setChiefScientists(chiefScientists);
            cruise.setCollateCentre(collateCentre);
            cruise.setPlatform(platform);
            cruise.setPrograms(programs);
            cruise.setSeaAreas(seaAreas);
            return this.cruiseRepository.save(cruise);//.getId(); } catch (Exception e) {
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
