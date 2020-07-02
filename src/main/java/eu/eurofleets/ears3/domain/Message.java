package eu.eurofleets.ears3.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Message {

    public static final long RESPONSE_OK = 0L;
    public static final long RESPONSE_NOK = -1L;
    private String code;
    private String description;

    public Message() {
    }

    public Message(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/domain/message/Message.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */
