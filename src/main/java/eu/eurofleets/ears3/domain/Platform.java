package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ICruise;
import be.naturalsciences.bmdc.cruise.model.IEvent;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IOrganisation;
import be.naturalsciences.bmdc.cruise.model.IPlatform;
import be.naturalsciences.bmdc.cruise.model.ITool;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
public class Platform implements IPlatform, Serializable {

    @OneToOne(optional = false)
//    @Cascade({ org.hibernate.annotations.CascadeType.ALL })
    private LinkedDataTerm term;  //an identifier in an external vocabulary, e.g. C17 (can be urn or url)
    @ManyToOne(optional = true)
    private LinkedDataTerm platformClass; //an identifier in an external vocabulary, e.g. L06 (can be urn or url)
    @ManyToOne(optional = true)
    private Organisation vesselOperator; //unfortunately not retrievable!
    @OneToMany(mappedBy = "platform")
    @XmlTransient
    @JsonIgnore
    private Collection<Cruise> cruises;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "platform")
    @XmlTransient
    @JsonIgnore
    private Collection<Event> events;

    public Platform() {
    }

    public Platform(ILinkedDataTerm platformTerm, ILinkedDataTerm platformClass, IOrganisation vesselOperator) {
        this.term = (LinkedDataTerm) platformTerm;
        this.platformClass = (LinkedDataTerm) platformClass;
        this.vesselOperator = (Organisation) vesselOperator;
        this.cruises = new ArrayList<>();
    }

    @Override
    public ILinkedDataTerm getTerm() {
        return term;
    }

    @Override
    public void setTerm(ILinkedDataTerm platformTerm) {
        this.term = (LinkedDataTerm) platformTerm;
    }

    @Override
    public ILinkedDataTerm getPlatformClass() {
        return platformClass;
    }

    @Override
    public void setPlatformClass(ILinkedDataTerm platformClass) {
        this.platformClass = (LinkedDataTerm) platformClass;
    }

    @Override
    public IOrganisation getVesselOperator() {
        return vesselOperator;
    }

    @Override
    public void setVesselOperator(IOrganisation organisation) {
        this.vesselOperator = (Organisation) organisation;
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

    @Override
    public Collection<Cruise> getCruises() {
        return cruises;
    }

    @Override
    public void addCruise(ICruise cruise) {
        cruises.add((Cruise) cruise);
    }

    @Override
    public Collection<? extends IEvent> getEvents() {
        return this.events;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
