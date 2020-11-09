package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.ProgramList;
import eu.eurofleets.ears3.dto.ProgramDTO;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.ProgramService;
import java.util.List;
import javax.websocket.server.PathParam;
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
    private EventService eventService;
    @Autowired
    private ProgramService programService;
    @Autowired
    private CruiseService cruiseService;

    @RequestMapping(method = {RequestMethod.GET}, value = {"programs"}, produces = {"application/xml", "application/json"})
    public ProgramList getProgramList(@RequestParam(required = false, defaultValue = "") String cruiseIdentifier) {
        List<Program> res;
        // List<Program> res;
        if (cruiseIdentifier == null || "".equals(cruiseIdentifier)) {
            res = this.programService.findAll();
        } else {
            res = this.programService.findByCruiseIdentifier(cruiseIdentifier);
        }
        return new ProgramList(res);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"program/{id}"}, produces = {"application/xml", "application/json"})
    public Program getProgramById(@PathVariable(value = "id") String id) {
        Program program = this.programService.findById(Long.parseLong(id));
        if (program != null) {
            return program;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no program with id " + id);
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"program"}, params = {"identifier"}, produces = {"application/xml", "application/json"})
    public Program getProgramByidentifier(@RequestParam(required = true, value = "identifier") String identifier) {
        Program program = this.programService.findByIdentifier(identifier);
        if (program != null) {
            return program;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no program with identifier " + identifier);
        }
    }

    /*
    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"insertProgram"}, params = {"id", "cruiseId"}, produces = {"application/xml"})
    public Message insertProgram(@RequestParam(required = true) String id, @RequestParam(required = true) String cruiseId, @RequestParam(required = false) String originatorCode, @RequestParam(required = false) String PIName, @RequestParam(required = false) String description, @RequestParam(required = false) String projects)
            throws ParseException {
        Program program = new Program();
        program.setId(id);
        program.setCruiseId(cruiseId);
        program.setOriginatorcode(originatorCode);
        program.setPIName(PIName);
        program.setDescription(description);

        if (projects != null) {
            String[] projectsArray = projects.split("\\,");
            Set<ProjectO> projectsSet = new HashSet();

            for (String str : projectsArray) {
                ProjectO project = null;
                try {
                    project = this.programService.getProject(str);
                } catch (NumberFormatException e) {
                }
                if (project == null) {
                    project = new ProjectO(str, "Name:" + str);
                }
                projectsSet.add(project);
            }
            program.setProjects(projectsSet);
        }

        this.programService.setProgram(program);
        return new Message(program.getProgramId(), "Program inserted");
    }*/
    @PostMapping(value = {"program"}, produces = {"application/xml", "application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Program> createProgram(@RequestBody ProgramDTO programDTO) {
        //return Response.status(HttpStatus.CREATED.value()).entity(this.cruiseService.save(cruiseDTO)).build();
        //  return ResponseEntity.ok(this.cruiseService.save(cruiseDTO).getId());//<>(this.cruiseService.save(cruiseDTO), HttpStatus.CREATED);
        return new ResponseEntity<Program>(this.programService.save(programDTO), HttpStatus.CREATED);
    }

    @DeleteMapping(value = {"program"}, params = {"identifier"}, produces = {"application/xml", "application/json"})
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
