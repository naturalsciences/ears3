package eu.eurofleets.ears2.domain.cruise;

import be.naturalsciences.bmdc.cruise.model.ICountry;
import be.naturalsciences.bmdc.cruise.model.ICruise;
import be.naturalsciences.bmdc.cruise.model.IEvent;
import be.naturalsciences.bmdc.cruise.model.IHarbour;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IOrganisation;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IPlatform;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.ISeaArea;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;

@javax.persistence.Entity
@javax.persistence.Table(name = "CRUISE")
@javax.xml.bind.annotation.XmlRootElement
@javax.xml.bind.annotation.XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
public class Cruise {

    @javax.persistence.Id
    @javax.persistence.GeneratedValue
    @javax.persistence.Column(name = "ID")
    private Long id;
    @javax.xml.bind.annotation.XmlAttribute(name = "id")
    @javax.persistence.Column(unique = true, nullable = false, length = 20)
    private String cruiseId;
    @javax.persistence.Column(nullable = false, length = 15)
    private String cruiseName;
    private Date startDate;
    private Date endDate;
    @javax.persistence.Column(length = 150)
    private String chiefScientist;
    @javax.persistence.Column(length = 150)
    private String chiefScientistOrganisation;
    @javax.persistence.Column(length = 150)
    private String platformCode;
    @javax.persistence.Column(length = 150)
    private String platformClass;
    @javax.persistence.Column(length = 150)
    private String objectives;
    @javax.persistence.Column(length = 150)
    private String collateCenter;
    @javax.persistence.Column(length = 45)
    private String startingHarbor;
    @javax.persistence.Column(length = 45)
    private String arrivalHarbor;

    public Cruise() {
    }

    public Cruise(String cruiseId, String cruiseName, Date startDate, Date endDate, String chiefScientist, String chiefScientistOrganisation, String platformCode, String platformClass, String objectives, String collateCenter) {
        this.cruiseId = cruiseId;
        this.cruiseName = cruiseName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.chiefScientist = chiefScientist;
        this.chiefScientistOrganisation = chiefScientistOrganisation;
        this.platformCode = platformCode;
        this.platformClass = platformClass;
        this.objectives = objectives;
        this.collateCenter = collateCenter;
    }

    @javax.persistence.OneToMany(cascade = {javax.persistence.CascadeType.ALL}, fetch = javax.persistence.FetchType.EAGER)
    @javax.persistence.JoinTable(name = "Cruise_SeaArea", joinColumns = {
        @javax.persistence.JoinColumn(name = "Cruise_ID")}, inverseJoinColumns = {
        @javax.persistence.JoinColumn(name = "SeaArea_ID")})
    @javax.xml.bind.annotation.XmlElementWrapper(name = "seaAreas")
    @javax.xml.bind.annotation.XmlElement(name = "seaArea")
    private java.util.Set<SeaArea> seaAreas = new java.util.HashSet();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCruiseId() {
        return this.cruiseId;
    }

    public void setCruiseId(String cruiseId) {
        this.cruiseId = cruiseId;
    }

    public String getCruiseName() {
        return this.cruiseName;
    }

    public void setCruiseName(String cruiseName) {
        this.cruiseName = cruiseName;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getChiefScientist() {
        return this.chiefScientist;
    }

    public void setChiefScientist(String chiefScientist) {
        this.chiefScientist = chiefScientist;
    }

    public String getChiefScientistOrganisation() {
        return this.chiefScientistOrganisation;
    }

    public void setChiefScientistOrganisation(String chiefScientistOrganisation) {
        this.chiefScientistOrganisation = chiefScientistOrganisation;
    }

    public String getPlatformCode() {
        return this.platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getPlatformClass() {
        return this.platformClass;
    }

    public void setPlatformClass(String platformClass) {
        this.platformClass = platformClass;
    }

    public String getObjectives() {
        return this.objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getCollateCenter() {
        return this.collateCenter;
    }

    public void setCollateCenter(String collateCenter) {
        this.collateCenter = collateCenter;
    }

    public String getStartingHarbor() {
        return this.startingHarbor;
    }

    public void setStartingHarbor(String startingHarbor) {
        this.startingHarbor = startingHarbor;
    }

    public String getArrivalHarbor() {
        return this.arrivalHarbor;
    }

    public void setArrivalHarbor(String arrivalHarbor) {
        this.arrivalHarbor = arrivalHarbor;
    }

    public java.util.Set<SeaArea> getSeaAreas() {
        return this.seaAreas;
    }

    public void setSeaAreas(java.util.Set<SeaArea> seaAreas) {
        this.seaAreas = seaAreas;
    }

    public String toString() {
        return "Cruise [id=" + this.id + ", cruiseId=" + this.cruiseId + ", cruiseName=" + this.cruiseName + ", startDate=" + this.startDate + ", endDate=" + this.endDate + ", chiefScientist=" + this.chiefScientist + ", chiefScientistOrganisation=" + this.chiefScientistOrganisation + ", platformCode=" + this.platformCode + ", platformClass=" + this.platformClass + ", objectives=" + this.objectives + ", collateCenter=" + this.collateCenter + ", seaAreas=" + this.seaAreas + "]";
    }
    
}
