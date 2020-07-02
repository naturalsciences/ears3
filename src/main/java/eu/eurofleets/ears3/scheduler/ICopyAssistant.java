/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler;

import be.naturalsciences.bmdc.cruise.model.IConcept;
import eu.eurofleets.ears3.AbstractConcept;

/**
 *
 * @author thomas
 */
interface ICopyAssistant {

    public void copy(AbstractConcept sourceConcept, IConcept targetConcept);
}
