package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Weather;
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
public abstract interface WeatherRepository
        extends CrudRepository<Weather, Long> {

    @Query("select t from Weather t where t.instrumentTime = ?1")
    public Weather findByInstrumentTime(OffsetDateTime instrumentTime);

    @Query("select t from Weather t where t.timeStamp = ?1")
    public Weather findByTimestamp(OffsetDateTime timeStamp);

    @Query("select t from Weather t where t.instrumentTime=?1 or (t.instrumentTime is null and t.timeStamp = ?1)")
    public Weather findByDate(OffsetDateTime date);

    @Query("select w from Weather w where w.timeStamp = (select max(w.timeStamp) from Weather w)")
    public Weather findLast();

    @Query("select t from Weather t where t.instrumentTime  in (?1)")
    public List<Weather> findAllByInstrumentTime(Set<OffsetDateTime> instrumentTime);

}
