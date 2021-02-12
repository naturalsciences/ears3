package eu.eurofleets.ears3.domain;

import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "programs")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProgramList {

    public ProgramList() {
    }

    public ProgramList(Collection<Program> programs) {
        this.programs = programs;
    }

    @XmlElement(name = "program")
    private Collection<Program> programs = null;

    public Collection<Program> getPrograms() {
        return this.programs;
    }

    public void setPrograms(Collection<Program> programs) {
        this.programs = programs;
    }
}
