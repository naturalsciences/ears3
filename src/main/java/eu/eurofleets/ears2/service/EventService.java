package eu.eurofleets.ears2.service;

import eu.eurofleets.ears2.domain.event.Event;
import eu.eurofleets.ears2.domain.event.Tool;
import java.util.Date;
import java.util.List;

public abstract interface EventService
{
  public abstract void insertEvent(Event paramEvent);
  
  public abstract Event getEvent(Long paramLong);
  
  public abstract List<Event> getEvent();
  
  public abstract Event getEventById(String paramString);
  
  public abstract Event getEventByDate(Date paramDate);
  
  public abstract List<Event> getEventBetweenDates(Date paramDate1, Date paramDate2);
  
  public abstract void removeEvent(Long paramLong);
  
  public abstract void removeEventById(String paramString);
  
  public abstract void removeEventsBetweenDates(Date paramDate1, Date paramDate2);
  
  public abstract void modifyEvent(Long paramLong, Event paramEvent);
  
  public abstract Tool getTool(String paramString);
  
  public abstract void insertTool(Tool paramTool);
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/EventService.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */