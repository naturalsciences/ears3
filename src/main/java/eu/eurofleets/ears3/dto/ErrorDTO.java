package eu.eurofleets.ears3.dto;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ErrorDTO {
    public int row;
    public String message;

    @JsonIgnore
    @XmlTransient
    public Exception e;

    public ErrorDTO(int row, String msg, Exception e) {
        this.row = row + 1;
        this.message = msg;
        this.e = e;
    }
}
