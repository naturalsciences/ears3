package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Project;
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
public class ProjectService implements EarsService<Project> {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findAll() {
        return IterableUtils.toList(this.projectRepository.findAll());
    }

    public Project findById(long id) {
        Assert.notNull(id, "Project id must not be null");
        return (Project) this.projectRepository.findById(id).orElse(null);
    }

    public Project findByIdentifier(String identifier) {
        Assert.notNull(identifier, "Project identifier must not be null");
        Project r = idCache.get(identifier);
        if (r != null) {
            return r;
        } else {
            r = this.projectRepository.findByIdentifier(identifier);
            idCache.put(identifier, r);
            return r;
        }
    }

    private static Map<String, Project> idCache = new HashMap<>();
    private static Map<String, Project> nameCache = new HashMap<>();

    public Project findByName(String name) {
        Assert.notNull(name, "Project name must not be null");
        Project r = nameCache.get(name);
        if (r != null) {
            return r;
        } else {
            r = this.projectRepository.findByName(name);
            nameCache.put(name, r);
            return r;
        }
    }

    public void deleteById(String id) {
        this.projectRepository.deleteById(Long.valueOf(id));
    }

    public Map<String, Project> findAllByIdentifier(Set<String> identifiers) {
        return projectRepository.findAllByIdentifier(identifiers).stream().collect(Collectors.toMap(v -> {
            return v.getTerm().getIdentifier();
        }, v -> v));
    }

    public Iterable<Project> saveAll(Collection<Project> things) {
        Set<String> identifiers = things.stream().map(l -> l.getTerm().getIdentifier()).collect(Collectors.toSet());
        Map<String, Project> existingThings = findAllByIdentifier(identifiers);
        for (Project thing : things) {
            Project existingThing = existingThings.get(thing.getTerm().getIdentifier());
            if (existingThing != null) {
                Long id = existingThing.getId();
                thing.setId(id);
            }
        }

        return projectRepository.saveAll(things);
    }
}
