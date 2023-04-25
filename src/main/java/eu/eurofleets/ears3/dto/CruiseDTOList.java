/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import eu.eurofleets.ears3.domain.Cruise;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Thomas Vandenberghe
 */
@XmlRootElement(name = "cruises")
@XmlAccessorType(XmlAccessType.FIELD)
public class CruiseDTOList {

    @XmlElement(name = "cruise")
    private List<CruiseDTO> cruises;

    public CruiseDTOList() {
    }

    public CruiseDTOList(List<CruiseDTO> cruises) {
        this.cruises = cruises;
    }

    public CruiseDTOList(Collection<Cruise> cruises) {
        if (this.cruises == null) {
            this.cruises = new ArrayList<>();
        }
        for (Cruise cruise : cruises) {
            this.cruises.add(new CruiseDTO(cruise));
        }
    }

    public List<CruiseDTO> getCruises() {
        if (this.cruises == null) {
            this.cruises = new ArrayList<>();
        }
        return this.cruises;
    }

    public void setCruises(List<CruiseDTO> events) {
        this.cruises = events;
    }
}
