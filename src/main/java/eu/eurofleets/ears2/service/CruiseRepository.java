package eu.eurofleets.ears2.service;

import eu.eurofleets.ears2.domain.cruise.Cruise;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource(collectionResourceRel = "cruise", path = "cruise")
public abstract interface CruiseRepository
        extends JpaRepository<Cruise, Long> {

    public abstract Cruise findByCruiseId(String paramString);

    @Query("select from Cruise c where c.cruiseName = ?1")
    public abstract Cruise findByCruiseName(String cruiseName);

    public abstract List<Cruise> findByPlatformCode(String paramString);

    @Modifying
    @Transactional
    @Query("delete from Cruise c where c.cruiseId = ?1")
    public abstract void deleteCruiseByCruiseId(String paramString);

    @Modifying
    @Transactional
    @Query("delete from Cruise c where c.startDate >= ?1  and c.endDate <= ?2")
    public abstract void deleteCruiseByDate(Date paramDate1, Date paramDate2);
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/service/CruiseRepository.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
