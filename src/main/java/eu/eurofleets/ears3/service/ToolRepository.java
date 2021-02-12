package eu.eurofleets.ears3.service;

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
public abstract interface ToolRepository
        extends CrudRepository<Tool, Long> {

    @Query("select t from Tool t left join LinkedDataTerm l on l.id=t.term where l.identifier= ?1 or l.urn=?1")
    public abstract Tool findByIdentifier(String identifier);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("delete from Tool p where p.term in (select l.id from LinkedDataTerm l where l.identifier= ?1 or l.urn=?1)")
    public abstract void deleteByIdentifier(String identifier);

    @Query("select p from Tool p left join LinkedDataTerm l on l.id=p.term where l.identifier in (?1)")
    public abstract List<Tool> findAllByIdentifier(Set<String> identifiers);
        
    @Query("select p from Tool p left join LinkedDataTerm l on l.id=p.term where l.name= ?1")
    public abstract Tool findByName(String name);
    
}
