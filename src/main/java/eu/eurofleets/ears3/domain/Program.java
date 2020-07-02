package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ICruise;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProject;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Program implements IProgram, Serializable {

    private String identifier;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany()
    private Collection<Cruise> cruises;
    @ManyToMany()
    private Collection<Person> principalInvestigators;

    private String description;
    @ManyToMany()
    private Collection<Project> projects;

    public Program() {
    }

    public Program(String identifier, Collection<? extends ICruise> cruises, Collection<? extends IPerson> principalInvestigators, String description, Collection<? extends IProject> projects) {
        this.identifier = identifier;
        this.cruises = (Collection<Cruise>) cruises;
        this.principalInvestigators = (Collection<Person>) principalInvestigators;
        this.description = description;
        this.projects = (Collection<Project>) projects;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Collection<? extends ICruise> getCruises() {
        return cruises;
    }

    @Override
    public void setCruises(Collection<? extends ICruise> cruises) {
        this.cruises = (Collection<Cruise>) cruises;
    }

    @Override
    public Collection<? extends IPerson> getPrincipalInvestigators() {
        return principalInvestigators;
    }

    @Override
    public void setPrincipalInvestigators(Collection<? extends IPerson> principalInvestigators) {
        this.principalInvestigators = (Collection<Person>) principalInvestigators;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Collection<? extends IProject> getProjects() {
        return projects;
    }

    @Override
    public void setProjects(Collection<? extends IProject> projects) {
        this.projects = (Collection<Project>) projects;
    }
}
