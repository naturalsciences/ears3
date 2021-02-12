package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Project;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public abstract interface ProjectRepository
        extends CrudRepository<Project, Long> {
    
    
    @Query("select p from Project p left join LinkedDataTerm l on l.id=p.term where l.identifier= ?1 or l.urn=?1")
    public abstract Project findByIdentifier(String identifier);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("delete from Project p where p.term in (select l.id from LinkedDataTerm l where l.identifier= ?1 or l.urn=?1)")
    public abstract void deleteByIdentifier(String identifier);
        
    @Query("select p from Project p left join LinkedDataTerm l on l.id=p.term where l.identifier in (?1)")
    public abstract List<Project> findAllByIdentifier(Set<String> identifiers);
       
    @Query("select p from Project p left join LinkedDataTerm l on l.id=p.term where l.name= ?1")
    public abstract Project findByName(String name);
}