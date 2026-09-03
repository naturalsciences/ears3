package eu.eurofleets.ears3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import eu.eurofleets.ears3.utilities.DatagramOrder;
import java.time.Instant;
import java.util.Collection;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedNativeQuery;
import jakarta.xml.bind.annotation.XmlTransient;

@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
@NamedNativeQuery(
        name = "findNavigationByApproximateDate",
        query = "SELECT t.* FROM navigation t ORDER BY abs(extract('epoch' from time_stamp-?) ) LIMIT 1",
        resultClass = Navigation.class
)
public class Navigation extends Acquisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlTransient
    @JsonIgnore
    private Long id;
    @DatagramOrder(1)
    private Double lon;
    @DatagramOrder(2)
    private Instant lonTimestamp;
    @DatagramOrder(3)
    private String lonInstrId;
    @DatagramOrder(4)
    private Double lat;
    @DatagramOrder(5)
    private Instant latTimestamp;
    @DatagramOrder(6)
    private String latInstrId;
    @DatagramOrder(7)
    private Double heading;
    @DatagramOrder(8)
    private Instant headingTimestamp;
    @DatagramOrder(9)
    private String headingInstrId;
    @DatagramOrder(10)
    private Double sow;
    @DatagramOrder(11)
    private Instant sowTimestamp;
    @DatagramOrder(12)
    private String sowInstrId;
    @DatagramOrder(13)
    private Double depth;
    @DatagramOrder(14)
    private Instant depthTimestamp;
    @DatagramOrder(15)
    private String depthInstrId;
    @DatagramOrder(16)
    private Double cog;
    @DatagramOrder(17)
    private Instant cogTimestamp;
    @DatagramOrder(18)
    private String cogInstrId;
    @DatagramOrder(19)
    private Double sog;
    @DatagramOrder(20)
    private Instant sogTimestamp;
    @DatagramOrder(21)
    private String sogInstrId;

    @ManyToMany(mappedBy = "navigation")
    @XmlTransient
    @JsonIgnore
    private Collection<Event> events;

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

    public Instant getLonTimestamp() {
        return this.lonTimestamp;
    }

    public void setLonTimestamp(Instant lonTimestamp) {
        this.lonTimestamp = lonTimestamp;
    }

    public String getLonInstrId() {
        return this.lonInstrId;
    }

    public void setLonInstrId(String lonInstrId) {
        this.lonInstrId = lonInstrId;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Instant getLatTimestamp() {
        return this.latTimestamp;
    }

    public void setLatTimestamp(Instant latTimestamp) {
        this.latTimestamp = latTimestamp;
    }

    public String getLatInstrId() {
        return this.latInstrId;
    }

    public void setLatInstrId(String latInstrId) {
        this.latInstrId = latInstrId;
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

    public Instant getDepthTimestamp() {
        return this.depthTimestamp;
    }

    public void setDepthTimestamp(Instant depthTimestamp) {
        this.depthTimestamp = depthTimestamp;
    }

    public String getDepthInstrId() {
        return this.depthInstrId;
    }

    public void setDepthInstrId(String depthInstrId) {
        this.depthInstrId = depthInstrId;
    }

    public Double getCog() {
        return this.cog;
    }

    public void setCog(Double cog) {
        this.cog = cog;
    }

    public Instant getCogTimestamp() {
        return this.cogTimestamp;
    }

    public void setCogTimestamp(Instant cogTimestamp) {
        this.cogTimestamp = cogTimestamp;
    }

    public String getCogInstrId() {
        return this.cogInstrId;
    }

    public void setCogInstrId(String cogInstrId) {
        this.cogInstrId = cogInstrId;
    }

    public Double getSog() {
        return this.sog;
    }

    public void setSog(Double sog) {
        this.sog = sog;
    }

    public Instant getSogTimestamp() {
        return this.sogTimestamp;
    }

    public void setSogTimestamp(Instant sogTimestamp) {
        this.sogTimestamp = sogTimestamp;
    }

    public String getSogInstrId() {
        return this.sogInstrId;
    }

    public void setSogInstrId(String sogInstrId) {
        this.sogInstrId = sogInstrId;
    }

    public Double getHeading() {
        return this.heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public Instant getHeadingTimestamp() {
        return this.headingTimestamp;
    }

    public void setHeadingTimestamp(Instant headingTimestamp) {
        this.headingTimestamp = headingTimestamp;
    }

    public String getHeadingInstrId() {
        return this.headingInstrId;
    }

    public void setHeadingInstrId(String headingInstrId) {
        this.headingInstrId = headingInstrId;
    }

    public Double getSow() {
        return this.sow;
    }

    public void setSow(Double sow) {
        this.sow = sow;
    }

    public Instant getSowTimestamp() {
        return this.sowTimestamp;
    }

    public void setSowTimestamp(Instant sowTimestamp) {
        this.sowTimestamp = sowTimestamp;
    }

    public String getSowInstrId() {
        return this.sowInstrId;
    }

    public void setSowInstrId(String sowInstrId) {
        this.sowInstrId = sowInstrId;
    }

    public Collection<Event> getEvents() {
        return events;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }

    public final static String DATAGRAM_SEPARATOR = ",";

    public String toString() {

        StringBuilder sb = new StringBuilder("$EF");
        sb.append("POS");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(toDatagramTime());
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(lon != null ? lon : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(lonTimestamp != null ? lonTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(lonInstrId != null ? lonInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(lat != null ? lat : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(latTimestamp != null ? latTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(latInstrId != null ? latInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(heading != null ? heading : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(headingTimestamp != null ? headingTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(headingInstrId != null ? headingInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(sow != null ? sow : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(sowTimestamp != null ? sowTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(sowInstrId != null ? sowInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(depth != null ? Math.abs(depth) : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(depthTimestamp != null ? depthTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(depthInstrId != null ? depthInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(cog != null ? cog : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(cogTimestamp != null ? cogTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(cogInstrId != null ? cogInstrId : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(sog != null ? sog : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(sogTimestamp != null ? sogTimestamp : "");
        sb.append(DATAGRAM_SEPARATOR);
        sb.append(sogInstrId != null ? sogInstrId : "");
        return sb.toString();

    }
}