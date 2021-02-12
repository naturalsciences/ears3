package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Thermosal;
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
public abstract interface ThermosalRepository
        extends CrudRepository<Thermosal, Long> {

    @Query("select n from Thermosal n where n.instrumentTime = ?1")
    public Thermosal findByDate(OffsetDateTime instrumentTime);

    @Query("select n from Thermosal n where n.instrumentTime  in (?1)")
    public List<Thermosal> findAllByDate(Set<Instant> instrumentTime);

}
