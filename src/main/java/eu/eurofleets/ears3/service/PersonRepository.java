package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.domain.Project;
import eu.eurofleets.ears3.domain.Property;
import eu.eurofleets.ears3.domain.Tool;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public abstract interface PersonRepository
        extends CrudRepository<Person, Long> {

    @Query("select p from Person p where p.firstName= ?1 and p.lastName=?2")
    public abstract Person findByName(String firstName, String lastName);

}
