/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler.vocabulary;

import be.naturalsciences.bmdc.cruise.model.IConcept;
import eu.eurofleets.ears3.AbstractConcept;
import eu.eurofleets.ears3.domain.Country;
import eu.eurofleets.ears3.domain.Harbour;
import eu.eurofleets.ears3.service.CountryService;
import gnu.trove.map.hash.THashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Thomas Vandenberghe
 */
public class HarbourCopyAssistant implements ICopyAssistant {

    CountryService countryService;

    public HarbourCopyAssistant(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public boolean isExcluded(AbstractConcept concept) {
        return false;
    }

    public static Map getJsonKeyVals(Map<String, String> keyVals, String json) {
        for (String key : keyVals.keySet()) {
            String pattern = "(\"" + key + "\"): *\"(.*?)\""; //json style
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(json);
            if (m.find()) {
                keyVals.put(key, m.group(2));
            } else {
                pattern = "<" + key + ">(.*?)<\\/" + key + ">"; //xml style
                p = Pattern.compile(pattern);
                m = p.matcher(json);
                if (m.find()) {
                    keyVals.put(key, m.group(1));
                }
            }
        }
        return keyVals;
    }

    public void copy(AbstractConcept concept, Harbour harbour) {
        Map<String, String> keyVal = new THashMap();
        String definitionEn = concept.getDefinitionEn();
        if (definitionEn != null) {
            keyVal.put("country", "");
            keyVal = getJsonKeyVals(keyVal, definitionEn);

        }
        if (!keyVal.get("country").equals("")) {
            String countryName = keyVal.get("country");
            Country country = countryService.findByName(countryName);
            harbour._setCountry(country);
        }
    }

    @Override
    public void copy(AbstractConcept sourceConcept, IConcept targetConcept) {
        copy(sourceConcept, (Harbour) targetConcept);
    }
}
