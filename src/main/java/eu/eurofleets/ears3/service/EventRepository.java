package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Event;

import java.time.OffsetDateTime;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface EventRepository extends JpaRepository<Event, Long> {

    String SELECT = "select e from Event e ";
    String WHERE = " where ";
    String AND = " and ";
    String COUNT = "select count(e) from Event e ";
    String ORDER_BY = " order by e.timeStamp";


    String TIMESTAMP_WHERE = "e.timeStamp between :startDate and :endDate ";
    String TIME_WHERE = "e.creationTime >= :after or e.modificationTime >= :after ";
    String TEXT_WHERE = "COALESCE(:station, e.station, '') = COALESCE(e.station, '') and (COALESCE(:label, e.label, '') = COALESCE(e.label, '') or COALESCE(:description, e.description, '') = COALESCE(e.description, '')) ";
    String PLATFORM_WHERE = "(COALESCE(:platformIdentifier, l.identifier, '') = COALESCE(l.identifier, '') or COALESCE(:platformIdentifier, l.urn, '') = COALESCE(l.urn, '')) ";
    String CRUISE_WHERE = "(COALESCE(:cruiseIdentifier, c.identifier, '') = COALESCE(c.identifier, '') or COALESCE(:cruiseIdentifier, c.name, '') = COALESCE(c.name, ''))";
    String PROGRAM_WHERE = "COALESCE(:programIdentifier, p.identifier, '') = COALESCE(p.identifier, '') ";
    String ACTOR_WHERE = "COALESCE(:actorEmail, pe.email, '') = COALESCE(pe.email, '') ";

    @Query(value = "select e from Event e where e.identifier = :identifier")
    Event findByIdentifier(@Param("identifier") String identifier);

    @Query(value = SELECT + WHERE + TEXT_WHERE + ORDER_BY,
            countQuery = COUNT + WHERE + TEXT_WHERE)
    Page<Event> findByText(@Param("label") String label,
                           @Param("station") String station,
                           @Param("description") String description,
                           Pageable pageable);

    @Query(value = SELECT + WHERE + TIMESTAMP_WHERE + ORDER_BY,
            countQuery = COUNT + WHERE + TIMESTAMP_WHERE)
    Page<Event> findByTimeStampBetween(@Param("startDate") OffsetDateTime startDate,
                                       @Param("endDate") OffsetDateTime endDate,
                                       Pageable pageable);


    String PLATFORM_ACTOR_PROGRAM_Q = "left join e.platform pl left join e.program p left join e.actor pe left join pl.term l "
            + WHERE + PLATFORM_WHERE + AND + ACTOR_WHERE + AND + PROGRAM_WHERE + AND + TEXT_WHERE;

    @Query(value = SELECT + PLATFORM_ACTOR_PROGRAM_Q + ORDER_BY,
            countQuery = COUNT + PLATFORM_ACTOR_PROGRAM_Q)
    Page<Event> findAllByPlatformActorProgram(@Param("platformIdentifier") String platformIdentifier,
                                              @Param("actorEmail") String actorEmail,
                                              @Param("programIdentifier") String programIdentifier,
                                              @Param("label") String label,
                                              @Param("station") String station,
                                              @Param("description") String description,
                                              Pageable pageable);

    String PLATFORM_ACTOR_PROGRAM_DATES_Q = PLATFORM_ACTOR_PROGRAM_Q + AND + TIMESTAMP_WHERE;

    @Query(value = SELECT + PLATFORM_ACTOR_PROGRAM_DATES_Q + ORDER_BY,
            countQuery = COUNT + PLATFORM_ACTOR_PROGRAM_DATES_Q)
    Page<Event> findAllByPlatformActorProgramDates(@Param("platformIdentifier") String platformIdentifier,
                                                   @Param("actorEmail") String actorEmail,
                                                   @Param("programIdentifier") String programIdentifier,
                                                   @Param("startDate") OffsetDateTime startDate,
                                                   @Param("endDate") OffsetDateTime endDate,
                                                   @Param("label") String label,
                                                   @Param("station") String station,
                                                   @Param("description") String description,
                                                   Pageable pageable);

    String CRUISE_Q = "inner join Cruise c on e.timeStamp between c.startDate and c.endDate" + WHERE + CRUISE_WHERE;

    String PROGRAM_Q = "left join e.program p left join e.actor pe ";
    String CRUISE_PROGRAM_ACTOR_Q = PROGRAM_Q + CRUISE_Q + AND + ACTOR_WHERE + AND + PROGRAM_WHERE + AND + TEXT_WHERE;

    @Query(value = SELECT + CRUISE_PROGRAM_ACTOR_Q + ORDER_BY,
            countQuery = COUNT + CRUISE_PROGRAM_ACTOR_Q)
    Page<Event> findAllByCruiseProgramActor(@Param("cruiseIdentifier") String cruiseIdentifier,
                                            @Param("programIdentifier") String programIdentifier,
                                            @Param("actorEmail") String actorEmail,
                                            @Param("label") String label,
                                            @Param("station") String station,
                                            @Param("description") String description,
                                            Pageable pageable);


    @Modifying
    @Transactional
    @Query("delete from Event e where e.identifier=:identifier")
    void deleteByIdentifier(@Param("identifier") String identifier);

    // NOTE: uses java.util.Date while every other date-bearing method in this
    // file uses OffsetDateTime. Left as-is - changing it changes this
    // method's public signature, which may affect other callers.
    @Modifying
    @Transactional
    @Query("delete from Event e where e.timeStamp between :startDate and :endDate")
    void deleteByTimeStampBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * @Todo: LIMIT is not acceptable in JPQL / Either the repository needs to use a Pageable interface, or I use the native Query here
     */
    @Query(value = "select distinct event_definition_id from event "
            + "left join linked_data_term ldp on ldp.id = event.process_id "
            + "left join linked_data_term lda on lda.id = event.action_id "
            + "left join tool t on t.id = event.tool_id "
            + "left join linked_data_term ldt on ldt.id = t.term_id "
            + "where ldp.name = :process and lda.name = :action and ldt.name= :tool limit 1;", nativeQuery = true)
    String findUUIDByToolActionProc(@Param("tool") String tool, @Param("process") String process, @Param("action") String action);


    /*No longer used*/
    String TOOL_Q = "left join e.tool t left join t.term l where l.identifier= :identifier or l.urn= :identifier";

    @Query(value = SELECT + TOOL_Q + ORDER_BY,
            countQuery = COUNT + TOOL_Q)
    Page<Event> findByTool(@Param("identifier") String identifier, Pageable pageable);

    String PLATFORM_CODE_Q = "inner join e.platform p left join p.term l where l.identifier= :platformIdentifier or l.urn= :platformIdentifier";

    @Query(value = SELECT + PLATFORM_CODE_Q + ORDER_BY,
            countQuery = COUNT + PLATFORM_CODE_Q)
    Page<Event> findByPlatformCode(@Param("platformIdentifier") String platformIdentifier, Pageable pageable);

    @Query(value = SELECT + WHERE + TIME_WHERE + ORDER_BY,
            countQuery = COUNT + WHERE + TIME_WHERE)
    Page<Event> findByCreatedOrModifiedAfter(@Param("after") OffsetDateTime after, Pageable pageable);

    @Query(value = SELECT + CRUISE_Q + ORDER_BY,
            countQuery = COUNT + CRUISE_Q)
    Page<Event> findByCruise(@Param("cruiseIdentifier") String cruiseIdentifier, Pageable pageable);

}