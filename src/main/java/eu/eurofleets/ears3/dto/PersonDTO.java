/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Thomas Vandenberghe
 */
@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class PersonDTO {

    private String firstName;
    private String lastName;
    private String organisation; //an identifier in an external vocabulary, e.g. EDMO (can be urn or url)
    private String phoneNumber;
    private String faxNumber;
    private String email;

    public PersonDTO() {

    }

    public PersonDTO(String firstName, String lastName, String organisation, String phoneNumber, String faxNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organisation = organisation;
        this.phoneNumber = phoneNumber;
        this.faxNumber = faxNumber;
        this.email = email;
    }

    public PersonDTO(String firstName, String lastName, String organisation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.organisation = organisation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    

}
