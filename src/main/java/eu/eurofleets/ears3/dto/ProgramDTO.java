/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProject;
import eu.eurofleets.ears3.domain.Program;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Thomas Vandenberghe
 */
@XmlRootElement(name = "program")
@XmlAccessorType(XmlAccessType.FIELD) // ignore all the getters
public class ProgramDTO {

    public String identifier;
    public List<PersonDTO> principalInvestigators = new ArrayList<>();
    public String description;
    public List<String> projects = new ArrayList<>();
    public String name;
    public String sampling;

    public ProgramDTO() {
    }

    public ProgramDTO(String identifier, List<PersonDTO> principalInvestigators, String description,
            List<String> projects, String name, String sampling) {
        this.identifier = identifier;
        this.principalInvestigators = principalInvestigators;
        this.description = description;
        this.projects = projects;
        this.name = name;
        this.sampling = sampling;
    }

    public ProgramDTO(IProgram program) {
        this((Program) program);
    }

    public ProgramDTO(Program program) {
        this.identifier = program.getIdentifier();
        for (IPerson pi : program.getPrincipalInvestigators()) {
            PersonDTO piDTO = new PersonDTO(pi.getFirstName(), pi.getLastName(),
                    pi.getOrganisation() != null ? pi.getOrganisation().getTerm().getIdentifier() : null,
                    pi._getPhoneNumber(), pi._getFaxNumber(), pi.getEmail());
            this.principalInvestigators.add(piDTO);
        }
        this.description = program.getDescription();
        for (IProject project : program.getProjects()) {
            this.projects.add(project.getTerm().getIdentifier());
        }
        this.name = program.getName();
        this.sampling = program.getSampling();
    }

}
