/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain.ears2;

/**
 *
 * @author Thomas Vandenberghe
 */
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import eu.eurofleets.ears3.domain.Cruise;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

//import org.apache.commons.lang3.SerializationUtils;
@XmlRootElement(namespace = "http://www.eurofleets.eu/", name = "cruise")
public class CruiseBean implements Serializable, Cloneable, Comparable<CruiseBean> {

    private static final long serialVersionUID = 1L;

    private Cruise cruise;
    private List<ProgramBean> programs;

    public CruiseBean() {
    }

    public CruiseBean(Cruise cruise) {
        this.cruise = cruise;
    }

    @XmlAttribute(name = "identifier")
    public String getIdentifier() {
        return cruise.getIdentifier();
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "id")
    public String getId() {
        return Long.toString(cruise.getId());
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "cruiseName")
    public String getCruiseName() {
        return cruise.getName();
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "startDate")
    public String getStartDate() {
        return cruise.getStartDate().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "endDate")
    public String getEndDate() {
        return cruise.getEndDate().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @XmlElementWrapper(namespace = "http://www.eurofleets.eu/", name = "programs")
    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "program")
    public List<ProgramBean> getPrograms() {
        List<ProgramBean> r = new ArrayList<>();
        for (IProgram program : cruise.getPrograms()) {
            ProgramBean pb = new ProgramBean(program);
            r.add(pb);
        }
        return r;
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "chiefScientist")
    public String getChiefScientist() {
        StringJoiner sj = new StringJoiner(",");
        for (IPerson cs : cruise.getChiefScientists()) {
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

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "platformCode")
    public String getPlatformCode() {
        return this.cruise.getPlatform().getTerm().getUrn();
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "platformClass")
    public String getPlatformClass() {
        return this.cruise.getPlatform().getPlatformClass().getUrn();
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "objectives")
    public String getObjectives() {
        return cruise.getObjectives();
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "collateCenter")
    public String getCollateCenter() {
        if (this.cruise.getCollateCentre() != null && this.cruise.getCollateCentre().getTerm() != null) {
            return cruise.getCollateCentre().getTerm().getUrn();
        } else {
            return null;
        }
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "startingHarbor")
    public String getStartingHarbor() {
        if (this.cruise.getDepartureHarbour() != null && this.cruise.getDepartureHarbour().getTerm() != null) {
            return cruise.getDepartureHarbour().getTerm().getUrn();
        } else {
            return null;
        }
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "arrivalHarbor")
    public String getArrivalHarbor() {
        if (this.cruise.getArrivalHarbour() != null && this.cruise.getArrivalHarbour().getTerm() != null) {
            return cruise.getArrivalHarbour().getTerm().getUrn();
        } else {
            return null;
        }
    }

    @XmlElementWrapper(namespace = "http://www.eurofleets.eu/", name = "seaAreas")
    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "seaArea")
    public Set<SeaAreaBean> getSeaAreas() {
        Set<SeaAreaBean> res = new HashSet<>();
        for (ILinkedDataTerm seaAreaTerm : cruise.getSeaAreaTerms()) {
            SeaAreaBean sb = new SeaAreaBean(seaAreaTerm.getName(), seaAreaTerm.getUrn());
            res.add(sb);
        }
        return res;
    }

    @Override
    public String toString() {
        return this.cruise.toString();
    }

    @Override
    public int compareTo(CruiseBean other) {
        return this.getIdentifier().compareTo(other.getIdentifier());
    }

    @Override
    public boolean equals(Object o
    ) {
        if (o instanceof CruiseBean) {
            CruiseBean other = (CruiseBean) o;
            return this.getIdentifier().equals(other.getIdentifier());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.getIdentifier());
        return hash;
    }
}
