package eu.eurofleets.ears3.dto;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.ISeaArea;
import com.fasterxml.jackson.annotation.JsonFormat;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.utilities.OffsetDateTimeAdapter;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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
    public List<PersonDTO> chiefScientists = new ArrayList<>();
    public List<String> seaAreas = new ArrayList<>();
    public Collection<String> programs = new ArrayList<>();
    public String platform;
    public String objectives;
    public String purpose;
    public boolean isCancelled;
    public List<String> P02 = new ArrayList<>();
    public String name;
    public String finalReportUrl;
    public String planningUrl;
    public String trackImageUrl;
    public String dataUrl;
    public String trackGmlUrl;

    public CruiseDTO(Cruise cruise) {
        this.identifier = cruise.getIdentifier();
        this.startDate = cruise.getStartDate();
        this.endDate = cruise.getEndDate();
        this.collateCentre = cruise.getCollateCentre().getTerm().getIdentifier();
        this.departureHarbour = cruise.getDepartureHarbour().getTerm().getIdentifier();
        this.arrivalHarbour = cruise.getArrivalHarbour().getTerm().getIdentifier();

        for (IPerson cs : cruise.getChiefScientists()) {
            PersonDTO ciDTO = new PersonDTO(cs.getFirstName(), cs.getLastName(), cs.getOrganisation() != null ? cs.getOrganisation().getTerm().getIdentifier() : null, cs._getPhoneNumber(), cs._getFaxNumber(), cs.getEmail());
            this.chiefScientists.add(ciDTO);

        }
        for (ISeaArea seaArea : cruise.getSeaAreas()) {
            this.seaAreas.add(seaArea.getTerm().getIdentifier());
        }
        for (IProgram program : cruise.getPrograms()) {
            this.programs.add(program.getIdentifier());
        }
        this.platform = cruise.getPlatform().getTerm().getIdentifier();
        this.objectives = cruise.getObjectives();
        this.purpose = cruise.getPurpose();
        this.isCancelled = cruise.getIsCancelled();

        for (ILinkedDataTerm iLinkedDataTerm : cruise.getP02()) {
            this.P02.add(iLinkedDataTerm.getIdentifier());
        }
        this.name = cruise.getName();
        this.finalReportUrl = cruise.getFinalReportUrl();
        this.planningUrl = cruise.getPlanningUrl();
        this.trackImageUrl = cruise.getTrackImageUrl();
        this.dataUrl = cruise.getDataUrl();
        this.trackGmlUrl = cruise.getTrackGmlUrl();
    }

    public CruiseDTO() {
    }

}
