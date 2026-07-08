package eu.eurofleets.ears3.domain;

import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;

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

    @XmlElementWrapper(name = "messages")
    @XmlElement(name = "message")
    public List<String> messages;

    public Message() {
    }

    public Message(int code, String identifier, E object, String message, String exceptionType) {
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
