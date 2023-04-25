/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler.vocabulary;

import be.naturalsciences.bmdc.cruise.model.IConcept;
import java.util.Map;

/**
 *
 * @author Thomas Vandenberghe
 */
interface IExternalHelper<C extends IConcept> {

    public Map<String, C> retrieve();
}
