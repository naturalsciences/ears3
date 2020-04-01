 package eu.eurofleets.ears2.domain.cruise;
 
 import java.util.List;
 import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlRootElement;
 
 
 
 
 @XmlRootElement(name="cruises")
 @XmlAccessorType(XmlAccessType.FIELD)
 public class CruiseList
 {
   public CruiseList() {}
   
   public CruiseList(List<Cruise> cruises) { this.cruises = cruises; }
   
   @XmlElement(name="cruise")
   private List<Cruise> cruises = null;
   
   public List<Cruise> getCruises()
   {
     return this.cruises;
   }
   
   public void setCruises(List<Cruise> cruises) {
     this.cruises = cruises;
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/domain/cruise/CruiseList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */