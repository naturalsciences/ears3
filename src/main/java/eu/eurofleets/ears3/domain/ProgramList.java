package eu.eurofleets.ears3.domain;

import java.util.Collection;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
