package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ICoordinate;
import be.naturalsciences.bmdc.cruise.model.ICruise;
import be.naturalsciences.bmdc.cruise.model.IEvent;
import be.naturalsciences.bmdc.cruise.model.IHarbour;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IOrganisation;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IPlatform;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProject;
import be.naturalsciences.bmdc.cruise.model.ISeaArea;
import be.naturalsciences.bmdc.cruise.model.ITool;
import com.fasterxml.jackson.annotation.JsonFormat;
import dto.Navigation;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Formula;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thomas
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Cruise implements ICruise, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, length = 100)
    private String identifier; //the identifier used by the operator for the cruise, like BE11-2018/08

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime endDate;
    private String finalReportUrl;
    private String planningUrl;
    private String trackImageUrl;
    private String dataUrl;
    private String trackGmlUrl;
    @ManyToOne(optional = false)
    private Organisation collateCentre; //the data centre that will do the data management and dissemination, an identifier in an external vocabulary, e.g. EDMO
    @ManyToOne(optional = false)
    private Harbour departureHarbour; //an identifier in an external vocabulary, e.g. C38 (can be urn or url)
    @ManyToOne(optional = false)
    private Harbour arrivalHarbour; //an identifier in an external vocabulary, e.g. C38 (can be urn or url)
    @ManyToMany()
    @JoinTable(
            name = "cruise_chief_scientists",
            joinColumns = {
                @JoinColumn(name = "cruise_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "chief_scientist_id")}
    )
    private Collection<Person> chiefScientists;
    @ManyToMany()
    public Collection<SeaArea> seaAreas;
    @ManyToMany()
    @JoinTable(
            name = "cruise_programs",
            joinColumns = {
                @JoinColumn(name = "cruise_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "program_id")}
    )
    private Collection<Program> programs;
    @ManyToOne(optional = false)
    private Platform platform;
    private String objectives;
    private boolean isCancelled; //additional field so that cancelled cruises don't need to be deleted from the database. They just don't show up in EARS.
    @ManyToMany()
    @JoinTable(
            name = "cruise_p02",
            joinColumns = {
                @JoinColumn(name = "cruise_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "p02_id")}
    )

    private Collection<LinkedDataTerm> P02;
    private String name; //a full name like 'MAST3' or 'SAMSON-67', not an identifier like a sequential number
    private String purpose;

    @Transient
    @Formula("(select e from event e left join cruise c where e.timeStamp >= c.startDate and e.timeStamp <= c.endDate and e.platform=c.platform)")
    private Collection<Event> events; //the events associated with this cruise. Stored in the database, but not via a PK-FK relation.
    @XmlTransient
    @Transient
    private Collection<Navigation> navigations; //the track of this cruise. Not stored in the database, but retrieved via a query.
    @Transient
    private Collection<Coordinate> track; //the track of this cruise. Not stored in the database, but retrieved via a query.
    @Transient
    private double westBoundLongitude;
    @Transient
    private double eastBoundLongitude;
    @Transient
    private double northBoundLatitude;
    @Transient
    private double southBoundLatitude;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public OffsetDateTime getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    @Override
    public OffsetDateTime getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public IOrganisation getCollateCentre() {
        return collateCentre;
    }

    @Override
    public void setCollateCentre(IOrganisation collateCentre) {
        this.collateCentre = (Organisation) collateCentre;
    }

    @Override
    public IHarbour getDepartureHarbour() {
        return departureHarbour;
    }

    @Override
    public void setDepartureHarbour(IHarbour departureHarbour) {
        this.departureHarbour = (Harbour) departureHarbour;
    }

    @Override
    public IHarbour getArrivalHarbour() {
        return arrivalHarbour;
    }

    @Override
    public void setArrivalHarbour(IHarbour arrivalHarbour) {
        this.arrivalHarbour = (Harbour) arrivalHarbour;
    }

    @Override
    public Collection<? extends IPerson> getChiefScientists() {
        return chiefScientists;
    }

    @Override
    public void setChiefScientists(Collection<? extends IPerson> chiefScientists) {
        this.chiefScientists = (Collection<Person>) chiefScientists;
    }

    @Override
    public Collection<? extends ISeaArea> getSeaAreas() {
        return seaAreas;
    }

    @Override
    public void setSeaAreas(Collection<? extends ISeaArea> seaAreas) {
        this.seaAreas = (Collection<SeaArea>) seaAreas;
    }

    @Override
    public Collection<? extends IProgram> getPrograms() {
        return programs;
    }

    @Override
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
    public void setPrograms(Collection<? extends IProgram> programs) {
        this.programs = (Collection<Program>) programs;
    }

    @Override
    public IPlatform getPlatform() {
        return platform;
    }

    @Override
    public void setPlatform(IPlatform platform) {
        this.platform = (Platform) platform;
    }

    @Override
    public String getObjectives() {
        return objectives;
    }

    @Override
    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    @Override
    public boolean getIsCancelled() {
        return isCancelled;
    }

    @Override
    public void setIsCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public Collection<? extends ILinkedDataTerm> getP02() {
        return P02;
    }

    @Override
    public void setP02(Collection<? extends ILinkedDataTerm> P02) {
        this.P02 = (Collection<LinkedDataTerm>) P02;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Collection<Event> getEvents() {
        return this.events;
    }

    @Override
    public void setEvents(Collection<? extends IEvent> events) {
        this.events = (Collection<Event>) events;
    }

    public String getFinalReportUrl() {
        return finalReportUrl;
    }

    public void setFinalReportUrl(String finalReportUrl) {
        this.finalReportUrl = finalReportUrl;
    }

    public String getPlanningUrl() {
        return planningUrl;
    }

    public void setPlanningUrl(String planningUrl) {
        this.planningUrl = planningUrl;
    }

    public String getTrackImageUrl() {
        return trackImageUrl;
    }

    public void setTrackImageUrl(String trackImageUrl) {
        this.trackImageUrl = trackImageUrl;
    }

    public String getTrackGmlUrl() {
        return trackGmlUrl;
    }

    public void setTrackGmlUrl(String trackGmlUrl) {
        this.trackGmlUrl = trackGmlUrl;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    @Override
    public Collection<ILinkedDataTerm> getProjectTerms() {
        Collection<ILinkedDataTerm> r = new ArrayList<>();
        if (programs != null) {
            for (IProgram p : programs) {
                if (p.getProjects() != null) {
                    for (IProject pr : p.getProjects()) {
                        r.add(pr.getTerm());
                    }
                }
            }
        }
        return r;
    }

    @Override
    public Collection<ILinkedDataTerm> getSeaAreaTerms() {
        Collection<ILinkedDataTerm> r = new HashSet<>();
        if (seaAreas != null) {
            for (ISeaArea a : seaAreas) {
                if (a.getTerm() != null) {
                    r.add(a.getTerm());
                }
            }
        }
        return r;
    }

    @Override
    public Set<ILinkedDataTerm> getInstrumentTypes() {
        Set<ILinkedDataTerm> toolCategories = new HashSet();
        if (events != null) {
            for (IEvent event : events) {
                toolCategories.add(event.getToolCategory());
            }
        }
        return toolCategories;
    }

    @Override
    public Set<? extends ITool> getInstruments() {
        Set<ITool> tools = new HashSet();
        if (events != null) {
            for (IEvent event : events) {
                tools.add(event.getTool());
            }
        }
        return tools;
    }

    public Collection<Navigation> getNavigations() {
        return this.navigations;
    }

    public void setNavigations(Collection<Navigation> navigations) {
        this.navigations = navigations;
    }

    @Override
    public Collection<? extends ICoordinate> getTrack() {
        return this.track;
    }

    @Override
    public void setTrack(Collection<? extends ICoordinate> coordinates) {
        this.track = (Collection<Coordinate>) coordinates;
    }

    @Override
    public void setSouthBoundLatitude(double y) {
        this.southBoundLatitude = y;
    }

    @Override
    public void setNorthBoundLatitude(double y) {
        this.northBoundLatitude = y;
    }

    @Override
    public void setWestBoundLongitude(double x) {
        this.westBoundLongitude = x;
    }

    @Override
    public void setEastBoundLongitude(double x) {
        this.eastBoundLongitude = x;
    }

    @Override
    public double getWestBoundLongitude() {
        return westBoundLongitude;
    }

    @Override
    public double getEastBoundLongitude() {
        return eastBoundLongitude;
    }

    @Override
    public double getNorthBoundLatitude() {
        return northBoundLatitude;
    }

    @Override
    public double getSouthBoundLatitude() {
        return southBoundLatitude;
    }

    @Override
    public String getPurpose() {
        return purpose;
    }

    @Override
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
