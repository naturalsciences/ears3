package eu.eurofleets.ears2.service;

import eu.eurofleets.ears2.domain.cruise.SeaArea;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface SeaAreaRepository
  extends JpaRepository<SeaArea, Long>
{
  public abstract SeaArea findById(Long paramLong);
  
  public abstract SeaArea findBySeaAreaId(String paramString);
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/SeaAreaRepository.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */