/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler.vocabulary;

import be.naturalsciences.bmdc.cruise.model.IConcept;
import eu.eurofleets.ears3.AbstractConcept;
import eu.eurofleets.ears3.domain.SeaArea;
import static eu.eurofleets.ears3.scheduler.vocabulary.HarbourCopyAssistant.getJsonKeyVals;
import gnu.trove.map.hash.THashMap;
import java.util.Map;

/**
 *
 * @author thomas
 */
public class SeaAreaCopyAssistant implements ICopyAssistant {

    public SeaAreaCopyAssistant() {
    }

    @Override
    public void copy(AbstractConcept sourceConcept, IConcept targetConcept) {
        copy(sourceConcept, (SeaArea) targetConcept);
    }

    private Double getBound(Map<String, String> keyVal, String key) {
        if (!keyVal.get(key).equals("")) {
            String s = keyVal.get(key);
            try {
                return Double.valueOf(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public void copy(AbstractConcept concept, SeaArea seaArea) {
        Map<String, String> keyVal = new THashMap();
        String definitionEn = concept.getDefinitionEn();
        if (definitionEn != null) {
            keyVal.put("Southernmost_latitude", "");
            keyVal.put("Northernmost_latitude", "");
            keyVal.put("Westernmost_longitude", "");
            keyVal.put("Easternmost_longitude", "");
            keyVal = getJsonKeyVals(keyVal, definitionEn);

        }
        try {
            seaArea.setSouthBoundLatitude(getBound(keyVal, "Southernmost_latitude"));
            seaArea.setNorthBoundLatitude(getBound(keyVal, "Northernmost_latitude"));
            seaArea.setWestBoundLongitude(getBound(keyVal, "Westernmost_longitude"));
            seaArea.setEastBoundLongitude(getBound(keyVal, "Easternmost_longitude"));
        } catch (Exception e) {
            int a = 5;
        }
    }

    @Override
    public boolean isExcluded(AbstractConcept concept
    ) {
        return false;
    }

}
