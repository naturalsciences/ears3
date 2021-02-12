package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.SeaArea;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class SeaAreaService implements EarsService<SeaArea>{

    private final SeaAreaRepository seaAreaRepository;

    @Autowired
    public SeaAreaService(SeaAreaRepository seaAreaRepository) {
        this.seaAreaRepository = seaAreaRepository;
    }

    public List<SeaArea> findAll() {
        return IterableUtils.toList(this.seaAreaRepository.findAll());
    }

    public SeaArea findById(long id) {
        Assert.notNull(id, "SeaArea id must not be null");
        return (SeaArea) this.seaAreaRepository.findById(id).orElse(null);
    }

    public SeaArea findByIdentifier(String identifier) {
        Assert.notNull(identifier, "SeaArea identifier must not be null");
        SeaArea r = idCache.get(identifier);
        if (r != null) {
            return r;
        } else {
            r = this.seaAreaRepository.findByIdentifier(identifier);
            idCache.put(identifier, r);
            return r;
        }
    }

    private static Map<String, SeaArea> idCache = new HashMap<>();
    private static Map<String, SeaArea> nameCache = new HashMap<>();

    public SeaArea findByName(String name) {
        Assert.notNull(name, "SeaArea name must not be null");
        SeaArea r = nameCache.get(name);
        if (r != null) {
            return r;
        } else {
            r = this.seaAreaRepository.findByName(name);
            nameCache.put(name, r);
            return r;
        }
    }

    public void deleteById(String id) {
        this.seaAreaRepository.deleteById(Long.valueOf(id));
    }

    public Map<String, SeaArea> findAllByIdentifier(Set<String> identifiers) {
        return seaAreaRepository.findAllByIdentifier(identifiers).stream().collect(Collectors.toMap(v -> {
            return v.getTerm().getIdentifier();
        }, v -> v));
    }

    public Iterable<SeaArea> saveAll(Collection<SeaArea> things) {
        Set<String> identifiers = things.stream().map(l -> l.getTerm().getIdentifier()).collect(Collectors.toSet());
        Map<String, SeaArea> existingThings = findAllByIdentifier(identifiers);
        for (SeaArea thing : things) {
            SeaArea existingThing = existingThings.get(thing.getTerm().getIdentifier());
            if (existingThing != null) {
                Long id = existingThing.getId();
                thing.setId(id);
            }
        }

        return seaAreaRepository.saveAll(things);
    }
}
