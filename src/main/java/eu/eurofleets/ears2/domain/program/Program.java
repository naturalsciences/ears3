 package eu.eurofleets.ears2.domain.program;
 
 import java.util.Set;
 import javax.persistence.Column;
 import javax.persistence.GeneratedValue;
 import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAttribute;
 import javax.xml.bind.annotation.XmlElement;
 import javax.xml.bind.annotation.XmlElementWrapper;
 import javax.xml.bind.annotation.XmlRootElement;
 
 @javax.persistence.Entity
 @javax.persistence.Table(name="PROGRAM")
 @XmlRootElement
 @javax.xml.bind.annotation.XmlAccessorType(XmlAccessType.FIELD)
 public class Program
 {
   @javax.persistence.Id
   @GeneratedValue
   @Column(name="ID")
   private Long id;
   @Column(unique=true, nullable=false, length=15)
   @XmlAttribute
   private String programId;
   @Column(unique=true, nullable=false, length=15)
   @XmlAttribute
   private String cruiseId;
   @Column(length=150)
   private String originatorCode;
   @Column(length=150)
   private String PIName;
   @Column(length=150)
   private String description;
   
   public Program() {}
   
   public Program(String programId, String cruiseId, String originatorCode, String PIName, String description)
   {
     this.programId = programId;
     this.cruiseId = cruiseId;
     this.originatorCode = originatorCode;
     this.PIName = PIName;
     this.description = description;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   @javax.persistence.OneToMany(cascade={javax.persistence.CascadeType.ALL}, fetch=javax.persistence.FetchType.EAGER)
   @javax.persistence.JoinTable(name="Program_Project", joinColumns={@javax.persistence.JoinColumn(name="Program_ID")}, inverseJoinColumns={@javax.persistence.JoinColumn(name="Project_ID")})
   @XmlElementWrapper(name="projects")
   @XmlElement(name="project")
   private Set<Project> projects = new java.util.HashSet();
   
 
 
 
 
 
 
 
   public Long getId()
   {
     return this.id;
   }
   
   public void setId(Long id) {
     this.id = id;
   }
   
   public String getProgramId() {
     return this.programId;
   }
   
   public void setProgramId(String programId) {
     this.programId = programId;
   }
   
   public String getCruiseId() {
     return this.cruiseId;
   }
   
   public void setCruiseId(String cruiseId) {
     this.cruiseId = cruiseId;
   }
   
   public String getOriginatorCode() {
     return this.originatorCode;
   }
   
   public void setOriginatorcode(String originatorCode) {
     this.originatorCode = originatorCode;
   }
   
   public String getPIName() {
     return this.PIName;
   }
   
   public void setPIName(String PIName) {
     this.PIName = PIName;
   }
   
   public String getDescription() {
     return this.description;
   }
   
   public void setDescription(String description) {
     this.description = description;
   }
   
   public Set<Project> getProjects() {
     return this.projects;
   }
   
   public void setProjects(Set<Project> projects) {
     this.projects = projects;
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/domain/program/Program.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */