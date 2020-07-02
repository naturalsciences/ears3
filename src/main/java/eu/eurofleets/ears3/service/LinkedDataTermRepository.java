package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.LinkedDataTerm;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public abstract interface LinkedDataTermRepository
        extends CrudRepository<LinkedDataTerm, Long> {

    @Query("select ldt from LinkedDataTerm ldt where ldt.identifier=?1")
    public LinkedDataTerm findByIdentifier(String identifier);
    
    @Query("select ldt from LinkedDataTerm ldt where ldt.identifier in (?1)")
    List<LinkedDataTerm> findAllByIdentifier(Set<String> identifiers);
   
}
