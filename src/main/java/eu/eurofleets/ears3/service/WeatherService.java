package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Weather;
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
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final EntityManager entityManager;

    @Autowired
    public WeatherService(WeatherRepository weatherRepository, EntityManager entityManager) {
        this.weatherRepository = weatherRepository;
        this.entityManager = entityManager;
    }

    public List<Weather> findAll() {
        return IterableUtils.toList(this.weatherRepository.findAll());
    }

    public Weather findByInstrumentTime(OffsetDateTime date) {
        return (Weather) this.weatherRepository.findByInstrumentTime(date);
    }

    public Weather findByTimestamp(OffsetDateTime date) {
        return (Weather) this.weatherRepository.findByTimestamp(date);
    }

    public Map<OffsetDateTime, Weather> findAllByInstrumentTime(Set<OffsetDateTime> dates) {
        return weatherRepository.findAllByInstrumentTime(dates).stream().collect(Collectors.toMap(v -> {
            return v.getInstrumentTime();
        }, v -> v));
    }

    public Iterable<Weather> saveAll(Collection<Weather> things) {
        if (things != null && !things.isEmpty()) {
            Set<OffsetDateTime> dates = things.stream().map(l -> l.getInstrumentTime()).collect(Collectors.toSet());
            Map<OffsetDateTime, Weather> existingThings = findAllByInstrumentTime(dates);
            for (Weather thing : things) {
                Weather existingThing = existingThings.get(thing.getInstrumentTime());
                if (existingThing != null) {
                    Long id = existingThing.getId();
                    thing.setId(id);
                }
            }
        }
        return weatherRepository.saveAll(things);
    }

    public Weather findLast() {
        return (Weather) this.weatherRepository.findLast();
    }

    public Weather findNearest(OffsetDateTime date) {
        Weather nav = (Weather) this.weatherRepository.findByDate(date);
        if (nav == null) {
            return findByApproximateDate(date);
        } else {
            return nav;
        }
    }

    private Weather findByApproximateDate(OffsetDateTime date) {
        Query q = entityManager.createNamedQuery("findWeatherByApproximateDate", Weather.class);
        q.setParameter(1, date.toInstant());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } else {
            return (Weather) resultList.get(0);
        }
    }

    public Weather save(Weather n) {
        return this.weatherRepository.save(n);
    }
}
