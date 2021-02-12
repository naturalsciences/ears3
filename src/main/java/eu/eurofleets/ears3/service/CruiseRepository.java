package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Cruise;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CruiseRepository
        extends EarsRepository<Cruise, Long> {

    //@Query("select c from Cruise c where c.id = ?1")
    //public abstract Optional<Cruise> findById(long id);
    @Query("select c from Cruise c where c.identifier = ?1")
    public abstract Cruise findByIdentifier(String identifier);

    @Query("select c from Cruise c left join Platform p on c.platform = p.id left join LinkedDataTerm l on l.id=p.term where l.identifier= ?1 or l.urn= ?1 order by c.endDate")
    public abstract Set<Cruise> findByPlatformCode(String code);

    @Query("select c from Cruise c where c.startDate>=?1 and c.endDate<=?2  order by c.endDate")
    public abstract Set<Cruise> findBetweenDate(OffsetDateTime startDate, OffsetDateTime endDate);

    @Query("select c from Cruise c where ?1 between c.startDate and c.endDate")
    public abstract Set<Cruise> findAtDate(OffsetDateTime at);

    @Query("select c from Cruise c left join Platform p on c.platform = p.id left join LinkedDataTerm l on l.id=p.term where (l.identifier= ?3 or l.urn= ?3) and c.startDate>=?1 and c.endDate<=?2  order by c.endDate")
    public Set<Cruise> findBetweenDate(OffsetDateTime startDate, OffsetDateTime endDate, String platformIdentifier);

    @Query("select c from Cruise c left join Platform p on c.platform = p.id left join LinkedDataTerm l on l.id=p.term where (l.identifier= ?2 or l.urn= ?2) and (?1 between c.startDate and c.endDate) order by c.endDate")
    public Set<Cruise> findAtDate(OffsetDateTime at, String platformIdentifier);

    @Modifying
    @Transactional
    @Query("delete from Cruise c where c.startDate >= ?1  and c.endDate <= ?2")
    public abstract void deleteByDate(Date paramDate1, Date paramDate2);

    @Modifying
    @Transactional
    @Query("delete from Cruise c where c.identifier=?1")
    public abstract void deleteByIdentifier(String identifier);

}
