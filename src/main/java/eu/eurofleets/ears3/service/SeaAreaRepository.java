package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.SeaArea;
import java.util.List;
import java.util.Set;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public abstract interface SeaAreaRepository
        extends CrudRepository<SeaArea, Long> {

    
    @Query("select s from SeaArea s left join s.term l where l.identifier= ?1 or l.urn=?1")
    public abstract SeaArea findByIdentifier(String identifier);
           
    @Query("select s from SeaArea s left join s.term l where l.identifier in (?1)")
    public abstract List<SeaArea> findAllByIdentifier(Set<String> identifiers);
    
    @Query("select s from SeaArea s left join s.term l where l.name= ?1")
    public abstract SeaArea findByName(String name);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("delete from SeaArea s where s.term in (select l from LinkedDataTerm l where l.identifier= ?1 or l.urn=?1)")
    public abstract void deleteByIdentifier(String identifier);
}

