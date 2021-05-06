/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.utilities;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author thomas
 */
public class OffsetDateTimeAdapter extends XmlAdapter<String, OffsetDateTime> {

    public OffsetDateTime unmarshal(String v) throws Exception {
        return OffsetDateTime.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
    }

    public String marshal(OffsetDateTime v) throws Exception {
        return v.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"));
    }

}
