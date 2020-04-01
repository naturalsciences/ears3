package eu.eurofleets.ears2.service;

import eu.eurofleets.ears2.domain.navigation.Navigation;
import java.util.Date;
import java.util.List;

public abstract interface NavigationService
{
  public abstract Navigation getPosition(Date paramDate, String paramString);
  
  public abstract List<Navigation> getPositions(Date paramDate1, Date paramDate2);
  
  public abstract void getBoundayBox(Date paramDate1, Date paramDate2);
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/NavigationService.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */