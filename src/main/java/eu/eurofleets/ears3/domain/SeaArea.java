package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.ISeaArea;
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
public class SeaArea implements ISeaArea, Serializable {

    @OneToOne(optional = false)
    private LinkedDataTerm term;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public SeaArea() {
    }

    public SeaArea(ILinkedDataTerm seaAreaTerm) {
        this.term = (LinkedDataTerm) seaAreaTerm;
    }

    @Override
    public ILinkedDataTerm getTerm() {
        return this.term;
    }

    @Override
    public void setTerm(ILinkedDataTerm seaAreaTerm) {
        this.term = (LinkedDataTerm) seaAreaTerm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
