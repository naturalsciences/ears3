/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.ontology;

/**
 *
 * @author Thomas Vandenberghe
 */
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "http://www.eurofleets.eu/", name = "message")
public class MessageBean implements Serializable, IResponseMessage {

    /**
     * The id of the entity that was just created.
     */
    private String code;

    private String description;

    private int status;

    public MessageBean() {
    }

    public MessageBean(String code, int status, String description) {
        this.code = code;
        this.description = description;
        this.status = status;
    }

    /**
     * The id of the entity that was just created.
     *
     * @return
     */
    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * A description of the web service action that was just performed.
     *
     * @return
     */
    @XmlElement(namespace = "http://www.eurofleets.eu/", name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getSummary() {
        return getDescription() + (getCode() != null ? (": identifier " + getCode()) : "");
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean isBad() {
        return false;
    }
}
