/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

/**
 *
 * @author thomas
 */
public class PersonDTO {

    public String firstName;
    public String lastName;
    public String organisation; //an identifier in an external vocabulary, e.g. EDMO (can be urn or url)
    public String phoneNumber;
    public String faxNumber;
    public String email;

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

}
