package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Program;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public abstract interface ProgramRepository
        extends CrudRepository<Program, Long> {

    @Query("select p from Program p left join p.cruises c order by p.identifier")
    @Override
    public abstract List<Program> findAll();

    @Query("select p from Program p left join p.cruises c where c.name = ?1")
    public abstract List<Program> findByCruiseIdentifier(String identifier);

    @Query("select p from Program p left join p.cruises c where c.id = ?1")
    public abstract List<Program> findByCruiseId(long param);

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
