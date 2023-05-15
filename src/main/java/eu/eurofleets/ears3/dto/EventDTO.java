package eu.eurofleets.ears3.dto;

import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import eu.eurofleets.ears3.domain.*;
import eu.eurofleets.ears3.utilities.OffsetDateTimeAdapter;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas Vandenberghe
 */
@XmlRootElement(name = "event")
@XmlAccessorType(XmlAccessType.FIELD) // ignore all the getters
public class EventDTO {

    private String identifier;
    private String eventDefinitionId;
    private String label;
    private String description;
    private String station;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    private OffsetDateTime timeStamp;
    private PersonDTO actor;
    private LinkedDataTermDTO subject;
    private ToolDTO tool;
    private LinkedDataTermDTO toolCategory;
    private LinkedDataTermDTO process;
    private LinkedDataTermDTO action;
    private Collection<PropertyDTO> properties;
    private String program;
    private String platform;
    private Collection<NavigationDTO> navigation;
    private Collection<ThermosalDTO> thermosal;
    private Collection<WeatherDTO> weather;

    public EventDTO() {
    }

    public EventDTO(String identifier, String eventDefinitionId, OffsetDateTime timeStamp, PersonDTO actor,
            LinkedDataTermDTO subject, ToolDTO tool, LinkedDataTermDTO toolCategory, LinkedDataTermDTO process,
            LinkedDataTermDTO action, Collection<PropertyDTO> properties, String program, String platform) {
        this.identifier = identifier;
        this.eventDefinitionId = eventDefinitionId;
        this.timeStamp = timeStamp;
        this.actor = actor;
        this.subject = subject;
        this.tool = tool;
        this.toolCategory = toolCategory;
        this.process = process;
        this.action = action;
        this.properties = properties;
        this.program = program;
        this.platform = platform;
    }

    public EventDTO(Event event) {
        IPerson actor = event.getActor();
        PersonDTO actorDTO = new PersonDTO(actor.getFirstName(), actor.getLastName(),
                actor.getOrganisation() != null ? actor.getOrganisation().getTerm().getIdentifier() : null,
                actor._getPhoneNumber(), actor._getFaxNumber(), actor.getEmail());
        LinkedDataTermDTO subject = new LinkedDataTermDTO(event.getSubject());
        LinkedDataTermDTO toolCategory = new LinkedDataTermDTO(event.getToolCategory());
        ToolDTO tool = new ToolDTO(event.getTool().getTerm(), event.getTool().getParentTool());
        LinkedDataTermDTO process = new LinkedDataTermDTO(event.getProcess());
        LinkedDataTermDTO action = new LinkedDataTermDTO(event.getAction());

        this.properties = new HashSet<>();
        for (IProperty property : event.getProperties()) {
            PropertyDTO prop = new PropertyDTO(new LinkedDataTermDTO(property.getKey()), property.getValue(),
                    property.getUom());
            properties.add(prop);
        }
        this.identifier = event.getIdentifier();
        this.eventDefinitionId = event.getEventDefinitionId();
        this.timeStamp = event.getTimeStamp();
        this.actor = actorDTO;
        this.subject = subject;
        this.tool = tool;
        this.toolCategory = toolCategory;
        this.process = process;
        this.action = action;
        this.program = event.getProgram() != null ? event.getProgram().getIdentifier() : null;
        this.platform = event.getPlatform().getTerm().getIdentifier();
        this.label = event.getLabel();
        this.description = event.getDescription();
        this.station = event.getStation();
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getEventDefinitionId() {
        return eventDefinitionId;
    }

    public OffsetDateTime getTimeStamp() {
        return timeStamp;
    }

    public PersonDTO getActor() {
        return actor;
    }

    public LinkedDataTermDTO getSubject() {
        return subject;
    }

    public ToolDTO getTool() {
        return tool;
    }

    public LinkedDataTermDTO getToolCategory() {
        return toolCategory;
    }

    public LinkedDataTermDTO getProcess() {
        return process;
    }

    public LinkedDataTermDTO getAction() {
        return action;
    }

    public Collection<PropertyDTO> getProperties() {
        return properties;
    }

    public String getProgram() {
        return program;
    }

    public String getPlatform() {
        return platform;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setEventDefinitionId(String eventDefinitionId) {
        this.eventDefinitionId = eventDefinitionId;
    }

    public void setTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setActor(PersonDTO actor) {
        this.actor = actor;
    }

    public void setSubject(LinkedDataTermDTO subject) {
        this.subject = subject;
    }

    public void setTool(ToolDTO tool) {
        this.tool = tool;
    }

    public void setToolCategory(LinkedDataTermDTO toolCategory) {
        this.toolCategory = toolCategory;
    }

    public void setProcess(LinkedDataTermDTO process) {
        this.process = process;
    }

    public void setAction(LinkedDataTermDTO action) {
        this.action = action;
    }

    public void setProperties(Collection<PropertyDTO> properties) {
        this.properties = properties;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Collection<WeatherDTO> getWeather() {
        return weather;
    }

    public void setWeather(Collection<WeatherDTO> weather) {
        this.weather = weather;
    }

    public Collection<NavigationDTO> getNavigation() {
        return navigation;
    }

    public void setNavigation(Collection<NavigationDTO> navigation) {
        this.navigation = navigation;
    }

    public Collection<ThermosalDTO> getThermosal() {
        return thermosal;
    }

    public void setThermosal(Collection<ThermosalDTO> thermosal) {
        this.thermosal = thermosal;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

}
