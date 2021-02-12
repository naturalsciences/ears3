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
import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://www.eurofleets.eu/", name = "seaArea")
public class SeaAreaBean implements Serializable, Comparable<SeaAreaBean> {

    private String name;
    private String urn;

    public SeaAreaBean() {
    }

    public SeaAreaBean(String name, String urn) {
        this.name = name;
        this.urn = urn;
    }

    @XmlAttribute(name = "id")
    public String getId() {
        return urn;
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "URN")
    public String getUrn() {
        return urn;
    }

    @Override
    public int compareTo(SeaAreaBean other) {
        return this.getUrn().compareTo(other.getUrn());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SeaAreaBean) {
            SeaAreaBean other = (SeaAreaBean) o;
            return this.getUrn().equals(other.getUrn());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.getUrn());
        return hash;
    }
}
/*
 <ewsl:cruise code="B">
 <ewsl:code>402</ewsl:code>
 <ewsl:cruiseName>B</ewsl:cruiseName>
 <ewsl:startDate>2016-04-07T00:00:00+02:00</ewsl:startDate>
 <ewsl:endDate>2016-04-15T00:00:00+02:00</ewsl:endDate>
 <ewsl:chiefScientist>v</ewsl:chiefScientist>
 <ewsl:chiefScientistOrganisation>SDN:EDMO::1988</ewsl:chiefScientistOrganisation>
 <ewsl:platformCode>SDN:C17::09AD</ewsl:platformCode>
 <ewsl:platformClass>SDN:L06::54</ewsl:platformClass>
 <ewsl:objectives>b</ewsl:objectives>
 <ewsl:collateCenter>SDN:EDMO::1994</ewsl:collateCenter>
 <ewsl:startingHarbor>SDN:C38::BSH3376</ewsl:startingHarbor>
 <ewsl:arrivalHarbor>SDN:C38::BSH3484</ewsl:arrivalHarbor>

 <ewsl:seaAreas>

 <ewsl:seaArea code="SDN:C19::3">
 <ewsl:URN>sillyCode:SDN:C19::3</ewsl:URN>
 </ewsl:seaArea>

 <ewsl:seaArea code="SDN:C19::2">
 <ewsl:URN>sillyCode:SDN:C19::2</ewsl:URN>
 </ewsl:seaArea>

 </ewsl:seaAreas>

 </ewsl:cruise>

 */
