/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import eu.eurofleets.ears3.domain.Program;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author thomas
 */
@XmlRootElement(name = "cruises")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProgramDTOList {

    @XmlElement(name = "cruise")
    private List<ProgramDTO> programs;

    public ProgramDTOList() {
    }

    public ProgramDTOList(List<ProgramDTO> programs) {
        this.programs = programs;
    }

    public ProgramDTOList(Collection<Program> programs) {
        if (this.programs == null) {
            this.programs = new ArrayList<>();
        }
        for (Program program : programs) {
            this.programs.add(new ProgramDTO(program));
        }
    }

    public List<ProgramDTO> getPrograms() {
        if (this.programs == null) {
            this.programs = new ArrayList<>();
        }
        return this.programs;
    }

    public void setPrograms(List<ProgramDTO> events) {
        this.programs = events;
    }
}
