 package eu.eurofleets.ears2.controller;
 
 import eu.eurofleets.ears2.domain.message.Message;
 import eu.eurofleets.ears2.domain.program.Program;
 import eu.eurofleets.ears2.domain.program.ProgramList;
 import eu.eurofleets.ears2.domain.program.Project;
 import eu.eurofleets.ears2.service.CruiseService;
 import eu.eurofleets.ears2.service.EventService;
 import eu.eurofleets.ears2.service.ProgramService;
 import java.text.ParseException;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.web.bind.annotation.RequestMapping;
 import org.springframework.web.bind.annotation.RequestParam;
 import org.springframework.web.bind.annotation.RestController;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 @RestController
 public class ProgramController
 {
   public static final String DEFAULT_VALUE = "_";
   @Autowired
   private EventService eventService;
   @Autowired
   private ProgramService programService;
   @Autowired
   private CruiseService cruiseService;
   
   @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, value={"insertProgram"}, params={"id", "cruiseId"}, produces={"application/xml"})
   public Message insertProgram(@RequestParam(required=true) String id, @RequestParam(required=true) String cruiseId, @RequestParam(required=false) String originatorCode, @RequestParam(required=false) String PIName, @RequestParam(required=false) String description, @RequestParam(required=false) String projects)
     throws ParseException
   {
     Program program = new Program();
     program.setProgramId(id);
     program.setCruiseId(cruiseId);
     program.setOriginatorcode(originatorCode);
     program.setPIName(PIName);
     program.setDescription(description);
     
 
     if (projects != null)
     {
       String[] projectsArray = projects.split("\\,");
       Set<Project> projectsSet = new HashSet();
       
       for (String str : projectsArray) {
         Project project = null;
         try {
           project = this.programService.getProject(str);
         }
         catch (NumberFormatException e) {}
         if (project == null) {
           project = new Project(str, "Name:" + str);
         }
         projectsSet.add(project);
       }
       program.setProjects(projectsSet);
     }
     
     this.programService.setProgram(program);
     return new Message(program.getProgramId(), "Program inserted");
   }
   
   @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, value={"getProgram"}, produces={"application/xml"})
   public ProgramList getProgramList(@RequestParam(required=false, defaultValue="_") String cruiseId) {
     List<Program> res;
    // List<Program> res;
     if ("_".equals(cruiseId))
     {
       res = this.programService.getProgramList();
     }
     else {
       res = this.programService.getProgramListByCruiseId(cruiseId);
     }
     return new ProgramList(res);
   }
   
   @RequestMapping(method={org.springframework.web.bind.annotation.RequestMethod.GET}, value={"getProgram"}, params={"id"}, produces={"application/xml"})
   public Program getProgramById(@RequestParam(required=true, value="id") String programId)
   {
     return this.programService.getProgramByID(programId);
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/controller/ProgramController.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */