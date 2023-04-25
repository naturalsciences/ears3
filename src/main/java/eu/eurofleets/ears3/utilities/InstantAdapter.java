/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.utilities;

import java.time.Instant;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Thomas Vandenberghe
 */
public class InstantAdapter  extends XmlAdapter<String, Instant> {

    public Instant unmarshal(String v) throws Exception {
        return Instant.parse(v);
    }

    public String marshal(Instant v) throws Exception {
        return v.toString();
    }
    
}
