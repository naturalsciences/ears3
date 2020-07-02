package eu.eurofleets.ears3.dto;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thomas
 */
public class CruiseDTO {

    public String identifier; 
    public OffsetDateTime startDate;
    public OffsetDateTime endDate;
    public String collateCentre; 
    public String departureHarbour; 
    public String arrivalHarbour; 
    public List<PersonDTO> chiefScientists;
    public List<String> seaAreas;
    public Collection<Integer> programs;
    public String platform;
    public String objectives;
    public boolean isCancelled; 
    public List<String> P02;
    public String name; 
    
    

}
