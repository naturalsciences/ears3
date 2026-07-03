package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.IOrganisation;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import eu.eurofleets.ears3.dto.PersonDTO;
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "first_name", "last_name", "organisation_id" }))
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Person implements IPerson, Serializable {

    @Column(nullable = false, name = "first_name")
    private String firstName;
    @Column(nullable = false, name = "last_name")
    private String lastName;
    @ManyToOne(optional = true)
    private Organisation organisation; //an identifier in an external vocabulary, e.g. EDMO (can be urn or url)

    private String phoneNumber;
    private String faxNumber;
    //    @Column(unique = true) //do not have a unique constraint on email address. EDMO codes can detail at the sub-organisation level. Persons may move to different departments, and change organisation_id, but their email address remains.
    private String email;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @XmlTransient
//    @JsonIgnore
    private Long id;

    public Person() {
    }

    /**
     * Create a person from a PersonDTO. This does not add the Organisation.
     * @param dto
     */
    public Person(PersonDTO dto) {
        this.firstName = dto.getFirstName();
        this.lastName = dto.getLastName();
        this.phoneNumber = dto.getPhoneNumber();
        this.faxNumber = dto.getFaxNumber();
        this.email = dto.getEmail();
    }

    public Person(String firstName, String lastName, IOrganisation organisation, String phoneNumber, String faxNumber,
            String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organisation = (Organisation) organisation;
        this.phoneNumber = phoneNumber;
        this.faxNumber = faxNumber;
        this.email = email;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    @JsonDeserialize(as = Organisation.class)
    public IOrganisation getOrganisation() {
        return organisation;
    }

    @Override
    public void setOrganisation(IOrganisation organisation) {
        this.organisation = (Organisation) organisation;
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
    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.firstName);
        hash = 67 * hash + Objects.hashCode(this.lastName);
        hash = 67 * hash + Objects.hashCode(this.organisation);
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
        final Person other = (Person) obj;
        if (!Objects.equals(this.firstName, other.firstName)) {
            return false;
        }
        if (!Objects.equals(this.lastName, other.lastName)) {
            return false;
        }
        if (!Objects.equals(this.organisation, other.organisation)) {
            return false;
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlTransient
    @JsonIgnore
    public String getFirstNameLastName() {
        return getFirstName() + " " + getLastName();
    }

}
