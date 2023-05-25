package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Navigation;
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
public class NavigationService {

    private final NavigationRepository navigationRepository;
    private final EntityManager entityManager;

    @Autowired
    public NavigationService(NavigationRepository navigationRepository, EntityManager entityManager) {
        this.navigationRepository = navigationRepository;
        this.entityManager = entityManager;
    }

    public List<Navigation> findAll() {
        return IterableUtils.toList(this.navigationRepository.findAll());
    }

    public Navigation findByInstrumentTime(OffsetDateTime date) {
        return (Navigation) this.navigationRepository.findByInstrumentTime(date);
    }

    public Navigation findByTimestamp(OffsetDateTime date) {
        return (Navigation) this.navigationRepository.findByTimestamp(date);
    }

    public Map<OffsetDateTime, Navigation> findAllByInstrumentTime(Set<OffsetDateTime> dates) {
        return navigationRepository.findAllByInstrumentTime(dates).stream().collect(Collectors.toMap(v -> {
            return v.getInstrumentTime();
        }, v -> v));
    }

    public Iterable<Navigation> saveAll(Collection<Navigation> things) {
        if (things != null && !things.isEmpty()) {
            Set<OffsetDateTime> dates = things.stream().map(l -> l.getInstrumentTime()).collect(Collectors.toSet());
            Map<OffsetDateTime, Navigation> existingThings = findAllByInstrumentTime(dates);
            for (Navigation thing : things) {
                Navigation existingThing = existingThings.get(thing.getInstrumentTime());
                if (existingThing != null) {
                    Long id = existingThing.getId();
                    thing.setId(id);
                }
            }
        }

        return navigationRepository.saveAll(things);
    }

    public Navigation findLast() {
        return (Navigation) this.navigationRepository.findLast();
    }

    public Navigation findNearest(OffsetDateTime date) {
        Navigation nav = (Navigation) this.navigationRepository.findByDate(date);
        if (nav == null) {
            return findByApproximateDate(date);
        } else {
            return nav;
        }
    }

    private Navigation findByApproximateDate(OffsetDateTime date) {
        Query q = entityManager.createNamedQuery("findNavigationByApproximateDate", Navigation.class);
        q.setParameter(1, date);
        List<Navigation> resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return (Navigation) resultList.get(0);
        }
    }

    public Navigation save(Navigation n) {
        return this.navigationRepository.save(n);
    }
}
