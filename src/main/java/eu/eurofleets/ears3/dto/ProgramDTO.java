/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author thomas
 */
@XmlRootElement(name = "program")
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class ProgramDTO {

    public String identifier;
    public List<PersonDTO> principalInvestigators;
    public String description;
    public List<String> projects;
    public String name;
    public String sampling;

    public ProgramDTO() {
    }

    public ProgramDTO(String identifier, List<PersonDTO> principalInvestigators, String description, List<String> projects, String name, String sampling) {
        this.identifier = identifier;
        this.principalInvestigators = principalInvestigators;
        this.description = description;
        this.projects = projects;
        this.name = name;
        this.sampling = sampling;
    }

}
