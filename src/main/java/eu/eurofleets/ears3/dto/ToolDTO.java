/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import eu.eurofleets.ears3.domain.LinkedDataTerm;

/**
 *
 * @author thomas
 */
public class ToolDTO {

    public LinkedDataTermDTO tool;
    public LinkedDataTermDTO parentTool;

    public ToolDTO() {
    }

    public ToolDTO(LinkedDataTermDTO tool, LinkedDataTermDTO parentTool) {
        this.tool = tool;
        this.parentTool = parentTool;
    }

    public ToolDTO(ILinkedDataTerm tool, ILinkedDataTerm parentTool) {
        this.tool = new LinkedDataTermDTO(tool);
        if (parentTool != null) {
            this.parentTool = new LinkedDataTermDTO(parentTool);
        }
    }

}
