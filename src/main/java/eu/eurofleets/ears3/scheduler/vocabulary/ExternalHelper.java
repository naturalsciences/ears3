/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler.vocabulary;

import be.naturalsciences.bmdc.cruise.model.IConcept;
import eu.eurofleets.ears3.AbstractConcept;
import eu.eurofleets.ears3.ExternalRetriever;
import eu.eurofleets.ears3.domain.Country;
import eu.eurofleets.ears3.domain.Harbour;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.domain.SeaArea;
import eu.eurofleets.ears3.domain.Tool;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import wsimport.uk.ac.nerc.vocab.types.CollectionMembers;
import wsimport.uk.ac.nerc.vocab.types.Concept;
import wsimport.uk.ac.nerc.vocab.types.ConceptCollection;


/**
 *
 * @author Thomas Vandenberghe
 */
public class ExternalHelper<C extends IConcept> implements IExternalHelper {

    static final Map<Class, String[]> CLS = new HashMap();

    static {
        CLS.put(Country.class, new String[]{"http://vocab.nerc.ac.uk/collection/C32/current/"});
        CLS.put(Harbour.class, new String[]{"http://vocab.nerc.ac.uk/collection/C38/current/"});
        CLS.put(LinkedDataTerm.class, new String[]{"http://vocab.nerc.ac.uk/collection/P02/current/", "http://vocab.nerc.ac.uk/collection/C77/current/", "http://vocab.nerc.ac.uk/collection/L06/current/"});
        CLS.put(Platform.class, new String[]{"http://vocab.nerc.ac.uk/collection/C17/current/"});
        CLS.put(SeaArea.class, new String[]{"http://vocab.nerc.ac.uk/collection/C19/current/","http://vocab.nerc.ac.uk/collection/C37/current/"});
        CLS.put(Tool.class, new String[]{"http://vocab.nerc.ac.uk/collection/L22/current/"});
    }

    private ConceptCollection retrieved;
    private int size;
    private Class<C> cls;
    private final ICopyAssistant copyAssistant;

    public Collection<? extends AbstractConcept> getExternalConcepts() {
        return retrieved.getMembers().getConcept();
    }

    public ExternalHelper(Class<C> cls, ICopyAssistant copyAssistant) {
        this.cls = cls;
        this.retrieved = new ConceptCollection();
        this.copyAssistant = copyAssistant;

        CollectionMembers members = new CollectionMembers();
        this.retrieved.setMembers(members);
        for (String url : CLS.get(cls)) {
            ConceptCollection result = ExternalRetriever.getCollection(url);
            this.retrieved.getMembers().getConcept().addAll(result.getMembers().getConcept());
        }
        size = retrieved.getMembers().getConcept().size();
    }

    public Map<String, C> retrieve() {
        return bodcConceptToEARS(retrieved);
    }

    private Map<String, C> bodcConceptToEARS(ConceptCollection thisColl) {
        Map<String, C> results = new HashMap();
        if (thisColl.getMembers() != null) {
            for (Concept concept : thisColl.getMembers().getConcept()) {
                if (copyAssistant == null || (copyAssistant != null && !copyAssistant.isExcluded(concept))) {
                    String identifier = concept.getUri();
                    String urn = concept.getIdentifier();
                    String prefLabel = concept.getPrefLabelEn();
                    LinkedDataTerm ldt = new LinkedDataTerm(identifier, null, prefLabel);
                    ldt.setUrn(urn);
                    try {
                        C c = cls.newInstance();
                        c.setTerm(ldt);
                        if (copyAssistant != null) {
                            copyAssistant.copy(concept, c);
                        }
                        results.put(identifier, c);
                    } catch (InstantiationException ex) {
                        Logger.getLogger(ExternalHelper.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(ExternalHelper.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return results;
    }
}
