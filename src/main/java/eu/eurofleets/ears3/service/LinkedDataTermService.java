package eu.eurofleets.ears3.service;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
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
        try {
            return ldtRepository.save(term);
        } catch (DataIntegrityViolationException | PersistenceException | ConstraintViolationException ex) { //
            return findByIdentifier(ILinkedDataTerm.cleanUrl(term.getIdentifier()));
        }
    }

    public LinkedDataTerm findOrCreate(LinkedDataTermDTO term) {
        if (term == null) {
            return null;
        }
        try {
            return ldtRepository.save(new LinkedDataTerm(term.identifier, term.transitiveIdentifier, term.name));
        } catch (DataIntegrityViolationException | PersistenceException | ConstraintViolationException ex) { //tried to save an already existing LinkedDataTerm without an id
            return findByIdentifier(ILinkedDataTerm.cleanUrl(term.identifier)); //it exists already so return it
        }
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
