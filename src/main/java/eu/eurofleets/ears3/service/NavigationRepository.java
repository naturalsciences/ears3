package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Navigation;
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

    @Query("select t from Navigation t where t.instrumentTime = ?1")
    public Navigation findByInstrumentTime(OffsetDateTime instrumentTime);

    @Query("select t from Navigation t where t.timeStamp = ?1")
    public Navigation findByTimestamp(OffsetDateTime timeStamp);

    @Query("select t from Navigation t where t.instrumentTime=?1 or (t.instrumentTime is null and t.timeStamp = ?1)")
    public Navigation findByDate(OffsetDateTime date);

    @Query("select t from Navigation t where t.timeStamp = (select max(t.timeStamp) from Navigation t)")
    public Navigation findLast();

    @Query("select t from Navigation t where t.instrumentTime  in (?1)")
    public List<Navigation> findAllByInstrumentTime(Set<OffsetDateTime> instrumentTime);

}
