package eu.eurofleets.ears3.controller.ears2;

import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.ears2.CruiseBean;
import eu.eurofleets.ears3.domain.ears2.CruiseBeanList;
import eu.eurofleets.ears3.service.CruiseService;
//import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/ears2")
@CrossOrigin(origins = "*", maxAge = 3600)
public class Ears2CruiseController {

    @Autowired
    private CruiseService cruiseService;

    //@Operation(hidden = true, summary = "Get all cruises in EARS2 formatted xml.")
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

        //@Operation(hidden = true, summary = "Get a cruise by identifier in EARS2 formatted xml.")
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
