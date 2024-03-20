package eu.eurofleets.ears3.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement(name="error")
public class ErrorDTO {
    public int row;
    public String message;

    @JsonIgnore
    @XmlTransient
    public Exception e;

    public ErrorDTO(){

    }

    public ErrorDTO(int row, String msg, Exception e) {
        this.row = row;
        this.message = msg;
        this.e = e;
    }

    public ErrorDTO(String msg, Exception e){
        this.message = msg;
        this.e = e;
    }
}
