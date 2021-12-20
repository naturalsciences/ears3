package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.domain.PersonList;
import eu.eurofleets.ears3.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PersonController {

    @Autowired
    private PersonService personService;

    @RequestMapping(method = {RequestMethod.GET}, value = {"person"}, params = {"fullName"}, produces = {"application/xml", "application/json"})
    public PersonList getPersonByFullName(@RequestParam(required = true, value = "fullName") String fullName) {
        return new PersonList(this.personService.findByFullName(fullName));

    }
}
