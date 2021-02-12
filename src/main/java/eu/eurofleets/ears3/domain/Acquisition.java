/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import eu.eurofleets.ears3.utilities.InstantAdapter;
import java.time.Instant;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author thomas
 */
@MappedSuperclass
public abstract class Acquisition {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private Instant timeStamp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private Instant instrumentTime;

    @XmlJavaTypeAdapter(value = InstantAdapter.class)
    public Instant getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    @XmlJavaTypeAdapter(value = InstantAdapter.class)
    public Instant getInstrumentTime() {
        return this.instrumentTime;
    }

    public void setInstrumentTime(Instant instrumentTime) {
        this.instrumentTime = instrumentTime;
    }
}
