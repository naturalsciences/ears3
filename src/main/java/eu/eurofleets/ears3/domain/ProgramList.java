package eu.eurofleets.ears3.domain;

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

    public ProgramList(List<Program> programs) {
        this.programs = programs;
    }

    @XmlElement(name = "program")
    private List<Program> programs = null;

    public List<Program> getPrograms() {
        return this.programs;
    }

    public void setPrograms(List<Program> programs) {
        this.programs = programs;
    }
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/domain/program/ProgramList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
