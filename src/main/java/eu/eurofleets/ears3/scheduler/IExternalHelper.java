/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler;

import be.naturalsciences.bmdc.cruise.model.IConcept;
import java.util.Map;

/**
 *
 * @author thomas
 */
interface IExternalHelper<C extends IConcept> {

    public Map<String, C> retrieve();
}
