package eu.eurofleets.ears3.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
