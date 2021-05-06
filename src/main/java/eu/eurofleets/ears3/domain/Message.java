package eu.eurofleets.ears3.domain;

import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlSeeAlso({EventDTO.class, CruiseDTO.class, ProgramDTO.class})
@XmlAccessorType(XmlAccessType.FIELD)
public class Message<E> {

    /**
     * The error message.
     */
    public String message;

    /**
     * The http status code.
     */
    public int code;

    /**
     * The identifier of the created entity
     */
    public String identifier;

    /**
     * The error exceptionType.
     */
    public String exceptionType;

    @XmlAnyElement(lax = true)
    public E object;

    public Message() {
    }

    public Message(String message, int code, String identifier, String exceptionType, E object) {
        this.message = message;
        this.code = code;
        this.identifier = identifier;
        this.exceptionType = exceptionType;
        this.object = object;
    }

    public Message(int code, String identifier, E object) {
        this.code = code;
        this.identifier = identifier;
        this.object = object;
    }

}
