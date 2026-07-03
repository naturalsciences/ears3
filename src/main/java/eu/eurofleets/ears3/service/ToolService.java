package eu.eurofleets.ears3.service;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.dto.ToolDTO;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class ToolService implements EarsService<Tool> {

    private final ToolRepository toolRepository;

    private static final Map<String, Tool> idCache = new HashMap<>();
    private static final Map<String, Tool> nameCache = new HashMap<>();

    public static void clearCache() {
        idCache.clear();
        nameCache.clear();
    }

    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    public ToolService(ToolRepository toolRepository, LinkedDataTermRepository linkedDataTermRepository) {
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
        String identifier = tool.getTerm().getIdentifier();
        Tool existingTool = findByIdentifier(identifier);
        if (existingTool == null) { //create
            String urn = ILinkedDataTerm.getUrnFromUrl(identifier);
            tool.getTerm().setUrn(urn);
            return toolRepository.saveAndFlush(tool);
        } else { //find
            existingTool.setParentTool(tool.getParentTool());
            return toolRepository.saveAndFlush(existingTool);
        }
    }
}
