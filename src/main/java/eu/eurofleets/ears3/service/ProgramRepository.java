package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Program;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.SortedSet;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public abstract interface ProgramRepository
        extends CrudRepository<Program, Long> {

    @Query("select p from Program p order by p.identifier")
    @Override
    public abstract SortedSet<Program> findAll();

    @Query("select distinct p from Cruise c right join c.programs p where c.identifier = ?1 order by p.identifier")
    public abstract SortedSet<Program> findByCruiseIdentifier(String identifier);

    @Query("select distinct p from Cruise c right join c.programs p left join c.platform pl left join pl.term plt where plt.identifier = ?1 order by p.identifier")
    public abstract SortedSet<Program> findByVesselIdentifier(String vesselIdentifier);

    @Query("select distinct p from Cruise c right join c.programs p where ?1 between c.startDate and c.endDate order by p.identifier")
    public abstract SortedSet<Program> findAtDate(OffsetDateTime at);

    @Query("select p from Program p where p.id = ?1")
    public abstract Optional<Program> findById(long id);

    @Query("select p from Program p where p.identifier= ?1")
    public abstract Program findByIdentifier(String identifier);

    @Modifying
    @Transactional
    @Query("delete from Program p where p.id = ?1")
    public abstract void deleteById(String id);

    @Modifying
    @Transactional
    @Query("delete from Program p where p.identifier=?1")
    public void deleteByIdentifier(String identifier);
}
