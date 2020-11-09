package eu.eurofleets.ears3.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import eu.eurofleets.ears3.utilities.DatagramOrder;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Navigation extends Acquisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    private Long id;
    @DatagramOrder(3)
    private Double lon;
    @DatagramOrder(4)
    private Double lat;
    @DatagramOrder(5)
    private Double heading;
    @DatagramOrder(6)
    private Double sow;
    @DatagramOrder(7)
    private Double depth;
    @DatagramOrder(8)
    private Double cog;
    @DatagramOrder(9)
    private Double sog;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLon() {
        return this.lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getDepth() {
        if (this.depth != null && this.depth < 0) {
            return this.depth * -1.0D;
        } else {
            return this.depth;
        }
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getCog() {
        return this.cog;
    }

    public void setCog(Double cog) {
        this.cog = cog;
    }

    public Double getSog() {
        return this.sog;
    }

    public void setSog(Double sog) {
        this.sog = sog;
    }

    public Double getHeading() {
        return this.heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public Double getSow() {
        return this.sow;
    }

    public void setSow(Double sow) {
        this.sow = sow;
    }
}
