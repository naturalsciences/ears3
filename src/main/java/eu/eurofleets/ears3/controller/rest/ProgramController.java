package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Message;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.ProgramList;
import eu.eurofleets.ears3.dto.ProgramDTO;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.ProgramService;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
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
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProgramController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private ProgramService programService;
    @Autowired
    private CruiseService cruiseService;

    @RequestMapping(method = {RequestMethod.GET}, value = {"programs"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public ProgramList getPrograms() {
        Set<Program> res = this.programService.findAll();
        Set<Program> currents = this.programService.findCurrent();
        List r = new ArrayList<>(res);
        r.addAll(0, currents);
        res = new LinkedHashSet(r);
        return new ProgramList(res);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"programs"}, params = {"cruiseIdentifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public ProgramList getPrograms(@RequestParam(required = false, defaultValue = "") String cruiseIdentifier) {
        Set<Program> res;
        if (cruiseIdentifier == null || "".equals(cruiseIdentifier)) {
            res = this.programService.findAll();
        } else {
            res = this.programService.findByCruiseIdentifier(cruiseIdentifier);
        }
        Set<Program> currents = this.programService.findCurrent();
        List<Program> r = new ArrayList<>(res);
        r.addAll(0, currents);
        res = new LinkedHashSet(r);
        return new ProgramList(res);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"programs"}, params = {"startDate", "endDate"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public ProgramList getPrograms(@RequestParam(required = false, defaultValue = "") String startDate, @RequestParam(required = false, defaultValue = "") String endDate) { //@DateTimeFormat(iso = ISO.DATE_TIME)
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

        return new ProgramList(res);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"program/{id}"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public Program getProgramById(@PathVariable(value = "id") String id) {
        Program program = this.programService.findById(Long.parseLong(id));
        if (program != null) {
            return program;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no program with id " + id);
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"program/current"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public ProgramList getCurrent() {
        return new ProgramList(this.programService.findCurrent());
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"program"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public Program getProgramByidentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        Program program = this.programService.findByIdentifier(identifier);
        if (program != null) {
            return program;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no program with identifier " + identifier);
        }
    }

    @PostMapping(value = {"program"}, produces = {"application/xml; charset=utf-8", "application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Message<ProgramDTO>> createProgram(@RequestBody ProgramDTO programDTO) {
        Program program = this.programService.save(programDTO);
        return new ResponseEntity<Message<ProgramDTO>>(new Message<ProgramDTO>(HttpStatus.CREATED.value(), program.getIdentifier(), programDTO), HttpStatus.CREATED);
    }

    @DeleteMapping(value = {"program"}, params = {"identifier"}, produces = {"application/xml; charset=utf-8", "application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String removeProgramByIdentifier(@RequestParam(required = true) String identifier) {
        this.programService.deleteByIdentifier(identifier);
        return "";
    }

    @DeleteMapping(value = {"program/{id}"}, produces = {"application/xml"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String removeProgramById(@PathVariable(required = true) String id) {
        this.programService.deleteById(Long.valueOf(id));
        return "";
    }
}
