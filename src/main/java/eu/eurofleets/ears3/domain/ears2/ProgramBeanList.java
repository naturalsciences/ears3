/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain.ears2;

import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Program;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author thomas
 */
@XmlRootElement(name = "programs")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProgramBeanList {

    @XmlElement(name = "program")
    private Collection<ProgramBean> programs = null;

    public ProgramBeanList() {

    }

    public ProgramBeanList(Collection<ProgramBean> programs) {
        this.programs = programs;
    }

    public ProgramBeanList(Collection<Program> programs, boolean fake) {
        Collection<ProgramBean> res = new ArrayList<>();
        for (Program p : programs) {
            ProgramBean program = new ProgramBean(p);
            res.add(program);
        }
        this.programs = res;
    }
}
