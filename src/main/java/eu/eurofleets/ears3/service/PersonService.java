/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.dto.PersonDTO;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
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
    private final OrganisationRepository organisationRepository;

    @Autowired
    public PersonService(PersonRepository personRepository, OrganisationRepository organisationRepository) {
        this.personRepository = personRepository;
        this.organisationRepository = organisationRepository;
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
        return findByNameAndOrganisation(person.getFirstName(), person.getLastName(),
                (Organisation) person.getOrganisation());
    }

    public Person findOrCreate(Person person) {
        if (person == null) {
            return null;
        }
        Person foundPerson = personRepository.findByNameAndOrganisation(person.getFirstName(), person.getLastName(),
                (Organisation) person.getOrganisation());
        if (foundPerson != null) {
            person.setId(foundPerson.getId()); //set the new variant to the id of the found one. If we later save() the new one we effectively update the the found one with info of the new one
        } else if (person.getEmail() != null) {
            foundPerson = personRepository.findByNameAndEmail(person.getFirstName(), person.getLastName(),
                    person.getEmail());
            if (foundPerson != null) {
                person.setId(foundPerson.getId());
            }
        }
        return personRepository.save(person);
    }

    public void save(Person person) {
        personRepository.save(person);
    }

    public void save(PersonDTO personDTO) {
        Person person = new Person(personDTO);
        Organisation foundOrganisation = organisationRepository.findByIdentifier(personDTO.getOrganisation());
        person.setOrganisation(foundOrganisation);
        findOrCreate(person);
    }

    public void delete(Person person) {
        personRepository.delete(person);
    }

    public List<Person> findAll() {
        return IterableUtils.toList(this.personRepository.findAll());
    }
}
