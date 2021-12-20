package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.domain.Thermosal;
import eu.eurofleets.ears3.domain.Weather;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThermosalService {

    private final ThermosalRepository thermosalRepository;
    private final EntityManager entityManager;

    @Autowired
    public ThermosalService(ThermosalRepository thermosalRepository, EntityManager entityManager) {
        this.thermosalRepository = thermosalRepository;
        this.entityManager = entityManager;
    }

    public List<Thermosal> findAll() {
        return IterableUtils.toList(this.thermosalRepository.findAll());
    }

    public Thermosal findByInstrumentTime(OffsetDateTime date) {
        return (Thermosal) this.thermosalRepository.findByInstrumentTime(date);
    }

    public Thermosal findByTimestamp(OffsetDateTime date) {
        return (Thermosal) this.thermosalRepository.findByTimestamp(date);
    }

    public Map<OffsetDateTime, Thermosal> findAllByInstrumentTime(Set<OffsetDateTime> dates) {
        return thermosalRepository.findAllByInstrumentTime(dates).stream().collect(Collectors.toMap(v -> {
            return v.getInstrumentTime();
        }, v -> v));
    }

    public Iterable<Thermosal> saveAll(Collection<Thermosal> things) {
        if (things != null && !things.isEmpty()) {
            Set<OffsetDateTime> dates = things.stream().map(l -> l.getInstrumentTime()).collect(Collectors.toSet());
            Map<OffsetDateTime, Thermosal> existingThings = findAllByInstrumentTime(dates);
            for (Thermosal thing : things) {
                Thermosal existingThing = existingThings.get(thing.getInstrumentTime());
                if (existingThing != null) {
                    Long id = existingThing.getId();
                    thing.setId(id);
                }
            }
        }
        return thermosalRepository.saveAll(things);
    }

    public Thermosal findLast() {
        return (Thermosal) this.thermosalRepository.findLast();
    }

    public Thermosal findNearest(OffsetDateTime date) {
        Thermosal nav = (Thermosal) this.thermosalRepository.findByDate(date);
        if (nav == null) {
            return findByApproximateDate(date);
        } else {
            return nav;
        }
    }

    private Thermosal findByApproximateDate(OffsetDateTime date) {
        Query q = entityManager.createNamedQuery("findThermosalByApproximateDate", Thermosal.class);
        q.setParameter(1, date.toInstant());
        return (Thermosal) q.getSingleResult();
    }

    public Thermosal save(Thermosal n) {
        return this.thermosalRepository.save(n);
    }
}
