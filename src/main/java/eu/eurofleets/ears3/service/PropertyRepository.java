package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Project;
import eu.eurofleets.ears3.domain.Property;
import eu.eurofleets.ears3.domain.Tool;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public abstract interface PropertyRepository
        extends CrudRepository<Property, Long> {
    
    @Query("select p from Property p left join LinkedDataTerm l on l.id=p.key where l.identifier= ?1 or l.urn=?1")
    public abstract Property findByIdentifier(String identifier);
}