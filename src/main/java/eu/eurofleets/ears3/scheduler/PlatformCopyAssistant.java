/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler;

import be.naturalsciences.bmdc.cruise.model.IConcept;
import eu.eurofleets.ears3.AbstractConcept;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Platform;
import gnu.trove.map.hash.THashMap;
import java.util.Map;

/**
 *
 * @author thomas
 */
public class PlatformCopyAssistant implements ICopyAssistant {

    public void copy(AbstractConcept concept, Platform platform) {
        Map<String, String> keyVal = new THashMap();
        if (concept.broadMatch() != null && concept.broadMatch().size() > 0) {
            String platformClass = concept.broadMatch().get(0);
            platform.setPlatformClass(new LinkedDataTerm(platformClass, null, null));
        }
    }

    @Override
    public void copy(AbstractConcept sourceConcept, IConcept targetConcept) {
        copy(sourceConcept, (Platform) targetConcept);
    }

}
