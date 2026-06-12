package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Harbour;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public abstract interface HarbourRepository
        extends CrudRepository<Harbour, Long> {
    
    @Query("select h from Harbour h left join h.term l where l.identifier= ?1 or l.urn=?1")
    public abstract Harbour findByIdentifier(String identifier);

    @Query("select h from Harbour h left join h.term l where l.name= ?1")
    public abstract Harbour findByName(String name);

    @Modifying
    @Transactional
    @Query("delete from Harbour h where h.term in (select l from LinkedDataTerm l where l.identifier= ?1 or l.urn=?1)")
    public abstract void deleteByIdentifier(String identifier);

    @Query("select h from Harbour h left join h.term l where l.identifier in (?1)")
    public abstract List<Harbour> findAllByIdentifier(Set<String> identifiers);
   
}
