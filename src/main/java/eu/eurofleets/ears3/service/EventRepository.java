package eu.eurofleets.ears3.service;

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

    //@Query("select e from Event e where e.id = ?1")
    //public abstract Optional<Event> findById(long id);
    @Query("select e from Event e where e.identifier = ?1")
    public abstract Event findByIdentifier(String identifier);

    @Query("select e from Event e where e.timeStamp between ?1 and ?2 order by e.timeStamp")
    public abstract List<Event> findByTimeStampBetween(OffsetDateTime paramDate1, OffsetDateTime paramDate2);

    @Query("select e from Event e left join Tool t on e.tool=t.id left join LinkedDataTerm l on l.id=t.term where l.identifier= ?1 or l.urn= ?1 order by e.timeStamp")
    public abstract List<Event> findByTool(String identifier);

    @Query("select e from Event e inner join Platform p on e.platform = p.id left join LinkedDataTerm l on l.id=p.term where l.identifier= ?1 or l.urn= ?1 order by e.timeStamp")
    public abstract List<Event> findByPlatformCode(String platformIdentifier);

    @Query("select e from Event e inner join Cruise c on e.timeStamp between c.startDate and c.endDate order by e.timeStamp")
    public abstract List<Event> findByCruise(String platformCode);

    @Query("select e from Event e left join Platform pl on e.platform = pl.id left join Program p on e.program = p.id left join Person pe on e.actor = pe.id left join LinkedDataTerm l on l.id=pl.term where (COALESCE(cast(?1 as string), l.identifier) = l.identifier or COALESCE(cast(?1 as string), l.urn) = l.urn) and COALESCE(cast(?2 as string), pe.email) = pe.email and COALESCE(cast(?3 as string), p.identifier) = p.identifier order by e.timeStamp")
    public abstract List<Event> findAllByPlatformActorAndProgram(String platformCode, String personEmail, String programIdentifier);

    @Modifying
    @Transactional
    @Query("delete from Event e where e.identifier=?1")
    public void deleteByIdentifier(String identifier);

    @Query("select e from Event e where e.creationTime >= ?1 or e.modificationTime >= ?1 order by e.timeStamp")
    public abstract List<Event> findByCreatedOrModifiedAfter(OffsetDateTime after);

    @Query("select e from Event e left join Platform pl on e.platform = pl.id left join Program p on e.program = p.id left join Person pe on e.actor = pe.id left join LinkedDataTerm l on l.id=pl.term where (COALESCE(cast(?1 as string), l.identifier) = l.identifier or COALESCE(cast(?1 as string), l.urn) = l.urn) and COALESCE(cast(?2 as string), pe.email) = pe.email and COALESCE(cast(?3 as string), p.identifier) = p.identifier and e.timeStamp between ?4 and ?5 order by e.timeStamp")
    public abstract List<Event> findAllByPlatformActorProgramAndDates(String platformIdentifier, String personEmail, String programIdentifier, OffsetDateTime start, OffsetDateTime end);

    @Query("select e from Event e left join Program p on e.program = p.id left join Person pe on e.actor = pe.id inner join Cruise c on e.timeStamp between c.startDate and c.endDate where COALESCE(cast(?1 as string), c.identifier) = c.identifier and COALESCE(cast(?3 as string), pe.email) = pe.email and COALESCE(cast(?2 as string), p.identifier) = p.identifier order by e.timeStamp")
    public abstract List<Event> findAllByCruiseProgramAndActor(String cruiseIdentifier, String programIdentifier, String actorEmail);

    /**@Todo: the Query*/
    String findUUIDByToolActionProc(String toolCategory, String tool, String process, String action);
}