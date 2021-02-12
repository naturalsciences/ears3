package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.ITool;
import eu.eurofleets.ears3.dto.ToolDTO;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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

}
