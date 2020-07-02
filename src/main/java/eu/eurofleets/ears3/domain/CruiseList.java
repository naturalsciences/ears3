package eu.eurofleets.ears3.domain;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cruises")
@XmlAccessorType(XmlAccessType.FIELD)
public class CruiseList {

    public CruiseList() {
    }

    public CruiseList(List<Cruise> cruises) {
        this.cruises = cruises;
    }

    @XmlElement(name = "cruise")
    private List<Cruise> cruises = null;

    public List<Cruise> getCruises() {
        return this.cruises;
    }

    public void setCruises(List<Cruise> cruises) {
        this.cruises = cruises;
    }
}

