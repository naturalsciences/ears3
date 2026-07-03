package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Tool;
import java.util.List;
import java.util.Set;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public abstract interface ToolRepository
        extends JpaRepository<Tool, Long> {

    @Query("select t from Tool t left join t.term l where l.identifier= ?1 or l.urn=?1")
    public abstract Tool findByIdentifier(String identifier);

    @Modifying
    @org.springframework.transaction.annotation.Transactional
    @Query("delete from Tool t where t.term in (select l from LinkedDataTerm l where l.identifier= ?1 or l.urn=?1)")
    public abstract void deleteByIdentifier(String identifier);

    @Query("select t from Tool t left join t.term l where l.identifier in (?1)")
    public abstract List<Tool> findAllByIdentifier(Set<String> identifiers);
        
    @Query("select t from Tool t left join t.term l where l.name= ?1")
    public abstract Tool findByName(String name);
    
}
