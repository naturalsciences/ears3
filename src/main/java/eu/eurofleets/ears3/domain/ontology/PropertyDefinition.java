package eu.eurofleets.ears3.domain.ontology;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "ontology_property")
public class PropertyDefinition extends EarsTermDefinitionBase {

    private boolean mandatory;

    private boolean multiple;

    /** e.g. "Subject", "Parameter", "Program" - nullable, matches EARSOntologyCreator.saveProperties() special cases. */
    private String valueClass;

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public String getValueClass() {
        return valueClass;
    }

    public void setValueClass(String valueClass) {
        this.valueClass = valueClass;
    }
}
