package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Organisation;
import java.util.List;
import java.util.Set;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public abstract interface OrganisationRepository
        extends CrudRepository<Organisation, Long> {

    @Query("select o from Organisation o left join o.term l where l.identifier= ?1 or l.urn=?1")
    public abstract Organisation findByIdentifier(String identifier);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("delete from Organisation o where o.term in (select l from LinkedDataTerm l where l.identifier= ?1 or l.urn=?1)")
    public abstract void deleteByIdentifier(String identifier);

    @Query("select o from Organisation o left join o.term l where l.identifier in (?1)")
    public abstract List<Organisation> findAllByIdentifier(Set<String> identifiers);

    @Query("select o from Organisation o left join o.term l where l.name= ?1")
    public abstract Organisation findByName(String name);
}
