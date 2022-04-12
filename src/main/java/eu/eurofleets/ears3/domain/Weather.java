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
        name = "findWeatherByApproximateDate",
        query = "SELECT t.* FROM weather t ORDER BY abs(extract('epoch' from time_stamp-?) ) LIMIT 1",
        resultClass = Weather.class
)
public class Weather extends Acquisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    @JsonIgnore
    private Long id;
    @DatagramOrder(3)
    private Double windSpeedAverage;
    @DatagramOrder(4)
    private Double windSpeedInstantaneous;
    @DatagramOrder(5)
    private Double windDirection;
    @DatagramOrder(6)
    private Double atmosphericTemperature;
    @DatagramOrder(7)
    private Double humidity;
    @DatagramOrder(8)
    private Double solarRadiation;
    @DatagramOrder(9)
    private Double atmosphericPressure;
    @DatagramOrder(10)
    private Double waterTemperature;

    public Double getWindSpeedAverage() {
        return this.windSpeedAverage;
    }

    public void setWindSpeedAverage(Double windSpeedAverage) {
        this.windSpeedAverage = windSpeedAverage;
    }

    public Double getWindSpeedInstantaneous() {
        return this.windSpeedInstantaneous;
    }

    public void setWindSpeedInstantaneous(Double windSpeedInstantaneous) {
        this.windSpeedInstantaneous = windSpeedInstantaneous;
    }

    public Double getWindDirection() {
        return this.windDirection;
    }

    public void setWindDirection(Double windDirection) {
        this.windDirection = windDirection;
    }

    public Double getAtmosphericTemperature() {
        return this.atmosphericTemperature;
    }

    public void setAtmosphericTemperature(Double atmosphericTemperature) {
        this.atmosphericTemperature = atmosphericTemperature;
    }

    public Double getHumidity() {
        return this.humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getSolarRadiation() {
        return this.solarRadiation;
    }

    public void setSolarRadiation(Double solarRadiation) {
        this.solarRadiation = solarRadiation;
    }

    public Double getAtmosphericPressure() {
        return this.atmosphericPressure;
    }

    public void setAtmosphericPressure(Double atmosphericPressure) {
        this.atmosphericPressure = atmosphericPressure;
    }

    public Double getWaterTemperature() {
        return this.waterTemperature;
    }

    public void setWaterTemperature(Double waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("$EF");
        sb.append("MET");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(toDatagramTime());
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(windSpeedAverage != null ? windSpeedAverage : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(windSpeedInstantaneous != null ? windSpeedInstantaneous : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(windDirection != null ? windDirection : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(atmosphericTemperature != null ? atmosphericTemperature : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(humidity != null ? humidity : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(solarRadiation != null ? solarRadiation : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(atmosphericPressure != null ? atmosphericPressure : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(waterTemperature != null ? waterTemperature : "");
        return sb.toString();

    }
}
