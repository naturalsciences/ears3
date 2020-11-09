package eu.eurofleets.ears3.controller.html;

import eu.eurofleets.ears3.controller.rest.*;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.EventList;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.EventDTOList;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ToolDTO;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.ProgramService;
import eu.eurofleets.ears3.utilities.DatagramUtilities;
import java.util.List;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller()
@RequestMapping(value = "html")
public class HtmlController {

    @RequestMapping(method = {RequestMethod.GET}, value = {"/event"}, produces = {"text/html; charset=utf-8"})
    public String event() {
        return "event";
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"/events"}, produces = {"text/html; charset=utf-8"})
    public String events() {
        return "events";
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"/login"}, produces = {"text/html; charset=utf-8"})
    public String login() {
        return "login";
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"/settings"}, produces = {"text/html; charset=utf-8"})
    public String settings() {
        return "settings";
    }
}
