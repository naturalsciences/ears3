/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler.vocabulary;

import be.naturalsciences.bmdc.cruise.model.IConcept;
import eu.eurofleets.ears3.AbstractConcept;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.service.LinkedDataTermService;
import gnu.trove.map.hash.THashMap;
import java.util.Map;

/**
 *
 * @author thomas
 */
public class PlatformCopyAssistant implements ICopyAssistant {

    LinkedDataTermService linkedDataTermService;

    public PlatformCopyAssistant(LinkedDataTermService linkedDataTermService) {
        this.linkedDataTermService = linkedDataTermService;
    }

    @Override
    public boolean isExcluded(AbstractConcept concept) {
        boolean excluded = true;
        String definitionEn = concept.getDefinitionEn();
        if (definitionEn.contains("decommissioned")) {
            return true;
        }
        if (definitionEn.contains("Belgium")
                || definitionEn.contains("Denmark")
                || definitionEn.contains("Estonia")
                || definitionEn.contains("Finland")
                || definitionEn.contains("France")
                || definitionEn.contains("Germany")
                || definitionEn.contains("Greece")
                || definitionEn.contains("Iceland")
                || definitionEn.contains("Ireland")
                || definitionEn.contains("Italy")
                || definitionEn.contains("Netherlands")
                || definitionEn.contains("Norway")
                || definitionEn.contains("Poland")
                || definitionEn.contains("Portugal")
                || definitionEn.contains("Romania")
                || definitionEn.contains("Slovenia")
                || definitionEn.contains("Spain")
                || definitionEn.contains("Sweden")
                || definitionEn.contains("United Kingdom")
                || definitionEn.contains("New Zealand")
                || definitionEn.contains("Canada")
                || definitionEn.contains("Greenland")) {
            excluded = false;
        }
        return excluded;
    }

    public static String getPlatformClass(AbstractConcept concept) {
        for (String broadMatch : concept.broadMatch()) {
            if (broadMatch.contains("L06")) {
                return broadMatch;
            }
        }
        return null;
    }

    public void copy(AbstractConcept concept, Platform platform) {
        Map<String, String> keyVal = new THashMap();
        if (concept.broadMatch() != null && concept.broadMatch().size() > 0) {
            String platformClassUrl = getPlatformClass(concept);
            if (platformClassUrl != null) {
                LinkedDataTerm platformClass = linkedDataTermService.findByIdentifier(platformClassUrl);
                platform.setPlatformClass(platformClass);
            }
        }
    }

    @Override
    public void copy(AbstractConcept sourceConcept, IConcept targetConcept) {
        copy(sourceConcept, (Platform) targetConcept);
    }
}
