package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Weather;
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
public class WeatherService {

    private final WeatherRepository weatherRepository;

    @Autowired
    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public List<Weather> findAll() {
        return IterableUtils.toList(this.weatherRepository.findAll());
    }

    public Weather findByDate(OffsetDateTime date) {
        return (Weather) this.weatherRepository.findByDate(date);
    }

    public Map<Instant, Weather> findAllByDate(Set<Instant> dates) {
        return weatherRepository.findAllByDate(dates).stream().collect(Collectors.toMap(v -> {
            return v.getInstrumentTime();
        }, v -> v));
    }

    public Iterable<Weather> saveAll(Collection<Weather> things) {
        Set<Instant> dates = things.stream().map(l -> l.getInstrumentTime()).collect(Collectors.toSet());
        Map<Instant, Weather> existingThings = findAllByDate(dates);
        for (Weather thing : things) {
            Weather existingThing = existingThings.get(thing.getInstrumentTime());
            if (existingThing != null) {
                Long id = existingThing.getId();
                thing.setId(id);
            }
        }

        return weatherRepository.saveAll(things);
    }
}
