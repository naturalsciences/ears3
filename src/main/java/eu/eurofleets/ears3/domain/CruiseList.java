package eu.eurofleets.ears3.domain;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cruises")
@XmlAccessorType(XmlAccessType.FIELD)
public class CruiseList {

    public CruiseList() {
    }

    public CruiseList(Collection<Cruise> cruises) {
        this.cruises = cruises;
    }

    @XmlElement(name = "cruise")
    private Collection<Cruise> cruises = null;

    public Collection<Cruise> getCruises() {
        return this.cruises;
    }

    public void setCruises(Collection<Cruise> cruises) {
        this.cruises = cruises;
    }
}
