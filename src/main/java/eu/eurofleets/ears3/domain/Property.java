package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IProperty;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import tools.jackson.databind.annotation.JsonDeserialize;

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
public class Property implements IProperty, Serializable {

    @ManyToOne(optional = false)
    private LinkedDataTerm key;
    @Column(name="\"value\"")
    private String value;
    private String uom;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Property() {
    }

    public Property(LinkedDataTerm key, String value, String uom) {
        this.key = key;
        this.value = value;
        this.uom = uom;
    }

    @Override
    @JsonDeserialize(as = LinkedDataTerm.class)
    public ILinkedDataTerm getKey() {
        return key;
    }

    @Override
    public void setKey(ILinkedDataTerm key) {
        this.key = (LinkedDataTerm) key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getUom() {
        return uom;
    }

    @Override
    public void setUom(String uom) {
        if ("".equals(uom)) {
            uom = null;
        }
        this.uom = uom;
    }

    public Property(ILinkedDataTerm key, String value) {
        this.key = (LinkedDataTerm) key;
        this.value = value;
        setUom(uom);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
