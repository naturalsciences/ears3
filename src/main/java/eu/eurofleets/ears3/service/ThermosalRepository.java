package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Thermosal;
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

    @Query("select t from Thermosal t where t.instrumentTime = ?1")
    public Thermosal findByInstrumentTime(OffsetDateTime instrumentTime);

    @Query("select t from Thermosal t where t.timeStamp = ?1")
    public Thermosal findByTimestamp(OffsetDateTime timeStamp);

    @Query("select t from Thermosal t where t.instrumentTime=?1 or (t.instrumentTime is null and t.timeStamp = ?1)")
    public Thermosal findByDate(OffsetDateTime date);

    @Query("select t from Thermosal t where t.timeStamp = (select max(t.timeStamp) from Thermosal t)")
    public Thermosal findLast();

    @Query("select t from Thermosal t where t.instrumentTime  in (?1)")
    public List<Thermosal> findAllByInstrumentTime(Set<OffsetDateTime> instrumentTime);

}
