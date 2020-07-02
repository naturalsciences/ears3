/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import java.util.List;

/**
 *
 * @author thomas
 */
public class ProgramDTO {

    public String identifier;
    public List<PersonDTO> principalInvestigators;
    public String description;
    public List<String> projects;

    public ProgramDTO() {
    }

    public ProgramDTO(String identifier, List<PersonDTO> principalInvestigators, String description, List<String> projects) {
        this.identifier = identifier;
        this.principalInvestigators = principalInvestigators;
        this.description = description;
        this.projects = projects;
    }

}
