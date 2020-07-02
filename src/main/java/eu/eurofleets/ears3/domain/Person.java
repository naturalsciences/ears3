package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.IOrganisation;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import eu.eurofleets.ears3.dto.PersonDTO;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
/*@Table(
        uniqueConstraints
        = @UniqueConstraint(columnNames = {"first_name", "last_name"})
)*/
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Person implements IPerson, Serializable {

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @ManyToOne(optional = true)
    private Organisation organisation; //an identifier in an external vocabulary, e.g. EDMO (can be urn or url)

    private String phoneNumber;
    private String faxNumber;
    private String email;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Person() {
    }

    public Person(PersonDTO dto) {
        this.firstName = dto.firstName;
        this.lastName = dto.lastName;
    }

    public Person(String firstName, String lastName, IOrganisation organisation, String phoneNumber, String faxNumber, String email) {
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
    public String _getEmailAddress() {
        return email;
    }

    @Override
    public void _setEmailAddress(String email) {
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

}
