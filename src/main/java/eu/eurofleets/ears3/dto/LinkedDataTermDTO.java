package eu.eurofleets.ears3.dto;

import eu.eurofleets.ears3.domain.*;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Transient;
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
    
}
