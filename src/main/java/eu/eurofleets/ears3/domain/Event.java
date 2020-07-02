package eu.eurofleets.ears3.domain;

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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Event implements IEvent, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String identifier;
    private String eventDefinitionId;
    private OffsetDateTime timeStamp;
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

    @OneToMany()
    private Collection<Property> properties;

    @ManyToOne(optional = true)
    private Program program;

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

    @Override
    public String toString() {
        return tool.getTerm().getName() + " " + process.getName() + " " + action.getName() + " at " + timeStamp.format(DateTimeFormatter.ISO_INSTANT);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
