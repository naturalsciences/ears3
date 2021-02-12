package eu.eurofleets.ears3.dto;

import eu.eurofleets.ears3.domain.*;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import com.fasterxml.jackson.annotation.JsonFormat;
import eu.eurofleets.ears3.utilities.OffsetDateTimeAdapter;
import java.time.OffsetDateTime;
import java.util.Collection;
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
 * @author thomas
 */
@XmlRootElement(name = "event")
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class EventDTO {

    public String identifier;
    public String eventDefinitionId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    public OffsetDateTime timeStamp;
    public PersonDTO actor;
    public LinkedDataTermDTO subject;
    public ToolDTO tool;
    public LinkedDataTermDTO toolCategory;
    public LinkedDataTermDTO process;
    public LinkedDataTermDTO action;
    public Collection<PropertyDTO> properties;
    public String program;
    public String platform;
    public Collection<NavigationDTO> navigation;
    public Collection<ThermosalDTO> thermosal;

    public Collection<WeatherDTO> weather;

    public EventDTO() {
    }

    public EventDTO(String identifier, String eventDefinitionId, OffsetDateTime timeStamp, PersonDTO actor, LinkedDataTermDTO subject, ToolDTO tool, LinkedDataTermDTO toolCategory, LinkedDataTermDTO process, LinkedDataTermDTO action, Collection<PropertyDTO> properties, String program, String platform) {
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
        PersonDTO actorDTO = new PersonDTO(actor.getFirstName(), actor.getLastName(), actor.getOrganisation() != null ? actor.getOrganisation().getTerm().getIdentifier() : null, actor._getPhoneNumber(), actor._getFaxNumber(), actor._getEmailAddress());
        LinkedDataTermDTO subject = new LinkedDataTermDTO(event.getSubject());
        LinkedDataTermDTO toolCategory = new LinkedDataTermDTO(event.getToolCategory());
        ToolDTO tool = new ToolDTO(event.getTool().getTerm(), event.getTool().getParentTool());
        LinkedDataTermDTO process = new LinkedDataTermDTO(event.getProcess());
        LinkedDataTermDTO action = new LinkedDataTermDTO(event.getAction());

        this.identifier = event.getIdentifier();
        this.eventDefinitionId = event.getEventDefinitionId();
        this.timeStamp = event.getTimeStamp();
        this.actor = actorDTO;
        this.subject = subject;
        this.tool = tool;
        this.toolCategory = toolCategory;
        this.process = process;
        this.action = action;
        this.properties = null;
        this.program = event.getProgram().getIdentifier();
        this.platform = event.getPlatform().getTerm().getIdentifier();
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

}
