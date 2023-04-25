/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.eurofleets.ears3.utilities.InstantAdapter;
import eu.eurofleets.ears3.utilities.OffsetDateTimeAdapter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Thomas Vandenberghe
 */
@MappedSuperclass
public abstract class Acquisition {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private OffsetDateTime timeStamp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private OffsetDateTime instrumentTime;

    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    public OffsetDateTime getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    public OffsetDateTime getInstrumentTime() {
        return this.instrumentTime;
    }

    public void setInstrumentTime(OffsetDateTime instrumentTime) {
        this.instrumentTime = instrumentTime;
    }

    @XmlTransient
    @JsonIgnore
    public OffsetDateTime getTime() {
        return this.instrumentTime != null ? this.instrumentTime : this.timeStamp;
    }

    public String toDatagramTime() {
        OffsetDateTime datetime = getTime();
        return DateTimeFormatter.ofPattern("yyMMdd").format(datetime) + "," + DateTimeFormatter.ofPattern("HHmmss").withZone(ZoneId.of("UTC")).format(datetime);
    }
}
