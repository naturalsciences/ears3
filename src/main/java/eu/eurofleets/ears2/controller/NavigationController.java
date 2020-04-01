 package eu.eurofleets.ears2.controller;
 
 import eu.eurofleets.ears2.domain.navigation.Navigation;
 import eu.eurofleets.ears2.service.NavigationService;
 import eu.eurofleets.ears2.utilities.DateUtilities;
 import java.text.ParseException;
 import java.util.List;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 @RestController
 public class NavigationController
 {
   public static final String DEFAULT_VALUE = "_";
   @Autowired
   private NavigationService navigationService;
   
   @RequestMapping(value={"getPosition/byDate/xml"}, produces={"application/xml"})
   public Navigation getPositionByDate(@RequestParam(required=true, value="date") String date)
     throws ParseException
   {
     return this.navigationService.getPosition(DateUtilities.parseDate(date), null);
   }
   
   @RequestMapping(value={"getPosition/byRange/xml"}, produces={"application/xml"})
   public List<Navigation> getPositionByRange(@RequestParam(required=true, value="startDate") String startDate, @RequestParam(required=true, value="endDate") String endDate)
     throws ParseException
   {
     return this.navigationService.getPositions(DateUtilities.parseDate(startDate), DateUtilities.parseDate(endDate));
   }
   
 
 
 
   @RequestMapping(value={"getBoundaryBox/byRange/xml"}, produces={"application/xml"})
   public List<Navigation> getBoundaryBoxByRange(@RequestParam(required=true, value="startDate") String startDate, @RequestParam(required=true, value="endDate") String endDate)
     throws ParseException
   {
     return null;
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/controller/NavigationController.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */