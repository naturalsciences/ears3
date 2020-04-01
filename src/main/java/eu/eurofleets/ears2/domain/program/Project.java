 package eu.eurofleets.ears2.domain.program;
 
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Table;
 import javax.xml.bind.annotation.XmlAccessType;
 import javax.xml.bind.annotation.XmlAccessorType;
 import javax.xml.bind.annotation.XmlAttribute;
 import javax.xml.bind.annotation.XmlTransient;
 
 
 
 
 
 @Entity
 @Table(name="PROJECT")
 @XmlAccessorType(XmlAccessType.FIELD)
 public class Project
 {
   @Id
   @GeneratedValue
   @Column(name="ID")
   @XmlTransient
   private Long id;
   @Column(length=45)
   @XmlAttribute(name="id")
   private String projectId;
   @Column(length=45)
   private String projectName;
   
   public Project() {}
   
   public Project(String projectId, String projectName)
   {
     this.projectId = projectId;
     this.projectName = projectName;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
   public Long getId()
   {
     return this.id;
   }
   
   public void setId(Long id) {
     this.id = id;
   }
   
   public String getProjectId() {
     return this.projectId;
   }
   
   public void setProjectId(String projectId) {
     this.projectId = projectId;
   }
   
   public String getProjectName() {
     return this.projectName;
   }
   
   public void setProjectName(String projectName) {
     this.projectName = projectName;
   }
   
 
   public String toString()
   {
     return "Project [id=" + this.id + ", projectId=" + this.projectId + ", projectName=" + this.projectName + "]";
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/domain/program/Project.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */