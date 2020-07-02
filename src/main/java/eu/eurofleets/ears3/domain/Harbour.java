/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ICountry;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IHarbour;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
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
public class Harbour implements IHarbour, Serializable {

    @OneToOne(optional = false)
    private LinkedDataTerm term;
    @ManyToOne(optional = true)
    private Country country;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Harbour() {
    }

    public Harbour(ILinkedDataTerm harbourTerm, ICountry country) {
        this.term = (LinkedDataTerm) harbourTerm;
        this.country = (Country) country;
    }

    @Override
    public ILinkedDataTerm getTerm() {
        return this.term;
    }

    @Override
    public void setTerm(ILinkedDataTerm harbour) {
        term = (LinkedDataTerm) harbour;
    }

    @Override
    public ICountry _getCountry() {
        return country;
    }

    @Override
    public void _setCountry(ICountry country) {
        this.country = (Country) country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
