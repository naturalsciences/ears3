package eu.eurofleets.ears2.service;

import eu.eurofleets.ears2.domain.program.Program;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource(collectionResourceRel="program", path="program")
public abstract interface ProgramRepository
  extends JpaRepository<Program, Long>
{
  public abstract Program findByProgramId(String paramString);
  
  public abstract List<Program> findByCruiseId(String paramString);
  
  public abstract List<Program> getProgramListByCruiseId(String paramString);
  
  @Modifying
  @Transactional
  @Query("delete from Program p where p.programId = ?1")
  public abstract void deleteProgramByProgramId(String paramString);
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/ProgramRepository.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */