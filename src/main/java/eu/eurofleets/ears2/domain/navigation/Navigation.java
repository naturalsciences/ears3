 package eu.eurofleets.ears2.domain.navigation;
 
 import java.util.Date;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlAttribute;
 import javax.xml.bind.annotation.XmlRootElement;
 import javax.xml.bind.annotation.XmlTransient;
 
 
 
 
 
 
 
 
 @Entity
 @Table(name="Navigation")
 @XmlRootElement
 @XmlAccessorType(XmlAccessType.FIELD)
 public class Navigation
 {
   @Id
   @GeneratedValue
   @Column(name="ID")
   @XmlTransient
   private Long id;
   @Column(unique=true, nullable=false, length=15)
   @XmlAttribute
   private String navigationId;
   private Date time;
   private double lon;
   private double lat;
   private double depth;
   private double cog;
   private double sog;
   
   public Long getId()
   {
     return this.id;
   }
   
   public void setId(Long id) {
     this.id = id;
   }
   
   public String getNavigationId() {
     return this.navigationId;
   }
   
   public void setNavigationId(String navigationId) {
     this.navigationId = navigationId;
   }
   
   public Date getTime() {
     return this.time;
   }
   
   public void setTime(Date time) {
     this.time = time;
   }
   
   public double getLon() {
     return this.lon;
   }
   
   public void setLon(double lon) {
     this.lon = lon;
   }
   
   public double getLat() {
     return this.lat;
   }
   
   public void setLat(double lat) {
     this.lat = lat;
   }
   
   public double getDepth() {
     return this.depth;
   }
   
   public void setDepth(double depth) {
     this.depth = depth;
   }
   
   public double getCog() {
     return this.cog;
   }
   
   public void setCog(double cog) {
     this.cog = cog;
   }
   
   public double getSog() {
     return this.sog;
   }
   
   public void setSog(double sog) {
     this.sog = sog;
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/domain/navigation/Navigation.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */