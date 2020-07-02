package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Harbour;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
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
public class HarbourService implements EarsService<Harbour> {

    private final HarbourRepository harbourRepository;

    @Autowired
    public HarbourService(HarbourRepository harbourRepository) {
        this.harbourRepository = harbourRepository;
    }

    @Override
    public List<Harbour> findAll() {
        return IterableUtils.toList(this.harbourRepository.findAll());
    }

    @Override
    public Harbour findById(long id) {
        Assert.notNull(id, "Harbour id must not be null");
        return (Harbour) this.harbourRepository.findById(id).orElse(null);
    }

    @Override
    public Harbour findByIdentifier(String identifier) {
        Assert.notNull(identifier, "Harbour identifier must not be null");
        Harbour r = idCache.get(identifier);
        if (r != null) {
            return r;
        } else {
            r = this.harbourRepository.findByIdentifier(identifier);
            idCache.put(identifier, r);
            return r;
        }
    }

    private static Map<String, Harbour> idCache = new HashMap<>();
    private static Map<String, Harbour> nameCache = new HashMap<>();

    @Override
    public Harbour findByName(String name) {
        Assert.notNull(name, "Harbour name must not be null");
        Harbour r = nameCache.get(name);
        if (r != null) {
            return r;
        } else {
            r = this.harbourRepository.findByName(name);
            nameCache.put(name, r);
            return r;
        }
    }

    @Override
    public void deleteById(String id) {
        this.harbourRepository.deleteById(Long.valueOf(id));
    }

    @Override
    public Map<String, Harbour> findAllByIdentifier(Set<String> identifiers) {
        return harbourRepository.findAllByIdentifier(identifiers).stream().collect(Collectors.toMap(v -> {
            return v.getTerm().getIdentifier();
        }, v -> v));
    }

    @Override
    public Iterable<Harbour> saveAll(Collection<Harbour> things) {
        Set<String> identifiers = things.stream().map(l -> l.getTerm().getIdentifier()).collect(Collectors.toSet());
        Map<String, Harbour> existingThings = findAllByIdentifier(identifiers);
        for (Harbour thing : things) {
            Harbour existingThing = existingThings.get(thing.getTerm().getIdentifier());
            if (existingThing != null) {
                Long id = existingThing.getId();
                thing.setId(id);
            }
        }

        return harbourRepository.saveAll(things);
    }
}
