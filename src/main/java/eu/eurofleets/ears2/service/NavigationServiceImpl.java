 package eu.eurofleets.ears2.service;
 
 import eu.eurofleets.ears2.domain.navigation.Navigation;
 import java.util.Date;
 import java.util.List;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Component;
 import org.springframework.transaction.annotation.Transactional;
 
 @Component("navigationService")
 @Transactional
 public class NavigationServiceImpl
   implements NavigationService
 {
   private final NavigationRepository navigationRepository;
   
   @Autowired
   public NavigationServiceImpl(NavigationRepository navigationRepository)
   {
     this.navigationRepository = navigationRepository;
   }
   
   public Navigation getPosition(Date date, String cruiseId)
   {
     return this.navigationRepository.findByTime(date);
   }
   
   public List<Navigation> getPositions(Date startDate, Date endDate)
   {
     return this.navigationRepository.findByTimeBetween(startDate, endDate);
   }
   
   public void getBoundayBox(Date startDate, Date endDate) {}
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/NavigationServiceImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */