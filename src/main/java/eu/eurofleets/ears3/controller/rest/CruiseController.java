package eu.eurofleets.ears3.controller.rest;

import be.naturalsciences.bmdc.cruise.comparator.CoordinateComparator;
import be.naturalsciences.bmdc.cruise.csr.CSRBuilder;
import be.naturalsciences.bmdc.cruise.csr.CSRPrinter;
import be.naturalsciences.bmdc.cruise.model.ICoordinate;
import be.naturalsciences.bmdc.cruise.model.ISeaArea;
import dto.EarsObjectList;
import dto.Navigation;
import eu.eurofleets.ears3.domain.Coordinate;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.CruiseList;
import eu.eurofleets.ears3.domain.License;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.SeaArea;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.SeaAreaService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.server.PathParam;
import javax.xml.bind.JAXBException;
import model.ProjectManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CruiseController {

    @Autowired
    private CruiseService cruiseService;

    @Autowired
    private SeaAreaService seaAreaService;

    @Autowired
    private Environment env;

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruises"}, produces = {"application/xml; charset=utf-8", "application/json; charset=utf-8"})
    public CruiseList getCruises(@RequestParam(required = false, defaultValue = "") String platformCode) {
        List<Cruise> res;
        if (platformCode == null || "".equals(platformCode)) {
            res = this.cruiseService.findAll();
        } else {
            res = this.cruiseService.findAllByPlatformCode(platformCode);
        }

        return new CruiseList(res);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise/{id}"}, produces = {"application/xml; charset=utf-8", "application/json; charset=utf-8"})
    public Cruise getCruiseById(@PathVariable(value = "id") String id) {
        System.out.println("id: " + id);
        Cruise cruise = this.cruiseService.findById(Long.parseLong(id));
        if (cruise != null) {
            return cruise;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no cruise with id " + id);
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json; charset=utf-8"})
    public Cruise getCruiseByidentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        Cruise cruise = this.cruiseService.findByIdentifier(identifier);
        if (cruise != null) {
            return cruise;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no cruise with identifier " + identifier);
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise/csr"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8"})
    public String getCSRByName(@RequestParam(required = true, value = "identifier") String identifier) throws JAXBException, IOException {
        Cruise cruise = this.cruiseService.findByIdentifier(identifier);
        if (cruise != null) {
            List<Coordinate> coordinates = new ArrayList();
            String startDate = cruise.getStartDate().withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
            String endDate = cruise.getEndDate().withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
            URL website = new URL(env.getProperty("ears.navigation.server") + "/ears2Nav/nav/getBetween/datagram?startDate=" + startDate + "&endDate=" + endDate);
            URLConnection connection = website.openConnection();

            InputStreamReader ipsr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(ipsr);
            String line;
            String headingString = null;
            double heading = 0;
            while ((line = br.readLine()) != null) {
                String lon = line.split(",")[3];
                String lat = line.split(",")[4];
                if (lat != null && !lat.isEmpty() && lon != null && !lon.isEmpty()) {
                    String[] split = line.split(",");
                    if (split.length >= 6) {
                        headingString = split[5];
                        if (Math.abs(Double.valueOf(headingString) - heading) > 0.5) {
                            heading = Double.valueOf(headingString);
                            Coordinate c = new Coordinate();
                            c.x = Double.valueOf(lon);
                            c.y = Double.valueOf(lat);
                            coordinates.add(c);
                        }
                    }
                }
            }
            br.close();

            if (coordinates.size() > 0) {
                cruise.setTrack(coordinates);
                ICoordinate max = Collections.max(cruise.getTrack(), new CoordinateComparator());
                ICoordinate min = Collections.min(cruise.getTrack(), new CoordinateComparator());

                cruise.setSouthBoundLatitude(min.getY());
                cruise.setNorthBoundLatitude(max.getY());
                cruise.setWestBoundLongitude(min.getX());
                cruise.setEastBoundLongitude(max.getX());
            }

            List<SeaArea> allAreas = seaAreaService.findAll();
            Set<SeaArea> set = new HashSet();
            for (ISeaArea seaArea : cruise.getSeaAreas()) {
                set.add((SeaArea) seaArea);
            }
            for (SeaArea s : allAreas) {
                for (Coordinate c : coordinates) {
                    if (s.containsCoordinate(c)) {
                        set.add(s);
                    }
                }
            }
            cruise.setSeaAreas(set);

            String licenseString = env.getProperty("ears.csr.license");
            License license = License.Licenses.valueOf(licenseString).license;
            CSRBuilder b = new CSRBuilder(cruise, license);
            CSRPrinter p = new CSRPrinter(b);

            return p.getResult();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no cruise with identifier " + identifier);
        }
    }

    @PostMapping(value = {"cruise"}, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Cruise> createCruise(@RequestBody CruiseDTO cruiseDTO) {
        if (cruiseDTO.platform == null) {
            String property = env.getProperty("ears.platform");
            if (property != null) {
                cruiseDTO.platform = property;
            } else {
                throw new IllegalArgumentException("No platform has been provided in the POST body and no platform has been set in the web service configuration.");
            }
        }
        return new ResponseEntity<Cruise>(this.cruiseService.save(cruiseDTO), HttpStatus.CREATED);

    }

    @DeleteMapping(value = {"cruise"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json; charset=utf-8"})
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
