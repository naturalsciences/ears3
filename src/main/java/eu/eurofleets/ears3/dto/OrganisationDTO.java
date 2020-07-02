/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import eu.eurofleets.ears3.domain.*;
import be.naturalsciences.bmdc.cruise.model.ICountry;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IOrganisation;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author thomas
 */
public class OrganisationDTO {

    public LinkedDataTermDTO term;
    public String phoneNumber;
    public String faxNumber;
    public String emailAddress;
    public String website;
    public String deliveryPoint;
    public String city;
    public String postalCode;

    public String country;

    public OrganisationDTO() {
    }

}
