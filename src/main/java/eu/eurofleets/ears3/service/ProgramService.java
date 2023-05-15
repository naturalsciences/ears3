package eu.eurofleets.ears3.service;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.Project;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ProgramService {

    public static Logger log = Logger.getLogger(CruiseService.class.getSimpleName());

    private final ProgramRepository programRepository;
    private final ProjectRepository projectRepository;
    private final OrganisationRepository organisationRepository;

    @Autowired
    public PersonService personService;

    @Autowired
    public ProjectService projectService;
    @Autowired
    private Environment env;

    @Autowired
    public ProgramService(ProgramRepository programRepository, ProjectRepository projectRepository,
            OrganisationRepository organisationRepository) {
        this.programRepository = programRepository;
        this.projectRepository = projectRepository;
        this.organisationRepository = organisationRepository;
    }

    public Set<Program> findAll() {
        return this.programRepository.findAll();
    }

    public Program findById(long id) {
        Assert.notNull(id, "Program ID must not be null");
        return this.programRepository.findById(id).orElse(null);
    }

    public Program findByIdentifier(String identifier) {
        Assert.notNull(identifier, "Program identifier must not be null");
        return this.programRepository.findByIdentifier(identifier);
    }

    public Program save(Program program) {
        if (env.getProperty("ears.read-only") == null || !env.getProperty("ears.read-only").equals("false")) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        return this.programRepository.save(program);
    }

    public Program save(ProgramDTO dto) {
        if (env.getProperty("ears.read-only") == null || !env.getProperty("ears.read-only").equals("false")) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        try {
            Program program = new Program();
            program.setDescription(dto.description);
            program.setIdentifier(dto.identifier);
            program.setName(dto.name);
            program.setSampling(dto.sampling);

            Program foundProgram = programRepository.findByIdentifier(dto.identifier);
            if (foundProgram != null) {
                program.setId(foundProgram.getId());
            }

            Collection<Person> principalInvestigators = new ArrayList<>();
            if (dto.principalInvestigators != null) {
                for (PersonDTO principalInvestigatorDTO : dto.principalInvestigators) {
                    Person p = new Person(principalInvestigatorDTO);
                    Organisation organisation = organisationRepository
                            .findByIdentifier(ILinkedDataTerm.cleanUrl(principalInvestigatorDTO.getOrganisation()));
                    p.setOrganisation(organisation);
                    p = personService.findOrCreate(p);
                    principalInvestigators.add(p);
                }
                program.setPrincipalInvestigators(principalInvestigators);
            }

            Collection<Project> projects = new ArrayList<>();
            if (dto.projects != null) {
                for (String project : dto.projects) {
                    Project p = projectService.findByIdentifier(project);
                    projects.add(p);
                }
            }
            program.setProjects(projects);
            return save(program);
        } catch (Exception e) {
            log.log(Level.SEVERE, "exception!", e);
            throw e;
        }
    }

    public void delete(Program program) {
        if (env.getProperty("ears.read-only") == null || !env.getProperty("ears.read-only").equals("false")) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        this.programRepository.delete(program);
    }

    public void deleteById(long id) {
        if (env.getProperty("ears.read-only") == null || !env.getProperty("ears.read-only").equals("false")) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        this.programRepository.deleteById(id);
    }

    public void deleteByIdentifier(String identifier) {
        if (env.getProperty("ears.read-only") == null || !env.getProperty("ears.read-only").equals("false")) {
            throw new IllegalArgumentException("Cannot create/modify entities on a read-only system.");
        }
        this.programRepository.deleteByIdentifier(identifier);
    }

    public Set<Program> findByCruiseIdentifier(String cruiseIdentifier) {
        return this.programRepository.findByCruiseIdentifier(cruiseIdentifier);
    }

    public Set<Program> findByVesselIdentifier(String vesselIdentifier) {
        return this.programRepository.findByVesselIdentifier(vesselIdentifier);
    }

    public Set<Program> findCurrent() {
        return this.programRepository.findAtDate(Instant.now().atOffset(ZoneOffset.UTC));
    }

    public Set<Program> findByAtDate(OffsetDateTime at) {
        return this.programRepository.findAtDate(at);
    }

}
