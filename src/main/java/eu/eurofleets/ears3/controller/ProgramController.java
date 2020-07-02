package eu.eurofleets.ears3.controller;

import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.ProgramList;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.ProgramService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProgramController {

    public static final String DEFAULT_VALUE = "_";
    @Autowired
    private EventService eventService;
    @Autowired
    private ProgramService programService;
    @Autowired
    private CruiseService cruiseService;

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"getProgram"}, produces = {"application/xml"})
    public ProgramList getProgramList(@RequestParam(required = false, defaultValue = "_") long cruiseId) {
        List<Program> res;
        // List<Program> res;
        if ("_".equals(cruiseId)) {
            res = this.programService.findAll();
        } else {
            res = this.programService.findByCruiseId(cruiseId);
        }
        return new ProgramList(res);
    }

    @RequestMapping(method = {org.springframework.web.bind.annotation.RequestMethod.GET}, value = {"getProgram"}, params = {"id"}, produces = {"application/xml"})
    public Program getProgramById(@RequestParam(required = true, value = "id") long programId) {
        return this.programService.findById(programId);
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

}
