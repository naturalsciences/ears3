package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.domain.PersonList;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.ProgramList;
import eu.eurofleets.ears3.dto.ProgramDTO;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.PersonService;
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
public class PersonController {

    @Autowired
    private PersonService personService;

    @RequestMapping(method = {RequestMethod.GET}, value = {"person"}, params = {"fullName"}, produces = {"application/xml", "application/json"})
    public PersonList getPersonByFullName(@RequestParam(required = true, value = "fullName") String fullName) {
        return new PersonList(this.personService.findByFullName(fullName));

    }
}
