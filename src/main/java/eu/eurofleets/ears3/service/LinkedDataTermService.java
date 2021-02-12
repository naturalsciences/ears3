package eu.eurofleets.ears3.service;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class LinkedDataTermService {
    
    private LinkedDataTermRepository ldtRepository;
    
    @Autowired
    public LinkedDataTermService(LinkedDataTermRepository ldtRepository) {
        this.ldtRepository = ldtRepository;
    }
    
    public LinkedDataTerm findByIdentifier(String identifier) {
        Assert.notNull(identifier, "LinkedDataTerm identifier must not be null");
        return this.ldtRepository.findByIdentifier(ILinkedDataTerm.cleanUrl(identifier));
    }
    
    public LinkedDataTerm findOrCreate(LinkedDataTerm term) {
        if (term == null) {
            return null;
        }
        LinkedDataTerm existingTerm = findByIdentifier(term.getIdentifier());
        if (existingTerm == null) {
            return ldtRepository.save(term);
        } else {
            return existingTerm;
        }
    }
    
    public LinkedDataTerm findOrCreate(LinkedDataTermDTO term) {
        if (term == null) {
            return null;
        }
        String urn = ILinkedDataTerm.getUrnFromUrl(term.identifier);
        LinkedDataTerm linkedDataTerm = new LinkedDataTerm(term.identifier, term.transitiveIdentifier, term.name);
        linkedDataTerm.setUrn(urn);
        return findOrCreate(linkedDataTerm);
    }
    
    public Map<String, LinkedDataTerm> findAllByIdentifier(Set<String> identifiers) {
        return ldtRepository.findAllByIdentifier(identifiers).stream().collect(Collectors.toMap(LinkedDataTerm::getIdentifier, v -> v));
    }
    
    public Iterable<LinkedDataTerm> saveAll(Collection<LinkedDataTerm> terms) {
        Set<String> identifiers = terms.stream().map(l -> l.getIdentifier()).collect(Collectors.toSet());
        Map<String, LinkedDataTerm> existingTerms = findAllByIdentifier(identifiers);
        for (LinkedDataTerm term : terms) {
            LinkedDataTerm existingTerm = existingTerms.get(term.getIdentifier());
            if (existingTerm != null) {
                Long id = existingTerm.getId();
                term.setId(id);
            }
        }
        
        return ldtRepository.saveAll(terms);
    }
    
}
