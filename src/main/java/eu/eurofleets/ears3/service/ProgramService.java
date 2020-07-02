package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.Project;
import java.util.List;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public ProgramService(ProgramRepository programRepository, ProjectRepository projectRepository) {
        this.programRepository = programRepository;
        this.projectRepository = projectRepository;
    }

    public List<Program> findAll() {
        return IterableUtils.toList(this.programRepository.findAll());
    }

    public Program findById(long id) {
        Assert.notNull(id, "Program ID must not be null");
        return this.programRepository.findById(id).orElse(null);
    }

    public Program findByIdentifier(String identifier) {
        Assert.notNull(identifier, "Identifier must not be null");
        return this.programRepository.findByIdentifier(identifier);
    }

    public List<Program> findByCruiseId(long cruiseId) {
        Assert.notNull(cruiseId, "platform Code must not be null");
        return this.programRepository.findByCruiseId(cruiseId);
    }

    public void save(Program program) {
        this.programRepository.save(program);
    }

    /*
    public void setProgram(String programId, Program program)
            throws DuplicateIdException {
        Program newProgram = new Program();
        newProgram.setProgramId(program.getProgramId());
        newProgram.setCruiseId(program.getCruiseId());
        newProgram.setOriginatorcode(program.getOriginatorCode());
        newProgram.setPIName(program.getPIName());
        newProgram.setDescription(program.getDescription());

        Set<Project> projectsSet = new HashSet();

        for (Project project : program.getProjects()) {
            projectsSet.add(getProject(project.getProjectId()));
        }
        newProgram.setProjects(projectsSet);
        try {
            this.programRepository.save(newProgram);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateIdException("This register already exists in DataBase. Try another ;-)");
        } catch (ConstraintViolationException e) {
            throw new DuplicateIdException("This register already exists in DataBase. Try another ;-)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    public void removeProgram(String programId) {
        this.programRepository.deleteProgramByProgramId(programId);
    }

    public Project getProject(long projectId) {
        return this.projectRepository.findById(projectId).orElse(null);
    }
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/ProgramServiceImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
