/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ISamplingEvent;
import java.io.Serializable;
import javax.persistence.Entity;

/**
 *
 * @author thomas
 */
@Entity
public class SamplingEvent extends Event implements ISamplingEvent, Serializable {

}
