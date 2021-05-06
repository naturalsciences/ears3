/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain.ears2;

import be.naturalsciences.bmdc.cruise.model.IPerson;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author thomas
 */
class ActorBean {

    private final IPerson person;

    public ActorBean(IPerson person) {
        this.person = person;
    }

    public IPerson getPerson() {
        return person;
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "firstName")
    public String getFirstName() {
        return person.getFirstName();
    }

    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "lastName")
    public String getLastName() {
        return person.getLastName();
    }

}
