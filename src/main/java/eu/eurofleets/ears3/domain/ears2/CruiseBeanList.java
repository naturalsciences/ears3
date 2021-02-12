package eu.eurofleets.ears3.domain.ears2;

import eu.eurofleets.ears3.domain.Cruise;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cruises")
@XmlAccessorType(XmlAccessType.FIELD)
public class CruiseBeanList {

    @XmlElement(name = "cruise")
    private Collection<CruiseBean> cruises = null;

    public CruiseBeanList() {

    }

    public CruiseBeanList(Collection<CruiseBean> cruises) {
        this.cruises = cruises;
    }

    public CruiseBeanList(Collection<Cruise> cruises, boolean fake) {
        List<CruiseBean> res = new ArrayList<>();
        for (Cruise c : cruises) {
            CruiseBean cruise = new CruiseBean(c);
            res.add(cruise);
        }
        this.cruises = res;
    }
}
