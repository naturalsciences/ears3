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
    @DatagramOrder(1)
    private Double salinity;
    @DatagramOrder(2)
    private Instant salinityTimestamp;
    @DatagramOrder(3)
    private String salinityInstrId;
    @DatagramOrder(4)
    private Double temperature;
    @DatagramOrder(5)
    private Instant temperatureTimestamp;
    @DatagramOrder(6)
    private String temperatureInstrId;
    @DatagramOrder(7)
    private Double rawFluorometry;
    @DatagramOrder(8)
    private Instant rawFluorometryTimestamp;
    @DatagramOrder(9)
    private String rawFluorometryInstrId;
    @DatagramOrder(10)
    private Double conductivity;
    @DatagramOrder(11)
    private Instant conductivityTimestamp;
    @DatagramOrder(12)
    private String conductivityInstrId;
    @DatagramOrder(13)
    private Double sigmat;
    @DatagramOrder(14)
    private Instant sigmatTimestamp;
    @DatagramOrder(15)
    private String sigmatInstrId;

    public Double getSalinity() {
        return this.salinity;
    }

    public void setSalinity(Double salinity) {
        this.salinity = salinity;
    }

    public Instant getSalinityTimestamp() {
        return this.salinityTimestamp;
    }

    public void setSalinityTimestamp(Instant salinityTimestamp) {
        this.salinityTimestamp = salinityTimestamp;
    }

    public String getSalinityInstrId() {
        return this.salinityInstrId;
    }

    public void setSalinityInstrId(String salinityInstrId) {
        this.salinityInstrId = salinityInstrId;
    }

    public Double getTemperature() {
        return this.temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Instant getTemperatureTimestamp() {
        return this.temperatureTimestamp;
    }

    public void setTemperatureTimestamp(Instant temperatureTimestamp) {
        this.temperatureTimestamp = temperatureTimestamp;
    }

    public String getTemperatureInstrId() {
        return this.temperatureInstrId;
    }

    public void setTemperatureInstrId(String temperatureInstrId) {
        this.temperatureInstrId = temperatureInstrId;
    }

    public Double getRawFluorometry() {
        return this.rawFluorometry;
    }

    public void setRawFluorometry(Double rawFluorometry) {
        this.rawFluorometry = rawFluorometry;
    }

    public Instant getRawFluorometryTimestamp() {
        return this.rawFluorometryTimestamp;
    }

    public void setRawFluorometryTimestamp(Instant rawFluorometryTimestamp) {
        this.rawFluorometryTimestamp = rawFluorometryTimestamp;
    }

    public String getRawFluorometryInstrId() {
        return this.rawFluorometryInstrId;
    }

    public void setRawFluorometryInstrId(String rawFluorometryInstrId) {
        this.rawFluorometryInstrId = rawFluorometryInstrId;
    }

    public Double getConductivity() {
        return this.conductivity;
    }

    public void setConductivity(Double conductivity) {
        this.conductivity = conductivity;
    }

    public Instant getConductivityTimestamp() {
        return this.conductivityTimestamp;
    }

    public void setConductivityTimestamp(Instant conductivityTimestamp) {
        this.conductivityTimestamp = conductivityTimestamp;
    }

    public String getConductivityInstrId() {
        return this.conductivityInstrId;
    }

    public void setConductivityInstrId(String conductivityInstrId) {
        this.conductivityInstrId = conductivityInstrId;
    }

    public Double getSigmat() {
        return this.sigmat;
    }

    public void setSigmat(Double sigmat) {
        this.sigmat = sigmat;
    }

    public Instant getSigmatTimestamp() {
        return this.sigmatTimestamp;
    }

    public void setSigmatTimestamp(Instant sigmatTimestamp) {
        this.sigmatTimestamp = sigmatTimestamp;
    }

    public String getSigmatInstrId() {
        return this.sigmatInstrId;
    }

    public void setSigmatInstrId(String sigmatInstrId) {
        this.sigmatInstrId = sigmatInstrId;
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
        sb.append(salinityTimestamp != null ? salinityTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(salinityInstrId != null ? salinityInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(temperature != null ? temperature : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(temperatureTimestamp != null ? temperatureTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(temperatureInstrId != null ? temperatureInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(rawFluorometry != null ? rawFluorometry : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(rawFluorometryTimestamp != null ? rawFluorometryTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(rawFluorometryInstrId != null ? rawFluorometryInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(conductivity != null ? conductivity : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(conductivityTimestamp != null ? conductivityTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(conductivityInstrId != null ? conductivityInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(sigmat != null ? sigmat : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(sigmatTimestamp != null ? sigmatTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(sigmatInstrId != null ? sigmatInstrId : "");
        return sb.toString();
    }
}