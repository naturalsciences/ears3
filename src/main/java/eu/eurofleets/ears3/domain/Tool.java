package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IProperty;
import be.naturalsciences.bmdc.cruise.model.ITool;
import eu.eurofleets.ears3.dto.ToolDTO;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thomas
 */
@Entity
@Table(
        uniqueConstraints
        = @UniqueConstraint(columnNames = {"term_id", "parent_term_id"})
)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) //ignore all the getters
public class Tool implements ITool, Serializable {

    @OneToOne(optional = false)
    private LinkedDataTerm term;
    @OneToOne(optional = true)
    private LinkedDataTerm parentTerm;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*private OffsetDateTime beginPosition;
    private OffsetDateTime endPosition;*/
    @Transient
    private String serialNumber;
    @Transient
    private LinkedDataTerm toolCategory;
    /*@OneToMany()
    @XmlElementWrapper(name = "characteristics")
    @XmlElement(name = "characteristic")*/
    @Transient
    private Collection<Property> characteristics;
    /*@OneToMany()
    @XmlElementWrapper(name = "capabilities")
    @XmlElement(name = "capability")*/
    @Transient
    private Collection<Property> capabilities;
    /*@OneToMany()
    @XmlElementWrapper(name = "measuredParameters")
    @XmlElement(name = "measuredParameter")*/
    @Transient
    private Collection<LinkedDataTerm> measuredParameters;

    public Tool() {
    }

    public Tool(ILinkedDataTerm thisTool, ILinkedDataTerm parentTerm) {
        this.term = (LinkedDataTerm) thisTool;
        this.parentTerm = (LinkedDataTerm) parentTerm;
    }

    public Tool(ToolDTO toolDTO) {
        this.term = new LinkedDataTerm(toolDTO.tool.identifier, toolDTO.tool.transitiveIdentifier, toolDTO.tool.name);
        if (toolDTO.parentTool != null) {
            this.parentTerm = new LinkedDataTerm(toolDTO.parentTool.identifier, toolDTO.parentTool.transitiveIdentifier, toolDTO.parentTool.name);
        }
    }

    @Override
    public ILinkedDataTerm getTerm() {
        return term;
    }

    @Override
    public void setTerm(ILinkedDataTerm thisTool) {
        this.term = (LinkedDataTerm) thisTool;
    }

    @Override
    public ILinkedDataTerm getParentTool() {
        return parentTerm;
    }

    @Override
    public void setParentTool(ILinkedDataTerm parentTerm) {
        this.parentTerm = (LinkedDataTerm) parentTerm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*  public OffsetDateTime getBeginPosition() {
        return beginPosition;
    }

    public void setBeginPosition(OffsetDateTime beginPosition) {
        this.beginPosition = beginPosition;
    }

    public OffsetDateTime getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(OffsetDateTime endPosition) {
        this.endPosition = endPosition;
    }*/
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public ILinkedDataTerm getToolCategory() {
        return toolCategory;
    }

    public void setToolCategory(ILinkedDataTerm toolCategory) {
        this.toolCategory = (LinkedDataTerm) toolCategory;
    }

    @Override
    public Collection<Property> getCharacteristics() {
        return this.characteristics;
    }

    @Override
    public Collection<Property> getCapabilities() {
        return this.capabilities;
    }

    @Override
    public Collection<LinkedDataTerm> getMeasuredParameters() {
        return this.measuredParameters;
    }

    @Override
    public void setCharacteristics(Collection<? extends IProperty> characteristics) {
        this.characteristics = (Collection<Property>) characteristics;
    }

    @Override
    public void setCapabilities(Collection<? extends IProperty> capabilities) {
        this.capabilities = (Collection<Property>) capabilities;
    }

    @Override
    public void setMeasuredParameters(Collection<? extends ILinkedDataTerm> measuredParameters) {
        this.measuredParameters = (Collection<LinkedDataTerm>) measuredParameters;
    }
}
