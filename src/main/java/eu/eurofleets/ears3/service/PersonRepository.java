package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public abstract interface PersonRepository
        extends CrudRepository<Person, Long> {

    @Query("select p from Person p where p.firstName= ?1 and p.lastName=?2")
    public abstract List<Person> findByName(String firstName, String lastName);

    @Query("select p from Person p where p.firstName= ?1 and p.lastName=?2 and p.organisation=?3")
    public abstract Person findByNameAndOrganisation(String firstName, String lastName, Organisation organisation);

    @Query("select p from Person p where p.firstName= ?1 and p.lastName=?2 and p.email= ?3")
    public abstract Person findByNameAndEmail(String firstName, String lastName,String email);

    @Query("select p from Person p where p.email= ?1")
    public abstract Person findByEmail(String email);

    @Query("select p from Person p where p.firstName||' '||p.lastName = ?1")
    public List<Person> findByFullName(String fullName);

}
