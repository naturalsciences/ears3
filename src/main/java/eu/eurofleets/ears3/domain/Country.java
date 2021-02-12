/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ICountry;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author thomas
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Country implements ICountry, Serializable {

    @OneToOne(optional = false)
    private LinkedDataTerm term;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Country() {
    }

    @Override
    public ILinkedDataTerm getTerm() {
        return term;
    }

    @Override
    public void setTerm(ILinkedDataTerm country) {
        this.term = (LinkedDataTerm) country;
    }

    public Country(ILinkedDataTerm countryTerm) {
        this.term = (LinkedDataTerm) countryTerm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.term);
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
        final Country other = (Country) obj;
        if (!Objects.equals(this.term, other.term)) {
            return false;
        }
        return true;
    }

    

}
