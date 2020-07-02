package eu.eurofleets.ears3.controller;

import be.naturalsciences.bmdc.cruise.csr.CSRBuilder;
import be.naturalsciences.bmdc.cruise.csr.CSRPrinter;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.CruiseList;
import eu.eurofleets.ears3.domain.License;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.service.CruiseService;
import java.util.List;
import javax.websocket.server.PathParam;
import javax.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CruiseController {

    @Autowired
    private CruiseService cruiseService;

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruises"}, produces = {"application/xml"})
    public CruiseList getCruises(@RequestParam(required = false, defaultValue = "") String platformCode) {
        List<Cruise> res;
        if (platformCode == null || "".equals(platformCode)) {
            res = this.cruiseService.findAll();
        } else {
            res = this.cruiseService.findAllByPlatformCode(platformCode);
        }
        return new CruiseList(res);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise"}, params = {"id"}, produces = {"application/xml"})
    public Cruise getCruiseById(@PathParam(value = "id") long id) {
        return this.cruiseService.findById(id);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise"}, params = {"identifier"}, produces = {"application/xml"})
    public Cruise getCruiseByidentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        return this.cruiseService.findByIdentifier(identifier);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise/csr"}, params = {"identifier"}, produces = {"application/xml"})
    public String getCSRByName(@RequestParam(required = true, value = "identifier") String identifier) throws JAXBException {
        Cruise cruise = this.cruiseService.findByIdentifier(identifier);
        CSRBuilder b = new CSRBuilder(cruise, new License(new LinkedDataTerm("https://creativecommons.org/publicdomain/zero/1.0/",null,"CC0")));
        CSRPrinter p = new CSRPrinter(b);
        return p.getResult();
    }

    @PostMapping(value = {"cruise"}, produces = {"application/xml"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Cruise> createCruise(@RequestBody CruiseDTO cruiseDTO) {
        //return Response.status(HttpStatus.CREATED.value()).entity(this.cruiseService.save(cruiseDTO)).build();
        //  return ResponseEntity.ok(this.cruiseService.save(cruiseDTO).getId());//<>(this.cruiseService.save(cruiseDTO), HttpStatus.CREATED);
    return new ResponseEntity<Cruise>(this.cruiseService.save(cruiseDTO), HttpStatus.CREATED);
    }
    
    @DeleteMapping(value = {"cruise"}, params = {"identifier"}, produces = {"application/xml"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String removeCruiseByIdentifier(@RequestParam(required = true) String identifier) {
        this.cruiseService.deleteByIdentifier(identifier);
        return "";
    }

    /*@RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"insertCruise"}, params = {"id", "cruiseName"}, produces = {"application/xml"})
    public Message insertCruise(@RequestParam(required = true) String id, @RequestParam(required = true) String cruiseName, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam(required = false) String chiefScientist, @RequestParam(required = false) String csorg, @RequestParam(required = false) String platformCode, @RequestParam(required = false) String platformClass, @RequestParam(required = false) String objectives, @RequestParam(required = false) String collateCenter, @RequestParam(required = false) String startingHarbor, @RequestParam(required = false) String arrivalHarbor, @RequestParam(required = false) String seaAreas)
            throws ParseException {
        Cruise cruise = new Cruise();
        cruise.setId(id);
        cruise.setName(cruiseName);
        if (startDate != null) {
            cruise.setStartDate(DateUtilities.parseDate(startDate));
        }
        if (endDate != null) {
            cruise.setEndDate(DateUtilities.parseDate(endDate));
        }
        cruise.setChiefScientist(chiefScientist);
        cruise.setChiefScientistOrganisation(csorg);
        cruise.setPlatformCode(platformCode);
        cruise.setPlatformClass(platformClass);
        cruise.setObjectives(objectives);
        cruise.setCollateCenter(collateCenter);
        cruise.setStartingHarbor(startingHarbor);
        cruise.setArrivalHarbor(arrivalHarbor);

        if (seaAreas != null) {
            String[] seaAreasAsArray = seaAreas.split("\\,");
            Set<SeaAreaO> seaAreasSet = new HashSet();

            for (String str : seaAreasAsArray) {
                SeaAreaO seaArea = null;
                try {
                    seaArea = this.cruiseService.getSeaArea(str);
                } catch (NumberFormatException e) {
                }
                if (seaArea == null) {
                    seaArea = new SeaAreaO(str, "urn:" + str);
                }
                seaAreasSet.add(seaArea);
            }
            cruise.setSeaAreas(seaAreasSet);
        }

        this.cruiseService.setCruise(cruise);

        return new Message(cruise.getCruiseId(), "Cruise inserted");
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"insertCruise"}, params = {"newId", "originId"}, produces = {"application/xml"})
    public Message insertCruiseByModifyingId(@RequestParam(required = true) String newId, @RequestParam(required = true) String originId) {
        Cruise cruise = this.cruiseService.getCruiseById(originId);
        Message res = new Message("-1", "Cruise not inserted");
        if (cruise == null) {
            res = new Message("-1", "Cruise " + originId + " does not exist");
        } else {
            try {
              //  this.cruiseService.setCruise(newId, cruise);
                res = new Message(newId, "Cruise inserted Succesfully");
            } catch (DuplicateIdException e) {
                res = new Message("-1", "Cruise " + newId + " exists in the DataBase");
            } catch (Exception e) {
                res = new Message("-1", "Cruise " + newId + " Some errores occurred coping the Cruise. The Cruise has not been copied to DDBB ");
            }
        }

        return res;
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"modifyCruise"}, params = {"id"}, produces = {"application/xml"})
    public Message modifyCruise(@RequestParam(required = true) String id, @RequestParam(required = false) String cruiseName, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam(required = false) String chiefScientist, @RequestParam(required = false) String csorg, @RequestParam(required = false) String platformCode, @RequestParam(required = false) String platformClass, @RequestParam(required = false) String objectives, @RequestParam(required = false) String collateCenter, @RequestParam(required = false) String startingHarbor, @RequestParam(required = false) String arrivalHarbor, @RequestParam(required = false) String seaAreas)
            throws ParseException {
        Cruise cruise = this.cruiseService.getCruiseById(id);
        if (cruiseName != null) {
            cruise.setName(cruiseName);
        }
        if (startDate != null) {
            cruise.setStartDate(DateUtilities.parseDate(startDate));
        }
        if (endDate != null) {
            cruise.setEndDate(DateUtilities.parseDate(endDate));
        }
        if (chiefScientist != null) {
            cruise.setChiefScientist(chiefScientist);
        }
        if (csorg != null) {
            cruise.setChiefScientistOrganisation(csorg);
        }
        if (platformCode != null) {
            cruise.setPlatformCode(platformCode);
        }
        if (platformClass != null) {
            cruise.setPlatformClass(platformClass);
        }
        if (objectives != null) {
            cruise.setObjectives(objectives);
        }
        if (collateCenter != null) {
            cruise.setCollateCenter(collateCenter);
        }
        if (startingHarbor != null) {
            cruise.setStartingHarbor(startingHarbor);
        }
        if (arrivalHarbor != null) {
            cruise.setArrivalHarbor(arrivalHarbor);
        }
        if (seaAreas != null) {
            String[] seaAreasAsArray = seaAreas.split("\\,");
            Set<SeaAreaO> seaAreasSet = new HashSet();

            for (String str : seaAreasAsArray) {
                SeaAreaO seaArea = null;
                try {
                    seaArea = this.cruiseService.getSeaArea(str);
                } catch (NumberFormatException e) {
                }
                if (seaArea == null) {
                    seaArea = new SeaAreaO(str, "urn:" + str);
                }
                seaAreasSet.add(seaArea);
            }
            cruise.setSeaAreas(seaAreasSet);
        }

        this.cruiseService.setCruise(cruise);

        return new Message(cruise.getCruiseId(), "Cruise modified");
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"removeCruise"}, params = {"startDate", "endDate"}, produces = {"application/xml"})
    public Message removeCruiseByDateRange(@RequestParam(required = true, value = "startDate") String startDate, @RequestParam(required = true, value = "endDate") String endDate)
            throws ParseException {
        this.cruiseService.removeCruise(DateUtilities.parseDate(startDate), DateUtilities.parseDate(endDate));

        Message res = new Message("0", "Cruises deleted");
        return res;
    }*/
}