package eu.eurofleets.ears2.service;

import eu.eurofleets.ears2.domain.program.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface ProjectRepository
  extends JpaRepository<Project, Long>
{
  public abstract Project findById(Long paramLong);
  
  public abstract Project findByProjectId(String paramString);
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/ProjectRepository.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */