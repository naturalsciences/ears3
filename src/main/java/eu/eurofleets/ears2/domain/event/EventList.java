 package eu.eurofleets.ears2.domain.event;
 
 import java.util.List;
 import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlRootElement;
 
 
 
 
 @XmlRootElement(name="events")
 @XmlAccessorType(XmlAccessType.FIELD)
 public class EventList
 {
   public EventList() {}
   
   public EventList(List<Event> events) { this.events = events; }
   
   @XmlElement(name="event")
   private List<Event> events = null;
   
   public List<Event> getCruises()
   {
     return this.events;
   }
   
   public void setEvents(List<Event> events) {
     this.events = events;
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/domain/event/EventList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */