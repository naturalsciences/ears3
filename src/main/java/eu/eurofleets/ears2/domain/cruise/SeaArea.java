 package eu.eurofleets.ears2.domain.cruise;
 
 import javax.persistence.Column;
 import javax.persistence.GeneratedValue;
 
 @javax.persistence.Entity
 @javax.persistence.Table(name="SEAAREA")
 @javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
 public class SeaArea
 {
   @javax.persistence.Id
   @GeneratedValue
   @Column(name="ID")
   @javax.xml.bind.annotation.XmlTransient
   private Long id;
   @Column(length=15)
   @javax.xml.bind.annotation.XmlAttribute(name="id")
   private String seaAreaId;
   @Column(length=150)
   private String URN;
   
   public SeaArea() {}
   
   public SeaArea(String seaAreaId, String uRN)
   {
     this.seaAreaId = seaAreaId;
     this.URN = uRN;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   public Long getId()
   {
     return this.id;
   }
   
   public void setId(Long id) {
     this.id = id;
   }
   
   public String getSeaAreaId() {
     return this.seaAreaId;
   }
   
   public void setSeaAreaId(String seaAreaId) {
     this.seaAreaId = seaAreaId;
   }
   
   public String getURN() {
     return this.URN;
   }
   
   public void setURN(String uRN) {
     this.URN = uRN;
   }
   
   public String toString()
   {
     return "SeaArea [id=" + this.id + ", seaAreaId=" + this.seaAreaId + ", URN=" + this.URN + "]";
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/domain/cruise/SeaArea.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */