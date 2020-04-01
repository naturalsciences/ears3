package eu.eurofleets.ears2.service;

import eu.eurofleets.ears2.domain.event.Tool;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface ToolRepository
  extends JpaRepository<Tool, Long>
{
  public abstract Tool findByName(String paramString);
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/ToolRepository.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */