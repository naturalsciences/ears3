package eu.eurofleets.ears2.service;

import eu.eurofleets.ears2.domain.navigation.Navigation;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract interface NavigationRepository
  extends JpaRepository<Navigation, Long>
{
  public abstract Navigation findByTime(Date paramDate);
  
  public abstract List<Navigation> findByTimeBetween(Date paramDate1, Date paramDate2);
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/NavigationRepository.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */