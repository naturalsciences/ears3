package eu.eurofleets.ears3.controller.ears2;

import eu.eurofleets.ears3.domain.ears2.ProgramBeanList;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.ears2.ProgramBean;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.ProgramService;
//import io.swagger.v3.oas.annotations.Operation;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/ears2")
@CrossOrigin(origins = "*", maxAge = 3600)
public class Ears2ProgramController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private ProgramService programService;
    @Autowired
    private CruiseService cruiseService;

    //@Operation(hidden = true, summary = "Get all programs in EARS2 formatted xml.")
    @RequestMapping(method = {RequestMethod.GET}, value = {"programs"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public ProgramBeanList getPrograms() {
        Set<Program> res = this.programService.findAll();
        return new ProgramBeanList(res, true);
    }

    //@Operation(hidden = true, summary = "Get some programs in EARS2 formatted xml.")
    @RequestMapping(method = {RequestMethod.GET}, value = {"programs"}, params = {"cruiseIdentifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public ProgramBeanList getProgramsByCruise(@RequestParam(required = false, defaultValue = "") String cruiseIdentifier) {
        Set<Program> res;
        if (cruiseIdentifier == null || "".equals(cruiseIdentifier)) {
            res = this.programService.findAll();
        } else {
            res = this.programService.findByCruiseIdentifier(cruiseIdentifier);
        }
        return new ProgramBeanList(res, true);
    }

    //@Operation(hidden = true, summary = "Get some programs in EARS2 formatted xml.")
    @RequestMapping(method = {RequestMethod.GET}, value = {"programs"}, params = {"vesselIdentifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public ProgramBeanList getProgramsByVessel(@RequestParam(required = false, defaultValue = "") String vesselIdentifier) {
        Set<Program> res;
        if (vesselIdentifier == null || "".equals(vesselIdentifier)) {
            res = this.programService.findAll();
        } else {
            res = this.programService.findByVesselIdentifier(vesselIdentifier);
        }
        return new ProgramBeanList(res, true);
    }

    //@Operation(hidden = true, summary = "Get some programs in EARS2 formatted xml.")
    @RequestMapping(method = {RequestMethod.GET}, value = {"programs"}, params = {"startDate", "endDate"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public ProgramBeanList getPrograms(@RequestParam(required = false, defaultValue = "") String startDate, @RequestParam(required = false, defaultValue = "") String endDate) { //@DateTimeFormat(iso = ISO.DATE_TIME)
        OffsetDateTime start = OffsetDateTime.parse(startDate);
        OffsetDateTime end = OffsetDateTime.parse(endDate);
        Set<Program> res = new HashSet<>();
        /* if (cruiseIdentifier == null || "".equals(cruiseIdentifier) && startDate == null && endDate == null) {
            res = this.programService.findAll();
        } else if (!"".equals(cruiseIdentifier)) {
            res = this.programService.findByCruiseIdentifier(cruiseIdentifier);
        } else*/ if (start == null && endDate == null) {
            res = this.programService.findAll();
        } else if (end == null) {
            end = Instant.now().atOffset(ZoneOffset.UTC);
        }
        Set<Cruise> findByDate = cruiseService.findAllBetweenDate(start, end, null);
        for (Cruise cruise : findByDate) {
            res.addAll(this.programService.findByCruiseIdentifier(cruise.getIdentifier()));
        }

        return new ProgramBeanList(res, true);
    }

    //@Operation(hidden = true, summary = "Get a program by identifier in EARS2 formatted xml.")
    @RequestMapping(method = {RequestMethod.GET}, value = {"program/{id}"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public ProgramBean getProgramById(@PathVariable(value = "id") String id) {
        Program program = this.programService.findById(Long.parseLong(id));
        if (program != null) {
            return new ProgramBean(program);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no program with id " + id);
        }
    }

    //@Operation(hidden = true, summary = "Get a program by id in EARS2 formatted xml.")
    @RequestMapping(method = {RequestMethod.GET}, value = {"program"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public ProgramBean getProgramByidentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        Program program = this.programService.findByIdentifier(identifier);
        if (program != null) {
            return new ProgramBean(program);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no program with identifier " + identifier);
        }
    }
}
