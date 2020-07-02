package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Transient;
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
public class LinkedDataTerm implements ILinkedDataTerm, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String identifier;  //an url in an external vocabulary, i.e. the EARS ontology
    @Column(unique = true, nullable = true, length = 100)
    private String transitiveIdentifier;  //an identifier in a transitive vocanbulary, i.e. the BODC Tool list L22 (can only be url)
    @Column(unique = false, nullable = false, length = 256)
    private String name;
    @Column(unique = true, nullable = true, length = 100)
    private String urn;
    @Column(unique = false, nullable = true, length = 100)
    private String transitiveUrn;

    public LinkedDataTerm() {
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTransitiveIdentifier() {
        return transitiveIdentifier;
    }

    public void setTransitiveIdentifier(String transitiveIdentifier) {
        this.transitiveIdentifier = transitiveIdentifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    /*public LinkedDataTerm(String identifier, String name) {
        this.identifier = identifier;
        this.name = name;
    }*/
    public LinkedDataTerm(String identifier, String transitiveIdentifier, String name) {
        this.identifier = ILinkedDataTerm.cleanUrl(identifier);
        this.name = name;
        this.transitiveIdentifier = transitiveIdentifier;
        this.urn = ILinkedDataTerm.getUrnFromUrl(identifier);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public ILinkedDataTerm getTerm() {
        return this;
    }

    @Override
    public void setTerm(ILinkedDataTerm term) {
        this.identifier = term.getIdentifier();
        this.transitiveIdentifier = term.getTransitiveIdentifier();
        this.urn = term.getUrn();
        this.transitiveUrn = term.getTransitiveUrn();
        this.name = term.getName();
    }

    @Override
    public String getUrn() {
        return urn;
    }

    @Override
    public void setUrn(String urn) {
        this.urn = urn;
    }

    @Override
    public String getTransitiveUrn() {
        return transitiveUrn;
    }

    @Override
    public void setTransitiveUrn(String transitiveUrn) {
        this.transitiveUrn = transitiveUrn;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.identifier);
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
        final LinkedDataTerm other = (LinkedDataTerm) obj;
        if (!Objects.equals(this.identifier, other.identifier)) {
            return false;
        }
        return true;
    }

}
