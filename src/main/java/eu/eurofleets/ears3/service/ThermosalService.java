package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Thermosal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThermosalService {

    private final ThermosalRepository thermosalRepository;

    @Autowired
    public ThermosalService(ThermosalRepository thermosalRepository) {
        this.thermosalRepository = thermosalRepository;
    }

    public List<Thermosal> findAll() {
        return IterableUtils.toList(this.thermosalRepository.findAll());
    }

    public Thermosal findByDate(OffsetDateTime date) {
        return (Thermosal) this.thermosalRepository.findByDate(date);
    }

    public Map<Instant, Thermosal> findAllByDate(Set<Instant> dates) {
        return thermosalRepository.findAllByDate(dates).stream().collect(Collectors.toMap(v -> {
            return v.getInstrumentTime();
        }, v -> v));
    }

    public Iterable<Thermosal> saveAll(Collection<Thermosal> things) {
        if (things != null && !things.isEmpty()) {
            Set<Instant> dates = things.stream().map(l -> l.getInstrumentTime()).collect(Collectors.toSet());
            Map<Instant, Thermosal> existingThings = findAllByDate(dates);
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
}
