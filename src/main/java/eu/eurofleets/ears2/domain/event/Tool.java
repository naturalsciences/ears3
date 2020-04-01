 package eu.eurofleets.ears2.domain.event;
 
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlTransient;
 
 
 
 
 
 
 
 
 @Entity
 @Table(name="TOOL")
 @XmlAccessorType(XmlAccessType.FIELD)
 public class Tool
 {
   @Id
   @GeneratedValue
   @Column(name="ID")
   @XmlTransient
   private Long id;
   @Column(length=45)
   private String name;
   
   public String toString()
   {
     return "Tool [id=" + this.id + ", name=" + this.name + "]";
   }
   
   public Long getId() {
     return this.id;
   }
   
   public void setId(Long id) {
     this.id = id;
   }
   
   public String getName() {
     return this.name;
   }
   
   public void setName(String name) {
     this.name = name;
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/domain/event/Tool.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */