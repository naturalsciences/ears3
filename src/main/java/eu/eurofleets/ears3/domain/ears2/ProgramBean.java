/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain.ears2;

/**
 *
 * @author Yvan Stojanov
 */
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProject;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.Project;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://www.eurofleets.eu/", name = "program")
public class ProgramBean implements Serializable, Cloneable, Comparable<ProgramBean> {

    private IProgram program;

    public ProgramBean() {

    }

    public ProgramBean(IProgram program) {
        this.program = program;
    }

    @XmlAttribute(name = "programId")
    public String getProgramId() {
        return this.program.getIdentifier();
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "principalInvestigator")
    public String getPrincipalInvestigator() {
        StringJoiner sj = new StringJoiner(",");
        for (IPerson cs : program.getPrincipalInvestigators()) {
            String firstName = cs.getFirstName().replace(" ", "+");
            String lastName = cs.getLastName().replace(" ", "+");
            String edmo = null;
            String org = null;
            String country = null;
            if (cs.getOrganisation() != null && cs.getOrganisation().getTerm() != null) {
                ILinkedDataTerm term = cs.getOrganisation().getTerm();
                edmo = term.getUrn();
                org = term.getName();
                country = cs.getOrganisation()._getCountry().getTerm().getName();
            }
            sj.add("{\"firstName\":\"" + firstName + "\",\"lastName\":\"" + lastName + "\",\"organisationCode\":\"" + edmo + "\",\"organisationName\":\"" + org + "\",\"country\":\"" + country + "\"}");
        }
        return "[" + sj.toString() + "]";
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "description")
    public String getDescription() {
        return this.program.getDescription();
    }

    @XmlElementWrapper(namespace = "http://www.eurofleets.eu/", name = "projects")
    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "projects")
    public Set<ProjectBean> getProjects() {
        Set<ProjectBean> res = new HashSet<>();
        for (IProject project : this.program.getProjects()) {
            ProjectBean pb = new ProjectBean((Project) project);
            res.add(pb);
        }
        return res;
    }

    @Override
    public String toString() {
        return this.program.toString();
    }

    @Override
    public int compareTo(ProgramBean other) {
        return this.getProgramId().compareToIgnoreCase(other.getProgramId());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ProgramBean) {
            ProgramBean other = (ProgramBean) o;
            return this.getProgramId().equals(other.getProgramId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.getProgramId());
        return hash;
    }
}
