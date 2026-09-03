package eu.eurofleets.ears3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static eu.eurofleets.ears3.domain.Navigation.DATAGRAM_SEPARATOR;
import eu.eurofleets.ears3.utilities.DatagramOrder;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.time.Instant;

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
    private Instant windSpeedAverageTimestamp;
    @DatagramOrder(5)
    private String windSpeedAverageInstrId;
    @DatagramOrder(6)
    private Double windSpeedInstantaneous;
    @DatagramOrder(7)
    private Instant windSpeedInstantaneousTimestamp;
    @DatagramOrder(8)
    private String windSpeedInstantaneousInstrId;
    @DatagramOrder(9)
    private Double windDirection;
    @DatagramOrder(10)
    private Instant windDirectionTimestamp;
    @DatagramOrder(11)
    private String windDirectionInstrId;
    @DatagramOrder(12)
    private Double atmosphericTemperature;
    @DatagramOrder(13)
    private Instant atmosphericTemperatureTimestamp;
    @DatagramOrder(14)
    private String atmosphericTemperatureInstrId;
    @DatagramOrder(15)
    private Double humidity;
    @DatagramOrder(16)
    private Instant humidityTimestamp;
    @DatagramOrder(17)
    private String humidityInstrId;
    @DatagramOrder(18)
    private Double solarRadiation;
    @DatagramOrder(19)
    private Instant solarRadiationTimestamp;
    @DatagramOrder(20)
    private String solarRadiationInstrId;
    @DatagramOrder(21)
    private Double atmosphericPressure;
    @DatagramOrder(22)
    private Instant atmosphericPressureTimestamp;
    @DatagramOrder(23)
    private String atmosphericPressureInstrId;
    @DatagramOrder(24)
    private Double waterTemperature;
    @DatagramOrder(25)
    private Instant waterTemperatureTimestamp;
    @DatagramOrder(26)
    private String waterTemperatureInstrId;

    public Double getWindSpeedAverage() {
        return this.windSpeedAverage;
    }

    public void setWindSpeedAverage(Double windSpeedAverage) {
        this.windSpeedAverage = windSpeedAverage;
    }

    public Instant getWindSpeedAverageTimestamp() {
        return this.windSpeedAverageTimestamp;
    }

    public void setWindSpeedAverageTimestamp(Instant windSpeedAverageTimestamp) {
        this.windSpeedAverageTimestamp = windSpeedAverageTimestamp;
    }

    public String getWindSpeedAverageInstrId() {
        return this.windSpeedAverageInstrId;
    }

    public void setWindSpeedAverageInstrId(String windSpeedAverageInstrId) {
        this.windSpeedAverageInstrId = windSpeedAverageInstrId;
    }

    public Double getWindSpeedInstantaneous() {
        return this.windSpeedInstantaneous;
    }

    public void setWindSpeedInstantaneous(Double windSpeedInstantaneous) {
        this.windSpeedInstantaneous = windSpeedInstantaneous;
    }

    public Instant getWindSpeedInstantaneousTimestamp() {
        return this.windSpeedInstantaneousTimestamp;
    }

    public void setWindSpeedInstantaneousTimestamp(Instant windSpeedInstantaneousTimestamp) {
        this.windSpeedInstantaneousTimestamp = windSpeedInstantaneousTimestamp;
    }

    public String getWindSpeedInstantaneousInstrId() {
        return this.windSpeedInstantaneousInstrId;
    }

    public void setWindSpeedInstantaneousInstrId(String windSpeedInstantaneousInstrId) {
        this.windSpeedInstantaneousInstrId = windSpeedInstantaneousInstrId;
    }

    public Double getWindDirection() {
        return this.windDirection;
    }

    public void setWindDirection(Double windDirection) {
        this.windDirection = windDirection;
    }

    public Instant getWindDirectionTimestamp() {
        return this.windDirectionTimestamp;
    }

    public void setWindDirectionTimestamp(Instant windDirectionTimestamp) {
        this.windDirectionTimestamp = windDirectionTimestamp;
    }

    public String getWindDirectionInstrId() {
        return this.windDirectionInstrId;
    }

    public void setWindDirectionInstrId(String windDirectionInstrId) {
        this.windDirectionInstrId = windDirectionInstrId;
    }

    public Double getAtmosphericTemperature() {
        return this.atmosphericTemperature;
    }

    public void setAtmosphericTemperature(Double atmosphericTemperature) {
        this.atmosphericTemperature = atmosphericTemperature;
    }

    public Instant getAtmosphericTemperatureTimestamp() {
        return this.atmosphericTemperatureTimestamp;
    }

    public void setAtmosphericTemperatureTimestamp(Instant atmosphericTemperatureTimestamp) {
        this.atmosphericTemperatureTimestamp = atmosphericTemperatureTimestamp;
    }

    public String getAtmosphericTemperatureInstrId() {
        return this.atmosphericTemperatureInstrId;
    }

    public void setAtmosphericTemperatureInstrId(String atmosphericTemperatureInstrId) {
        this.atmosphericTemperatureInstrId = atmosphericTemperatureInstrId;
    }

    public Double getHumidity() {
        return this.humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Instant getHumidityTimestamp() {
        return this.humidityTimestamp;
    }

    public void setHumidityTimestamp(Instant humidityTimestamp) {
        this.humidityTimestamp = humidityTimestamp;
    }

    public String getHumidityInstrId() {
        return this.humidityInstrId;
    }

    public void setHumidityInstrId(String humidityInstrId) {
        this.humidityInstrId = humidityInstrId;
    }

    public Double getSolarRadiation() {
        return this.solarRadiation;
    }

    public void setSolarRadiation(Double solarRadiation) {
        this.solarRadiation = solarRadiation;
    }

    public Instant getSolarRadiationTimestamp() {
        return this.solarRadiationTimestamp;
    }

    public void setSolarRadiationTimestamp(Instant solarRadiationTimestamp) {
        this.solarRadiationTimestamp = solarRadiationTimestamp;
    }

    public String getSolarRadiationInstrId() {
        return this.solarRadiationInstrId;
    }

    public void setSolarRadiationInstrId(String solarRadiationInstrId) {
        this.solarRadiationInstrId = solarRadiationInstrId;
    }

    public Double getAtmosphericPressure() {
        return this.atmosphericPressure;
    }

    public void setAtmosphericPressure(Double atmosphericPressure) {
        this.atmosphericPressure = atmosphericPressure;
    }

    public Instant getAtmosphericPressureTimestamp() {
        return this.atmosphericPressureTimestamp;
    }

    public void setAtmosphericPressureTimestamp(Instant atmosphericPressureTimestamp) {
        this.atmosphericPressureTimestamp = atmosphericPressureTimestamp;
    }

    public String getAtmosphericPressureInstrId() {
        return this.atmosphericPressureInstrId;
    }

    public void setAtmosphericPressureInstrId(String atmosphericPressureInstrId) {
        this.atmosphericPressureInstrId = atmosphericPressureInstrId;
    }

    public Double getWaterTemperature() {
        return this.waterTemperature;
    }

    public void setWaterTemperature(Double waterTemperature) {
        this.waterTemperature = waterTemperature;
    }

    public Instant getWaterTemperatureTimestamp() {
        return this.waterTemperatureTimestamp;
    }

    public void setWaterTemperatureTimestamp(Instant waterTemperatureTimestamp) {
        this.waterTemperatureTimestamp = waterTemperatureTimestamp;
    }

    public String getWaterTemperatureInstrId() {
        return this.waterTemperatureInstrId;
    }

    public void setWaterTemperatureInstrId(String waterTemperatureInstrId) {
        this.waterTemperatureInstrId = waterTemperatureInstrId;
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
        sb.append(windSpeedAverageTimestamp != null ? windSpeedAverageTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(windSpeedAverageInstrId != null ? windSpeedAverageInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(windSpeedInstantaneous != null ? windSpeedInstantaneous : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(windSpeedInstantaneousTimestamp != null ? windSpeedInstantaneousTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(windSpeedInstantaneousInstrId != null ? windSpeedInstantaneousInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(windDirection != null ? windDirection : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(windDirectionTimestamp != null ? windDirectionTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(windDirectionInstrId != null ? windDirectionInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(atmosphericTemperature != null ? atmosphericTemperature : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(atmosphericTemperatureTimestamp != null ? atmosphericTemperatureTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(atmosphericTemperatureInstrId != null ? atmosphericTemperatureInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(humidity != null ? humidity : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(humidityTimestamp != null ? humidityTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(humidityInstrId != null ? humidityInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(solarRadiation != null ? solarRadiation : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(solarRadiationTimestamp != null ? solarRadiationTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(solarRadiationInstrId != null ? solarRadiationInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(atmosphericPressure != null ? atmosphericPressure : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(atmosphericPressureTimestamp != null ? atmosphericPressureTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(atmosphericPressureInstrId != null ? atmosphericPressureInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(waterTemperature != null ? waterTemperature : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(waterTemperatureTimestamp != null ? waterTemperatureTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(waterTemperatureInstrId != null ? waterTemperatureInstrId : "");
        return sb.toString();

    }
}