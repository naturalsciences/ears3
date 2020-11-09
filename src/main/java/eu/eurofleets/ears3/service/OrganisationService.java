package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Organisation;
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
public class OrganisationService implements EarsService<Organisation> {

    private final OrganisationRepository organisationRepository;

    @Autowired
    public OrganisationService(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    public List<Organisation> findAll() {
        return IterableUtils.toList(this.organisationRepository.findAll());
    }

    public Organisation findById(long id) {
        Assert.notNull(id, "Organisation id must not be null");
        return (Organisation) this.organisationRepository.findById(id).orElse(null);
    }

    public Organisation findByIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }
        Organisation r = idCache.get(identifier);
        if (r != null) {
            return r;
        } else {
            r = this.organisationRepository.findByIdentifier(identifier);
            idCache.put(identifier, r);
            return r;
        }
    }

    private static Map<String, Organisation> idCache = new HashMap<>();
    private static Map<String, Organisation> nameCache = new HashMap<>();

    public Organisation findByName(String name) {
        Assert.notNull(name, "Organisation name must not be null");
        Organisation r = nameCache.get(name);
        if (r != null) {
            return r;
        } else {
            r = this.organisationRepository.findByName(name);
            nameCache.put(name, r);
            return r;
        }
    }

    public void deleteById(String id) {
        this.organisationRepository.deleteById(Long.valueOf(id));
    }

    public Map<String, Organisation> findAllByIdentifier(Set<String> identifiers) {
        return organisationRepository.findAllByIdentifier(identifiers).stream().collect(Collectors.toMap(v -> {
            return v.getTerm().getIdentifier();
        }, v -> v));
    }

    public Iterable<Organisation> saveAll(Collection<Organisation> things) {
        Set<String> identifiers = things.stream().map(l -> l.getTerm().getIdentifier()).collect(Collectors.toSet());
        Map<String, Organisation> existingThings = findAllByIdentifier(identifiers);
        for (Organisation thing : things) {
            Organisation existingThing = existingThings.get(thing.getTerm().getIdentifier());
            if (existingThing != null) {
                Long id = existingThing.getId();
                thing.setId(id);
            }
        }

        return organisationRepository.saveAll(things);
    }
}
