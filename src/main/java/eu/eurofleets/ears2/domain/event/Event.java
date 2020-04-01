 package eu.eurofleets.ears2.domain.event;
 
 import java.util.Date;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlAttribute;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlRootElement;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 @Entity
 @Table(name="event")
 @XmlRootElement
 @XmlAccessorType(XmlAccessType.FIELD)
 public class Event
 {
   @XmlAttribute(name="eventId")
   @Column(length=15)
   private String eventId;
   @Id
   @GeneratedValue
   @Column(name="ID")
   private Long id;
   @Column
   private Date timeStamp;
   @Column(length=45)
   private String actor;
   @XmlElement(name="subjectName")
   @Column(length=45)
   private String subject;
   @Column(length=45)
   @XmlElement(name="name")
   private String actionName;
   @Column(length=45)
   @XmlElement(name="property")
   private String actionProperty;
   @Column(length=45)
   private String categoryName;
   @ManyToOne
   @JoinColumn(name="toolID")
   @XmlElement(name="tool")
   private Tool tool;
   
   public Long getId()
   {
     return this.id;
   }
   
   public void setId(Long id) {
     this.id = id;
   }
   
   public String getEventId() {
     return this.eventId;
   }
   
   public void setEventId(String eventId) {
     this.eventId = eventId;
   }
   
   public Date getTimeStamp() {
     return this.timeStamp;
   }
   
   public void setTimeStamp(Date timeStamp) {
     this.timeStamp = timeStamp;
   }
   
   public String getActor() {
     return this.actor;
   }
   
   public void setActor(String actor) {
     this.actor = actor;
   }
   
   public String getSubject() {
     return this.subject;
   }
   
   public void setSubject(String subject) {
     this.subject = subject;
   }
   
   public String getActionName() {
     return this.actionName;
   }
   
   public void setActionName(String actionName) {
     this.actionName = actionName;
   }
   
   public String getActionProperty() {
     return this.actionProperty;
   }
   
   public void setActionProperty(String actionProperty) {
     this.actionProperty = actionProperty;
   }
   
   public String getCategoryName() {
     return this.categoryName;
   }
   
   public void setCategoryName(String categoryName) {
     this.categoryName = categoryName;
   }
   
   public Tool getTool() {
     return this.tool;
   }
   
   public void setTool(Tool tool) {
     this.tool = tool;
   }
   
   public String toString()
   {
     return "Event [id=" + this.id + ", timeStamp=" + this.timeStamp + ", actor=" + this.actor + ", subject=" + this.subject + ", actionName=" + this.actionName + ", actionProperty=" + this.actionProperty + ", categoryName=" + this.categoryName + ", tool=" + this.tool + "]";
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/domain/event/Event.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */