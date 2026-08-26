package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;

import java.time.LocalDate;
import java.util.List;
import jakarta.transaction.Transactional;
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

    @Query(value = """
            SELECT q.* FROM (SELECT 'pics' as engagement_type, p.* FROM person p
                             JOIN program_principal_investigators ppi ON ppi.principal_investigators_id = p.id
                             JOIN "program" prg ON prg.id = ppi.program_id
                             JOIN cruise_programs cp ON cp.program_id = prg.id
                             JOIN cruise c ON c.id = cp.cruise_id
                             WHERE c.start_date >= ?1 AND c.end_date <= ?2
                             UNION
                             SELECT 'pics' as engagement_type, p.* FROM person p
                             JOIN cruise_chief_scientists ccs ON ccs.chief_scientist_id = p.id
                             JOIN cruise c ON c.id = ccs.cruise_id
                             WHERE c.start_date >= ?1 AND c.end_date <= ?2
                             UNION
                             SELECT 'pics' as engagement_type, p.* FROM person p
                             JOIN event e ON e.actor_id = p.id
                             WHERE e."time_stamp" >= ?1 AND e."time_stamp" <= ?2 ) as q
                             WHERE lower(last_name) not like '%unknown%'
                             ORDER BY q.last_name, q.first_name
            """, nativeQuery = true)
    public List<Person> findByActiveInPeriod(LocalDate periodStart, LocalDate periodEnd);

}
