package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Navigation;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class NavigationService {

    private final NavigationRepository navigationRepository;

    @Autowired
    public NavigationService(NavigationRepository navigationRepository) {
        this.navigationRepository = navigationRepository;
    }

    public List<Navigation> findAll() {
        return IterableUtils.toList(this.navigationRepository.findAll());
    }

    public Navigation findByDate(OffsetDateTime date) {
        return (Navigation) this.navigationRepository.findByDate(date);
    }

    public Map<Instant, Navigation> findAllByDate(Set<Instant> dates) {
        return navigationRepository.findAllByDate(dates).stream().collect(Collectors.toMap(v -> {
            return v.getInstrumentTime();
        }, v -> v));
    }

    public Iterable<Navigation> saveAll(Collection<Navigation> things) {
        Set<Instant> dates = things.stream().map(l -> l.getInstrumentTime()).collect(Collectors.toSet());
        Map<Instant, Navigation> existingThings = findAllByDate(dates);
        for (Navigation thing : things) {
            Navigation existingThing = existingThings.get(thing.getInstrumentTime());
            if (existingThing != null) {
                Long id = existingThing.getId();
                thing.setId(id);
            }
        }

        return navigationRepository.saveAll(things);
    }
}
