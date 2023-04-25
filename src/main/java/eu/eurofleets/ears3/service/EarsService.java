/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.service;

import be.naturalsciences.bmdc.cruise.model.IConcept;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface EarsService<S extends IConcept> {

    void deleteById(String id);

    List<S> findAll();

    Map<String, S> findAllByIdentifier(Set<String> identifiers);

    S findById(long id);

    S findByIdentifier(String identifier);

    S findByName(String name);

    Iterable<S> saveAll(Collection<S> things);

}
