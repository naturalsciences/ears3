package eu.eurofleets.ears3.domain;

import eu.eurofleets.ears3.utilities.DatagramOrder;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Thermosal extends Acquisition{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    private Long id;
    @DatagramOrder(3)
    private Double temperature;
    @DatagramOrder(4)
    private Double salinity;
    @DatagramOrder(5)
    private Double sigmat;
    @DatagramOrder(6)
    private Double conductivity;
    @DatagramOrder(7)
    private Double fluor;

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

    public Double getFluor() {
        return this.fluor;
    }

    public void setFluor(Double fluor) {
        this.fluor = fluor;
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
}
