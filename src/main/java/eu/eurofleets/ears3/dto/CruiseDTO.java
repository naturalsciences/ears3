package eu.eurofleets.ears3.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import eu.eurofleets.ears3.utilities.OffsetDateTimeAdapter;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thomas
 */
@XmlRootElement(name = "cruise")
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class CruiseDTO {

    public String identifier;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    public OffsetDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX")
    @XmlJavaTypeAdapter(value = OffsetDateTimeAdapter.class)
    public OffsetDateTime endDate;
    public String collateCentre;
    public String departureHarbour;
    public String arrivalHarbour;
    public List<PersonDTO> chiefScientists;
    public List<String> seaAreas;
    public Collection<String> programs;
    public String platform;
    public String objectives;
    public String purpose;
    public boolean isCancelled;
    public List<String> P02;
    public String name;
    public String finalReportUrl;
    public String planningUrl;
    public String trackImageUrl;
    public String dataUrl;
    public String trackGmlUrl;

}
