package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Platform;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public abstract interface PlatformRepository
        extends CrudRepository<Platform, Long> {

    @Query("select p from Platform p left join LinkedDataTerm l on l.id=p.term where l.identifier= ?1 or l.urn=?1")
    public abstract Platform findByIdentifier(String identifier);

    @Modifying
    @Transactional
    @Query("delete from Platform p where p.term in (select l.id from LinkedDataTerm l where l.identifier= ?1 or l.urn=?1)")
    public abstract void deleteByIdentifier(String identifier);
        
    @Query("select p from Platform p left join LinkedDataTerm l on l.id=p.term where l.identifier in (?1)")
    public abstract List<Platform> findAllByIdentifier(Set<String> identifiers);
       
    @Query("select p from Platform p left join LinkedDataTerm l on l.id=p.term where l.name= ?1")
    public abstract Platform findByName(String name);
}