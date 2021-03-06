package eu.eurofleets.ears3.controller.rest;

import be.naturalsciences.bmdc.cruise.comparator.CoordinateComparator;
import be.naturalsciences.bmdc.cruise.csr.CSRBuilder;
import be.naturalsciences.bmdc.cruise.csr.CSRPrinter;
import be.naturalsciences.bmdc.cruise.model.ICoordinate;
import be.naturalsciences.bmdc.cruise.model.ISeaArea;
import eu.eurofleets.ears3.domain.Coordinate;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.CruiseList;
import eu.eurofleets.ears3.domain.License;
import eu.eurofleets.ears3.domain.Message;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.SeaArea;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.SeaAreaService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBException;
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

    @RequestMapping(method = {RequestMethod.GET}, value = {"alive"}, produces = {"text/plain"})
    public String getAlive() {
        return "";
    }

    // @RequestMapping(method = {RequestMethod.GET}, value = {"cruises"}, produces = {"application/xml; charset=utf-8", "application/json"})
    private CruiseList getCruises(String platformCode) {
        Collection<Cruise> res;
        if (platformCode == null || "".equals(platformCode)) {
            res = this.cruiseService.findAll();
        } else {
            res = this.cruiseService.findAllByPlatformIdentifier(platformCode);
        }

        return new CruiseList(res);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruises"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public CruiseList getCruisesAt(@RequestParam(required = false, defaultValue = "") String platformIdentifier, @RequestParam(required = false) OffsetDateTime startDate, @RequestParam(required = false) OffsetDateTime endDate, @RequestParam(required = false) OffsetDateTime atDate) {
        if (startDate == null && endDate == null && atDate == null) {
            return getCruises(platformIdentifier);
        }
        if (startDate != null && endDate == null) {
            endDate = Instant.now().atOffset(ZoneOffset.UTC);
        }
        if (startDate == null && endDate != null) {
            startDate = OffsetDateTime.parse("1900-01-01", DateTimeFormatter.ISO_DATE);
        }

        Set<Cruise> res;
        if (atDate == null) {
            res = this.cruiseService.findAllBetweenDate(startDate, endDate, platformIdentifier);
        } else {
            res = this.cruiseService.findAtDate(atDate, platformIdentifier);
        }

        return new CruiseList(res);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise/{id}"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public Cruise getCruiseById(@PathVariable(value = "id") String id) {
        System.out.println("id: " + id);
        Cruise cruise = this.cruiseService.findById(Long.parseLong(id));
        if (cruise != null) {
            return cruise;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no cruise with id " + id);
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public Cruise getCruiseByidentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        Cruise cruise = this.cruiseService.findByIdentifier(identifier);
        if (cruise != null) {
            return cruise;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no cruise with identifier " + identifier);
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise/current"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public CruiseList getCurrent() {
        return new CruiseList(this.cruiseService.findCurrent());
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise/csr"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8"})
    public String getCSRByName(@RequestParam(required = true, value = "identifier") String identifier) throws JAXBException, IOException {
        Cruise cruise = this.cruiseService.findByIdentifier(identifier);
        if (cruise != null) {
            List<Coordinate> coordinates = new ArrayList();
            String startDate = cruise.getStartDate().withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
            String endDate = cruise.getEndDate().withOffsetSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);

            /*DatagramUtilities<Navigation> navUtil = new DatagramUtilities<>(Navigation.class, env.getProperty("ears.navigation.server"));
            List<Navigation> between = navUtil.between(cruise.getStartDate(), cruise.getEndDate());*/
            URL website = new URL(env.getProperty("ears.navigation.server") + "/ears3Nav/nav/getBetween/datagram?startDate=" + startDate + "&endDate=" + endDate);
            URLConnection connection = website.openConnection();

            InputStreamReader ipsr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(ipsr);
            String line;
            String headingString = null;
            double heading = 0;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("$")) { //EARS datagram
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

    @PostMapping(value = {"cruise"}, produces = {"application/xml; charset=utf-8", "application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Message<CruiseDTO>> createCruise(@RequestBody CruiseDTO cruiseDTO) {
        if (cruiseDTO.platform == null) {
            String property = env.getProperty("ears.platform");
            if (property != null) {
                cruiseDTO.platform = property;
            } else {
                throw new IllegalArgumentException("No platform has been provided in the POST body and no platform has been set in the web service configuration.");
            }
        }
        Cruise cruise = this.cruiseService.save(cruiseDTO);
        return new ResponseEntity<Message<CruiseDTO>>(new Message<CruiseDTO>(HttpStatus.CREATED.value(), cruise.getIdentifier(), cruiseDTO), HttpStatus.CREATED);
    }

    @DeleteMapping(value = {"cruise"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String removeCruiseByIdentifier(@RequestParam(required = true) String identifier) {
        this.cruiseService.deleteByIdentifier(identifier);
        return "";
    }
    /*
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"removeCruise"}, params = {"startDate", "endDate"}, produces = {"application/xml"})
    public Message removeCruiseByDateRange(@RequestParam(required = true, value = "startDate") String startDate, @RequestParam(required = true, value = "endDate") String endDate)
            throws ParseException {
        this.cruiseService.removeCruise(DateUtilities.parseDate(startDate), DateUtilities.parseDate(endDate));

        Message res = new Message("0", "Cruises deleted");
        return res;
    }*/
}
