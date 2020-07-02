package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Event;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public abstract interface EventRepository
        extends CrudRepository<Event, Long> {

    // public abstract Event findByEventId(String param);
    //public abstract Event findFirstByTimeStampBeforeOrderByTimeStampDesc(Date paramDate);
    //public abstract List<Event> findByTimeStampBetween(Date paramDate1, Date paramDate2);
    @Modifying
    @Transactional
    @Query("delete from Event e where e.timeStamp between ?1 and ?2")
    public abstract void deleteByTimeStampBetween(Date paramDate1, Date paramDate2);

    @Query("select e from Event e where e.identifier = ?1")
    public abstract Event findByIdentifier(String identifier);

    @Query("select e from Event e where e.timeStamp between ?1 and ?2")
    public abstract List<Event> getByTimeStampBetween(OffsetDateTime paramDate1, OffsetDateTime paramDate2);

    @Query("select e from Event e left join Tool t on e.tool=t.id left join LinkedDataTerm l on l.id=t.term where l.identifier= ?1 or l.urn= ?1")
    public abstract List<Event> findByTool(String identifier);
}
