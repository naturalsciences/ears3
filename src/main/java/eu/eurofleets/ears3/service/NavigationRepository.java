package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.domain.Project;
import eu.eurofleets.ears3.domain.Property;
import eu.eurofleets.ears3.domain.Tool;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public abstract interface NavigationRepository
        extends CrudRepository<Navigation, Long> {

    @Query("select n from Navigation n where n.instrumentTime = ?1")
    public Navigation findByDate(OffsetDateTime instrumentTime);

    @Query("select n from Navigation n where n.instrumentTime  in (?1)")
    public List<Navigation> findAllByDate(Set<Instant> instrumentTime);

}
