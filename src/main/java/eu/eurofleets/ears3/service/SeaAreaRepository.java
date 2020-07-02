package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Country;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.domain.SeaArea;
import eu.eurofleets.ears3.domain.Tool;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public abstract interface SeaAreaRepository
        extends CrudRepository<SeaArea, Long> {

    
    @Query("select s from SeaArea s left join LinkedDataTerm l on l.id=s.term where l.identifier= ?1 or l.urn=?1")
    public abstract SeaArea findByIdentifier(String identifier);
           
    @Query("select p from SeaArea p left join LinkedDataTerm l on l.id=p.term where l.identifier in (?1)")
    public abstract List<SeaArea> findAllByIdentifier(Set<String> identifiers);
    
    @Query("select p from SeaArea p left join LinkedDataTerm l on l.id=p.term where l.name= ?1")
    public abstract SeaArea findByName(String name);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("delete from SeaArea p where p.term in (select l.id from LinkedDataTerm l where l.identifier= ?1 or l.urn=?1)")
    public abstract void deleteByIdentifier(String identifier);
}

