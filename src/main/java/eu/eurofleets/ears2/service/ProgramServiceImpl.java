 package eu.eurofleets.ears2.service;
 
 import eu.eurofleets.ears2.Exceptions.DuplicateIdException;
 import eu.eurofleets.ears2.domain.program.Program;
 import eu.eurofleets.ears2.domain.program.Project;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import org.hibernate.exception.ConstraintViolationException;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.dao.DataIntegrityViolationException;
 import org.springframework.stereotype.Component;
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.util.Assert;
 
 
 
 
 @Component("programService")
 @Transactional
 public class ProgramServiceImpl
   implements ProgramService
 {
   private final ProgramRepository programRepository;
   private final ProjectRepository projectRepository;
   
   @Autowired
   public ProgramServiceImpl(ProgramRepository programRepository, ProjectRepository projectRepository)
   {
     this.programRepository = programRepository;
     this.projectRepository = projectRepository;
   }
   
   public List<Program> getProgramList()
   {
     return this.programRepository.findAll();
   }
   
 
   public Program getProgramByID(String programId)
   {
     Assert.notNull(programId, "Program ID must not be null");
     return this.programRepository.findByProgramId(programId);
   }
   
 
 
 
 
 
 
 
 
   public List<Program> getProgramListByCruiseId(String cruiseId)
   {
     Assert.notNull(cruiseId, "platform Code must not be null");
     return this.programRepository.getProgramListByCruiseId(cruiseId);
   }
   
 
   public void setProgram(Program program)
   {
     this.programRepository.save(program);
   }
   
   public void setProgram(String programId, Program program)
     throws DuplicateIdException
   {
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
     try
     {
       this.programRepository.save(newProgram);
     } catch (DataIntegrityViolationException e) {
       throw new DuplicateIdException("This register already exists in DataBase. Try another ;-)");
     }
     catch (ConstraintViolationException e) {
       throw new DuplicateIdException("This register already exists in DataBase. Try another ;-)");
     }
     catch (Exception e) {
       e.printStackTrace();
     }
   }
   
 
   public void removeProgram(String programId)
   {
     this.programRepository.deleteProgramByProgramId(programId);
   }
   
 
 
 
 
 
 
   public Project getProject(String projectId)
   {
     return this.projectRepository.findByProjectId(projectId);
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/ProgramServiceImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */