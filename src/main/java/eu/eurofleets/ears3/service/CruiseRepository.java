package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Cruise;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.CrudRepository;
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

    @Query("select c from Cruise c left join Platform p on c.platform = p.id left join LinkedDataTerm l on l.id=p.term where l.identifier= ?1 or l.urn= ?1")
    public abstract List<Cruise> findByPlatformCode(String code);

    @Modifying
    @Transactional
    @Query("delete from Cruise c where c.startDate >= ?1  and c.endDate <= ?2")
    public abstract void deleteByDate(Date paramDate1, Date paramDate2);

    @Modifying
    @Transactional
    @Query("delete from Cruise c where c.identifier=?1")
    public abstract void deleteByIdentifier(String identifier);
}
