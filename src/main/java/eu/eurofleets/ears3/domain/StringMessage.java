package eu.eurofleets.ears3.domain;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Message")
@XmlAccessorType(XmlAccessType.FIELD)
public class StringMessage extends Message<String> {

    public StringMessage() {
    }

    public StringMessage(String message, int code, String identifier, String exceptionType) {
        this.message = message;
        this.code = code;
        this.identifier = identifier;
        this.exceptionType = exceptionType;
        this.object = null;
    }

}
