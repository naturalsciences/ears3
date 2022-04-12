/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ICountry;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IOrganisation;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author thomas
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Organisation implements IOrganisation, Serializable {

    @OneToOne(optional = false)
    private LinkedDataTerm term;
    @XmlTransient
    @JsonIgnore
    private String phoneNumber;
    @XmlTransient
    @JsonIgnore
    private String faxNumber;
    @XmlTransient
    @JsonIgnore
    private String emailAddress;
    @XmlTransient
    @JsonIgnore
    private String website;
    @XmlTransient
    @JsonIgnore
    private String deliveryPoint;
    @XmlTransient
    @JsonIgnore
    private String city;
    @XmlTransient
    @JsonIgnore
    private String postalCode;
    @ManyToOne(optional = true)
    private Country country;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    @JsonIgnore
    private Long id;

    public Organisation() {
    }

    /*public Organisation(OrganisationDTO dto){
        this.
    }*/
    @Override
    public ILinkedDataTerm getTerm() {
        return term;
    }

    @Override
    public void setTerm(ILinkedDataTerm organisationTerm) {
        this.term = (LinkedDataTerm) organisationTerm;
    }

    @Override
    public ICountry _getCountry() {
        return country;
    }

    @Override
    public void _setCountry(ICountry country) {
        this.country = (Country) country;
    }

    @Override
    public String _getDeliveryPoint() {
        return deliveryPoint;
    }

    @Override
    public void _setDeliveryPoint(String deliveryPoint) {
        this.deliveryPoint = deliveryPoint;
    }

    @Override
    public String _getCity() {
        return city;
    }

    @Override
    public void _setCity(String city) {
        this.city = city;
    }

    @Override
    public String _getPostalcode() {
        return postalCode;
    }

    @Override
    public void _setPostalcode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String _getWebsite() {
        return website;
    }

    @Override
    public void _setWebsite(String website) {
        this.website = website;
    }

    @Override
    public String _getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void _setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String _getFaxNumber() {
        return faxNumber;
    }

    @Override
    public void _setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    @Override
    public String _getEmailAddress() {
        return emailAddress;
    }

    @Override
    public void _setEmailAddress(String emailAddresss) {
        this.emailAddress = emailAddresss;
    }

    public Organisation(ILinkedDataTerm organisationTerm, String phoneNumber, String faxNumber, String emailAddress, String website, String deliveryPoint, String city, String postalCode, ICountry country) {
        this.term = (LinkedDataTerm) organisationTerm;
        this.phoneNumber = phoneNumber;
        this.faxNumber = faxNumber;
        this.emailAddress = emailAddress;
        this.website = website;
        this.deliveryPoint = deliveryPoint;
        this.city = city;
        this.postalCode = postalCode;
        this.country = (Country) country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
