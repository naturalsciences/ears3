/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.domain.Program;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 *
 * @author thomas
 */
@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person findByName(String firstName, String lastName) {
        Assert.notNull(firstName, "firstName must not be null");
        Assert.notNull(lastName, "lastName must not be null");
        return this.personRepository.findByName(firstName, lastName);
    }

    public Person findByName(Person person) {
        return findByName(person.getFirstName(), person.getLastName());
    }

    public Person findOrCreate(Person person) {
        if (person == null) {
            return null;
        }
        try {
            return personRepository.save(person);
        } catch (DataIntegrityViolationException | PersistenceException | ConstraintViolationException ex) { //
            return findByName(person);
        }
    }
}
