package eu.eurofleets.ears3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static eu.eurofleets.ears3.domain.Navigation.DATAGRAM_SEPARATOR;
import eu.eurofleets.ears3.utilities.DatagramOrder;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
@NamedNativeQuery(
        name = "findThermosalByApproximateDate",
        query = "SELECT t.* FROM thermosal t ORDER BY abs(extract('epoch' from time_stamp-?) ) LIMIT 1",
        resultClass = Thermosal.class
)
public class Thermosal extends Acquisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    @JsonIgnore
    private Long id;
    @DatagramOrder(3)
    private Double salinity;
    @DatagramOrder(4)
    private Double temperature;
    @DatagramOrder(5)
    private Double rawFluorometry;
    @DatagramOrder(6)
    private Double conductivity;
    @DatagramOrder(7)
    private Double sigmat;

    public Double getSalinity() {
        return this.salinity;
    }

    public void setSalinity(Double salinity) {
        this.salinity = salinity;
    }

    public Double getTemperature() {
        return this.temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getRawFluorometry() {
        return this.rawFluorometry;
    }

    public void setRawFluorometry(Double rawFluorometry) {
        this.rawFluorometry = rawFluorometry;
    }

    public Double getConductivity() {
        return this.conductivity;
    }

    public void setConductivity(Double conductivity) {
        this.conductivity = conductivity;
    }

    public Double getSigmat() {
        return this.sigmat;
    }

    public void setSigmat(Double sigmat) {
        this.sigmat = sigmat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("$EF");
        sb.append("TSS");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(toDatagramTime());
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(salinity != null ? salinity : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(temperature != null ? temperature : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(rawFluorometry != null ? rawFluorometry : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(conductivity != null ? conductivity : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(sigmat != null ? sigmat : "");
        return sb.toString();
    }
}
