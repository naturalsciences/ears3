package eu.eurofleets.ears3.controller.ears2;

import eu.eurofleets.ears3.controller.rest.*;
import be.naturalsciences.bmdc.cruise.comparator.CoordinateComparator;
import be.naturalsciences.bmdc.cruise.csr.CSRBuilder;
import be.naturalsciences.bmdc.cruise.csr.CSRPrinter;
import be.naturalsciences.bmdc.cruise.model.ICoordinate;
import be.naturalsciences.bmdc.cruise.model.ISeaArea;
import eu.eurofleets.ears3.domain.Coordinate;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.CruiseList;
import eu.eurofleets.ears3.domain.License;
import eu.eurofleets.ears3.domain.SeaArea;
import eu.eurofleets.ears3.domain.ears2.CruiseBean;
import eu.eurofleets.ears3.domain.ears2.CruiseBeanList;
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
@RequestMapping(value = "/ears2")
@CrossOrigin(origins = "*", maxAge = 3600)
public class Ears2CruiseController {

    @Autowired
    private CruiseService cruiseService;

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruises"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public CruiseBeanList getCruises(@RequestParam(required = false, defaultValue = "") String platformCode) {
        Collection<Cruise> res;
        if (platformCode == null || "".equals(platformCode)) {
            res = this.cruiseService.findAll();
        } else {
            res = this.cruiseService.findAllByPlatformIdentifier(platformCode);
        }

        return new CruiseBeanList(res, true);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"cruise"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public CruiseBean getCruiseByidentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        Cruise cruise = this.cruiseService.findByIdentifier(identifier);
        if (cruise != null) {
            return new CruiseBean(cruise);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no cruise with identifier " + identifier);
        }
    }

}
