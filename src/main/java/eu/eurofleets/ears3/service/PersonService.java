/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 *
 * @author Thomas Vandenberghe
 */
@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> findByName(String firstName, String lastName) {
        Assert.notNull(firstName, "firstName must not be null");
        Assert.notNull(lastName, "lastName must not be null");
        return this.personRepository.findByName(firstName, lastName);
    }
    
        public Person findById(Long id) {
       
        return this.personRepository.findById(id).orElse(null);
    }

    /**
     * *
     * Find one or more persons by a full name, ie. 'firstName lastName'
     * @param person
     * @return
     */
    public List<Person> findByName(Person person) {
        return findByName(person.getFirstName(), person.getLastName());
    }

    /**
     * **
     * Find one or more persons by a full name, ie. 'firstName lastName'
     *
     * @param fullName
     * @return
     */
    public List<Person> findByFullName(String fullName) {
        return this.personRepository.findByFullName(fullName);
    }

    public Person findByNameAndOrganisation(String firstName, String lastName, Organisation organisation) {
        Assert.notNull(firstName, "firstName must not be null");
        Assert.notNull(lastName, "lastName must not be null");
        return this.personRepository.findByNameAndOrganisation(firstName, lastName, organisation);
    }

    public Person findByNameAndOrganisation(Person person) {
        return findByNameAndOrganisation(person.getFirstName(), person.getLastName(), (Organisation) person.getOrganisation());
    }

    public Person findOrCreate(Person person) {
        if (person == null) {
            return null;
        }
        //try {
        Person foundPerson = personRepository.findByNameAndOrganisation(person.getFirstName(), person.getLastName(), (Organisation) person.getOrganisation());
        if (foundPerson != null) {
            person.setId(foundPerson.getId());
        } else if (person.getEmail() != null) {
            foundPerson = personRepository.findByEmail(person.getEmail());
            if (foundPerson != null) {
                person.setId(foundPerson.getId());
            }
        }
        return personRepository.save(person);
    }

    public void save(Person person) {
         personRepository.save(person);
    }

    public void delete(Person person) {
         personRepository.delete(person);
    }
}
