package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.domain.PersonList;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping(value = { "person" }, params = { "fullName" }, produces = {
            "application/xml", "application/json" })
    public PersonList getPersonByFullName(@RequestParam(value = "fullName") String fullName) {
        return new PersonList(this.personService.findByFullName(fullName));
    }

    @GetMapping(value = { "person" }, params = { "year" }, produces = {
            "application/xml", "application/json" })
    public PersonList getPersonByYearActive(@RequestParam(value = "year") int year) {
        return new PersonList(this.personService.findByActiveInYear(year));
    }

    @GetMapping(value = { "persons" }, produces = {
            "application/xml", "application/json" })
    public PersonList getAllPersons() {
        return new PersonList(this.personService.findAll());
    }

    @PostMapping(value = { "person" }, produces = { "application/xml; charset=utf-8",
            "application/json;charset=UTF-8" })
    @ResponseStatus(HttpStatus.CREATED)
    public void createPerson(@RequestBody PersonDTO personDTO) {
        this.personService.save(personDTO);
    }


    @DeleteMapping(value = { "person" }, params = { "id" }, produces = { "application/xml; charset=utf-8",
            "application/json;charset=UTF-8" })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String removePersonByIdentifier(@RequestParam(required = true) String id) {
        this.personService.deleteById(id);
        return "";
    }
}
