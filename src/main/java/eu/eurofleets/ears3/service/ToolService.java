package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import eu.eurofleets.ears3.dto.ToolDTO;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class ToolService implements EarsService<Tool> {

    private final ToolRepository toolRepository;

    @Autowired
    public ToolService(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
    }

    public List<Tool> findAll() {
        return IterableUtils.toList(this.toolRepository.findAll());
    }

    public Tool findById(long id) {
        Assert.notNull(id, "Tool id must not be null");
        return (Tool) this.toolRepository.findById(id).orElse(null);
    }

    public Tool findByIdentifier(String identifier) {
        Assert.notNull(identifier, "Tool identifier must not be null");
        Tool r = idCache.get(identifier);
        if (r != null) {
            return r;
        } else {
            r = this.toolRepository.findByIdentifier(identifier);
            idCache.put(identifier, r);
            return r;
        }
    }

    private static Map<String, Tool> idCache = new HashMap<>();
    private static Map<String, Tool> nameCache = new HashMap<>();

    public Tool findByName(String name) {
        Assert.notNull(name, "Tool name must not be null");
        Tool r = nameCache.get(name);
        if (r != null) {
            return r;
        } else {
            r = this.toolRepository.findByName(name);
            nameCache.put(name, r);
            return r;
        }
    }

    public void deleteById(String id) {
        this.toolRepository.deleteById(Long.valueOf(id));
    }

    public Map<String, Tool> findAllByIdentifier(Set<String> identifiers) {
        return toolRepository.findAllByIdentifier(identifiers).stream().collect(Collectors.toMap(v -> {
            return v.getTerm().getIdentifier();
        }, v -> v));
    }

    public Iterable<Tool> saveAll(Collection<Tool> things) {
        Set<String> identifiers = things.stream().map(l -> l.getTerm().getIdentifier()).collect(Collectors.toSet());
        Map<String, Tool> existingThings = findAllByIdentifier(identifiers);
        for (Tool thing : things) {
            Tool existingThing = existingThings.get(thing.getTerm().getIdentifier());
            if (existingThing != null) {
                Long id = existingThing.getId();
                thing.setId(id);
            }
        }

        return toolRepository.saveAll(things);
    }

    public Tool findOrCreate(Tool tool) {
        if (tool == null) {
            return null;
        }
        try {
            return toolRepository.save(tool);
        } catch (DataIntegrityViolationException | PersistenceException | ConstraintViolationException ex) { //
            return findByIdentifier(tool.getTerm().getIdentifier());
        }
    }

    public Tool findOrCreate(ToolDTO tool) {
        if (tool == null) {
            return null;
        }
        try {
            return toolRepository.save(new Tool(tool));
        } catch (DataIntegrityViolationException | PersistenceException | ConstraintViolationException ex) { //
            return findByIdentifier(tool.tool.identifier);
        }
    }

}
