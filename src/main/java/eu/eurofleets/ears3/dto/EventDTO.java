package eu.eurofleets.ears3.dto;

import eu.eurofleets.ears3.domain.*;
import be.naturalsciences.bmdc.cruise.model.IEvent;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProperty;
import be.naturalsciences.bmdc.cruise.model.ITool;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thomas
 */
public class EventDTO {

    public String identifier;
    public String eventDefinitionId;
    public OffsetDateTime timeStamp;
    public PersonDTO actor;
    public LinkedDataTermDTO subject;
    public ToolDTO tool;
    public LinkedDataTermDTO toolCategory;
    public LinkedDataTermDTO process;
    public LinkedDataTermDTO action;
    public Collection<PropertyDTO> properties;
    public int program;

    public EventDTO() {
    }

    public EventDTO(String identifier, String eventDefinitionId, OffsetDateTime timeStamp, PersonDTO actor, LinkedDataTermDTO subject, ToolDTO tool, LinkedDataTermDTO toolCategory, LinkedDataTermDTO process, LinkedDataTermDTO action, Collection<PropertyDTO> properties, int program) {
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
    }

}
