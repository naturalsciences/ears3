package eu.eurofleets.ears2.service;

import eu.eurofleets.ears2.domain.event.Event;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public abstract interface EventRepository
  extends JpaRepository<Event, Long>
{
  public abstract Event findByEventId(String paramString);
  
  public abstract Event findFirstByTimeStampBeforeOrderByTimeStampDesc(Date paramDate);
  
  public abstract List<Event> findByTimeStampBetween(Date paramDate1, Date paramDate2);
  
  @Modifying
  @Transactional
  @Query("delete from Event e where e.eventId = ?1")
  public abstract void deleteByEventId(String paramString);
  
  @Modifying
  @Transactional
  @Query("delete from Event e where e.timeStamp between ?1 and ?2")
  public abstract void deleteByTimeStampBetween(Date paramDate1, Date paramDate2);
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/EventRepository.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */