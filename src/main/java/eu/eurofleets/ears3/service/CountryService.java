package eu.eurofleets.ears3.service;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import eu.eurofleets.ears3.domain.Country;
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
public class CountryService implements EarsService<Country> {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<Country> findAll() {
        return IterableUtils.toList(this.countryRepository.findAll());
    }

    public Country findById(long id) {
        Assert.notNull(id, "Country id must not be null");
        return (Country) this.countryRepository.findById(id).orElse(null);
    }

    public Country findByIdentifier(String identifier) {
        Assert.notNull(identifier, "Country identifier must not be null");
        identifier = ILinkedDataTerm.cleanUrl(identifier);
        Country r = idCache.get(identifier);
        if (r != null) {
            return r;
        } else {
            r = this.countryRepository.findByIdentifier(identifier);
            idCache.put(identifier, r);
            return r;
        }
    }

    private static Map<String, Country> idCache = new HashMap<>();
    private static Map<String, Country> nameCache = new HashMap<>();

    public Country findByName(String name) {
        Assert.notNull(name, "Country name must not be null");
        Country r = nameCache.get(name);
        if (r != null) {
            return r;
        } else {
            r = this.countryRepository.findByName(name);
            nameCache.put(name, r);
            return r;
        }
    }

    public void deleteById(String id) {
        this.countryRepository.deleteById(Long.valueOf(id));
    }

    public Map<String, Country> findAllByIdentifier(Set<String> identifiers) {
        return countryRepository.findAllByIdentifier(identifiers).stream().collect(Collectors.toMap(v -> {
            return v.getTerm().getIdentifier();
        }, v -> v));
    }

    public Iterable<Country> saveAll(Collection<Country> things) {
        Set<String> identifiers = things.stream().map(l -> l.getTerm().getIdentifier()).collect(Collectors.toSet());
        Map<String, Country> existingThings = findAllByIdentifier(identifiers);
        for (Country thing : things) {
            Country existingThing = existingThings.get(thing.getTerm().getIdentifier());
            if (existingThing != null) {
                Long id = existingThing.getId();
                thing.setId(id);
            }
        }

        return countryRepository.saveAll(things);
    }
}
