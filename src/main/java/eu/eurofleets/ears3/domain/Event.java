package eu.eurofleets.ears3.domain;

import eu.eurofleets.ears3.utilities.OffsetDateTimeAdapter;
import be.naturalsciences.bmdc.cruise.model.IEvent;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IPlatform;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProperty;
import be.naturalsciences.bmdc.cruise.model.ITool;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Event implements IEvent, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String identifier;
    private String eventDefinitionId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    private OffsetDateTime timeStamp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    private OffsetDateTime creationTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    private OffsetDateTime modificationTime;
    @ManyToOne(optional = true)
    private Person actor;
    @ManyToOne(optional = false)
    private LinkedDataTerm subject;
    @ManyToOne(optional = false)
    private Tool tool;
    @ManyToOne(optional = false)
    private LinkedDataTerm toolCategory;
    @ManyToOne(optional = false)
    private LinkedDataTerm process;
    @ManyToOne(optional = false)
    private LinkedDataTerm action;
    @ManyToOne(optional = false)
    private Platform platform;

    @OneToMany()
    @XmlElementWrapper(name = "properties")
    @XmlElement(name = "property")
    private Collection<Property> properties;

    @ManyToOne(optional = true)
    private Program program;

    @ManyToMany()
    @JoinTable(
            name = "event_navigation",
            joinColumns = {
                @JoinColumn(name = "event_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "navigation_id")}
    )
    private Collection<Navigation> navigation;

    @ManyToMany()
    @JoinTable(
            name = "event_thermosal",
            joinColumns = {
                @JoinColumn(name = "event_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "thermosal_id")}
    )
    private Collection<Thermosal> thermosal;

    @ManyToMany()
    @JoinTable(
            name = "event_weather",
            joinColumns = {
                @JoinColumn(name = "event_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "weather_id")}
    )
    private Collection<Weather> weather;

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getEventDefinitionId() {
        return eventDefinitionId;
    }

    @Override
    public void setEventDefinitionId(String eventDefinitionId) {
        this.eventDefinitionId = eventDefinitionId;
    }

    @Override
    public OffsetDateTime getTimeStamp() {
        return timeStamp;
    }

    @Override
    public void setTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public IPerson getActor() {
        return actor;
    }

    @Override
    public void setActor(IPerson actor) {
        this.actor = (Person) actor;
    }

    @Override
    public ILinkedDataTerm getSubject() {
        return subject;
    }

    @Override
    public void setSubject(ILinkedDataTerm subject) {
        this.subject = (LinkedDataTerm) subject;
    }

    @Override
    public ITool getTool() {
        return tool;
    }

    @Override
    public void setTool(ITool tool) {
        this.tool = (Tool) tool;
    }

    @Override
    public ILinkedDataTerm getToolCategory() {
        return toolCategory;
    }

    @Override
    public void setToolCategory(ILinkedDataTerm toolCategory) {
        this.toolCategory = (LinkedDataTerm) toolCategory;
    }

    @Override
    public ILinkedDataTerm getProcess() {
        return process;
    }

    @Override
    public void setProcess(ILinkedDataTerm process) {
        this.process = (LinkedDataTerm) process;
    }

    @Override
    public ILinkedDataTerm getAction() {
        return action;
    }

    @Override
    public void setAction(ILinkedDataTerm action) {
        this.action = (LinkedDataTerm) action;
    }

    @Override
    public Collection<? extends IProperty> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Collection<? extends IProperty> properties) {
        this.properties = (Collection<Property>) properties;
    }

    @Override
    public IProgram getProgram() {
        return program;
    }

    @Override
    public void setProgram(IProgram program) {
        this.program = (Program) program;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Platform getPlatform() {
        return platform;
    }

    @Override
    public void setPlatform(IPlatform platform) {
        this.platform = (Platform) platform;
    }

    public OffsetDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(OffsetDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public OffsetDateTime getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(OffsetDateTime modificationTime) {
        this.modificationTime = modificationTime;
    }

    public Collection<Navigation> getNavigation() {
        return navigation;
    }

    public void setNavigation(Collection<Navigation> navigation) {
        this.navigation = navigation;
    }

    public Collection<Thermosal> getThermosal() {
        return thermosal;
    }

    public void setThermosal(Collection<Thermosal> thermosal) {
        this.thermosal = thermosal;
    }

    public Collection<Weather> getWeather() {
        return weather;
    }

    public void setWeather(Collection<Weather> weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return tool.getTerm().getName() + " " + process.getName() + " " + action.getName() + " at " + timeStamp.format(DateTimeFormatter.ISO_INSTANT);
    }

    /**
     * *
     * Return the value and unit of a property of this provided the propertyUrl
     * parameter.
     *
     * @param propertyUrl
     * @return
     */
    public List<String> getPropertyValues(String propertyUrl) {
        List<String> r = new ArrayList<>();
        for (Property property : properties) {
            if (property.getKey().getIdentifier().equals(propertyUrl)) {
                if (property.getUom() != null) {
                    r.add(property.getValue() + " " + property.getUom());
                } else {
                    r.add(property.getValue());
                }
            }

        }
        return r;
    }

}
