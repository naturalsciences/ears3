package eu.eurofleets.ears3.dto;

import eu.eurofleets.ears3.domain.Cruise;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Thomas Vandenberghe
 */
@XmlRootElement(name = "errors")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorDTOList {

    @XmlElement(name = "error")
    private List<ErrorDTO> errors = new ArrayList<>();

    public ErrorDTOList() {
    }

    public ErrorDTOList(List<ErrorDTO> errors) {
        this.errors = errors;
    }

    public List<ErrorDTO> getErrors() {
        return this.errors;
    }

    public void setErrors(List<ErrorDTO> errors) {
        this.errors = errors;
    }

    public void addError(ErrorDTO error) {
        this.errors.add(error);
    }
}