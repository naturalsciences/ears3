package eu.eurofleets.ears3.dto;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Thomas Vandenberghe
 */
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class LinkedDataTermDTO {

    public String identifier;  //an identifier in an external vocabulary, i.e. the EARS ontology
    public String transitiveIdentifier;  //an identifier in a transitive vocanbulary, i.e. the BODC Tool list L22 (can only be url)
    public String name;

    public LinkedDataTermDTO() {
    }

    public LinkedDataTermDTO(String identifier, String transitiveIdentifier, String name) {
        this.identifier = identifier;
        this.transitiveIdentifier = transitiveIdentifier;
        this.name = name;
    }

    public LinkedDataTermDTO(ILinkedDataTerm linkedDataTerm) {
        this.identifier = linkedDataTerm.getIdentifier();
        this.transitiveIdentifier = linkedDataTerm.getTransitiveIdentifier();
        this.name = linkedDataTerm.getName();
    }

}
