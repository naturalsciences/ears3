package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.ISeaArea;
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

    private Double southBoundLatitude;
    private Double northBoundLatitude;
    private Double westBoundLongitude;
    private Double eastBoundLongitude;

    public SeaArea() {
    }

    public boolean containsCoordinate(Coordinate coord) {
        if (westBoundLongitude != null && eastBoundLongitude != null && southBoundLatitude != null && northBoundLatitude != null) {
            return coord.x >= westBoundLongitude && coord.x <= eastBoundLongitude && coord.y >= southBoundLatitude && coord.y <= northBoundLatitude;
        } else {
            return false;
        }
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

    public Double getSouthBoundLatitude() {
        return southBoundLatitude;
    }

    public void setSouthBoundLatitude(Double southBoundLatitude) {
        this.southBoundLatitude = southBoundLatitude;
    }

    public Double getNorthBoundLatitude() {
        return northBoundLatitude;
    }

    public void setNorthBoundLatitude(Double northBoundLatitude) {
        this.northBoundLatitude = northBoundLatitude;
    }

    public Double getWestBoundLongitude() {
        return westBoundLongitude;
    }

    public void setWestBoundLongitude(Double westBoundLongitude) {
        this.westBoundLongitude = westBoundLongitude;
    }

    public Double getEastBoundLongitude() {
        return eastBoundLongitude;
    }

    public void setEastBoundLongitude(Double eastBoundLongitude) {
        this.eastBoundLongitude = eastBoundLongitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.term);
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
        final SeaArea other = (SeaArea) obj;
        if (!Objects.equals(this.term, other.term)) {
            return false;
        }
        return true;
    }

}
