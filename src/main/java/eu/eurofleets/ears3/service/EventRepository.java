package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Event;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public abstract interface EventRepository
        extends JpaRepository<Event, Long> {

    static final String SELECT = "select e ";
    static final String COUNT = "select count(e) ";
    static final String ORDER_BY = " order by e.timeStamp";

    @Query(value = "select e from Event e where e.identifier = ?1")
    public abstract Event findByIdentifier(String identifier);

    String TIMESTAMP_BETWEEN_Q = " from Event e where e.timeStamp between ?1 and ?2";
    @Query(value = SELECT + TIMESTAMP_BETWEEN_Q + ORDER_BY,
            countQuery = COUNT + TIMESTAMP_BETWEEN_Q)
    public abstract Page<Event> findByTimeStampBetween(OffsetDateTime paramDate1, OffsetDateTime paramDate2, Pageable pageable);

    String TOOL_Q = "from Event e left join e.tool t left join t.term l where l.identifier= ?1 or l.urn= ?1";
    @Query(value = SELECT + TOOL_Q + ORDER_BY,
            countQuery = COUNT + TOOL_Q)
    public abstract Page<Event> findByTool(String identifier, Pageable pageable);

    String PLATFORM_CODE_Q = "from Event e inner join e.platform p left join p.term l where l.identifier= ?1 or l.urn= ?1";
    @Query(value = SELECT + PLATFORM_CODE_Q + ORDER_BY,
            countQuery = COUNT + PLATFORM_CODE_Q)
    public abstract Page<Event> findByPlatformCode(String platformIdentifier, Pageable pageable);

    String CRUISE_Q = "from Event e inner join Cruise c on e.timeStamp between c.startDate and c.endDate";
    @Query(value = SELECT + CRUISE_Q + ORDER_BY,
            countQuery = COUNT + CRUISE_Q)
    public abstract Page<Event> findByCruise(String platformCode, Pageable pageable);

    String ALL_Q = "from Event e left join e.platform pl left join e.program p left join e.actor pe left join pl.term l where (COALESCE(cast(?1 as string), l.identifier) = l.identifier or COALESCE(cast(?1 as string), l.urn) = l.urn) and COALESCE(cast(?2 as string), pe.email) = pe.email and COALESCE(cast(?3 as string), p.identifier) = p.identifier";
    @Query(value = SELECT + ALL_Q + ORDER_BY,
            countQuery = COUNT + ALL_Q)
    public abstract Page<Event> findAllByPlatformActorAndProgram(String platformIdentifier, String actorEmail, String programIdentifier, Pageable pageable);

    String TIME_Q = "from Event e where e.creationTime >= ?1 or e.modificationTime >= ?1";
    @Query(value = SELECT + TIME_Q + ORDER_BY,
            countQuery = COUNT + TIME_Q)
    public abstract Page<Event> findByCreatedOrModifiedAfter(OffsetDateTime after, Pageable pageable);

    String PLATFORM_ACTOR_PROGRAM_DATES_Q = ALL_Q + " and e.timeStamp between ?4 and ?5";
    @Query(value = SELECT + PLATFORM_ACTOR_PROGRAM_DATES_Q + ORDER_BY,
            countQuery = COUNT + PLATFORM_ACTOR_PROGRAM_DATES_Q)
    public abstract Page<Event> findAllByPlatformActorProgramAndDates(String platformIdentifier, String actorEmail, String programIdentifier, OffsetDateTime start, OffsetDateTime end, Pageable pageable);

    String CRUISE_PROGRAM_ACTOR_Q = "from Event e left join e.program p left join e.actor pe inner join Cruise c on e.timeStamp between c.startDate and c.endDate where COALESCE(cast(?1 as string), c.identifier) = c.identifier and COALESCE(cast(?3 as string), pe.email) = pe.email and COALESCE(cast(?2 as string), p.identifier) = p.identifier";
    @Query(value = SELECT + CRUISE_PROGRAM_ACTOR_Q + ORDER_BY,
            countQuery = COUNT + CRUISE_PROGRAM_ACTOR_Q)
    public abstract Page<Event> findAllByCruiseProgramAndActor(String cruiseIdentifier, String programIdentifier, String actorEmail, Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete from Event e where e.identifier=?1")
    public void deleteByIdentifier(String identifier);

    @Modifying
    @Transactional
    @Query("delete from Event e where e.timeStamp between ?1 and ?2")
    public abstract void deleteByTimeStampBetween(Date paramDate1, Date paramDate2);

    /**
     * @Todo: LIMIT is not acceptable in JPQL /  Either the repository needs to use a Pageable interface, or I use the native Query here
     */
    //@Query("select distinct eventDefinitionId from event left join LinkedDataTerm ldp on ldp.id = event.processId left join LinkedDataTerm lda on lda.id = event.actionId left join tool t on t.id = event.toolId left join LinkedDataTerm ldt  on ldt.id = t.termId where ldp.name = ?3 and lda.name = ?4 and ldt.name=?2 limit 1")
    @Query(value = "select distinct event_definition_id from event left join linked_data_term ldp on ldp.id = event.process_id left join linked_data_term lda on lda.id = event.action_id left join tool t on t.id = event.tool_id left join linked_data_term ldt on ldt.id = t.term_id where ldp.name = :process and lda.name = :action and ldt.name= :tool limit 1;", nativeQuery = true)
    String findUUIDByToolActionProc(String tool, String process, String action);
    //String findUUIDByToolActionProc(String toolCategory, String tool, String process, String action);
}