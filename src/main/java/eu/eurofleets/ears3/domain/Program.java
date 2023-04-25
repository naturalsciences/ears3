package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ICruise;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProject;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas Vandenberghe
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Program implements IProgram, Serializable, Comparable {

    @Column(unique = true, nullable = false)
    private String identifier;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    @JsonIgnore
    private Long id;
    @XmlTransient
    @JsonIgnore
    @ManyToMany(mappedBy = "programs")
    private Collection<Cruise> cruises;
    @ManyToMany()
    private Collection<Person> principalInvestigators;

    @Column(length = 4000)
    private String description;
    @ManyToMany()
    private Collection<Project> projects;
    private String name;
    @Column(length = 2000)
    private String sampling;

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

    public void addCruise(Cruise cruise) {
        this.cruises.add(cruise);
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

    @Override
    public String getSampling() {
        return sampling;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setSampling(String sampling) {
        this.sampling = sampling;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.identifier);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Program other = (Program) obj;
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object obj) {
        if (this == obj) {
            return 0;
        }
        if (obj == null) {
            return -1;
        }
        if (getClass() != obj.getClass()) {
            return -1;
        }
        final Program other = (Program) obj;
        return this.identifier.compareTo(other.identifier);
    }
}
