package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Platform;
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
public class PlatformService implements EarsService<Platform> {

    private final PlatformRepository platformRepository;

    @Autowired
    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    public List<Platform> findAll() {
        return IterableUtils.toList(this.platformRepository.findAll());
    }

    public Platform findById(long id) {
        Assert.notNull(id, "Platform id must not be null");
        return this.platformRepository.findById(id).orElse(null);
    }

    public Platform findByIdentifier(String identifier) {
        Assert.notNull(identifier, "Platform identifier must not be null");
        return this.platformRepository.findByIdentifier(identifier);
    }

    private static Map<String, Platform> idCache = new HashMap<>();
    private static Map<String, Platform> nameCache = new HashMap<>();

    public Platform findByName(String name) {
        Assert.notNull(name, "Organisation name must not be null");
        Platform r = nameCache.get(name);
        if (r != null) {
            return r;
        } else {
            r = this.platformRepository.findByName(name);
            nameCache.put(name, r);
            return r;
        }
    }

    /*public int createPlatform(PlatformDTO dto) {
        Platform p = new Platform();
        
        p.setPlatformClass(new LinkedDataTerm(dto.platformClass));
        return this.platformRepository.save(cruise).getId();
    }*/
    public void deleteById(String id) {
        this.platformRepository.deleteById(Long.valueOf(id));
    }

    public Map<String, Platform> findAllByIdentifier(Set<String> identifiers) {
        return platformRepository.findAllByIdentifier(identifiers).stream().collect(Collectors.toMap(v -> {
            return v.getTerm().getIdentifier();
        }, v -> v));
    }

    public Iterable<Platform> saveAll(Collection<Platform> things) {
        Set<String> identifiers = things.stream().map(l -> l.getTerm().getIdentifier()).collect(Collectors.toSet());
        Map<String, Platform> existingThings = findAllByIdentifier(identifiers);
        for (Platform thing : things) {
            Platform existingThing = existingThings.get(thing.getTerm().getIdentifier());
            if (existingThing != null) {
                Long id = existingThing.getId();
                thing.setId(id);
            }
        }

        return platformRepository.saveAll(things);
    }
}
