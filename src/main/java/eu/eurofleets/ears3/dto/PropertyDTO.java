/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.dto;

/**
 *
 * @author thomas
 */
public class PropertyDTO {

    public LinkedDataTermDTO key;
    public String value;
    public String uom;

    public PropertyDTO() {
    }

    public PropertyDTO(LinkedDataTermDTO key, String value, String uom) {
        this.key = key;
        this.value = value;
        this.uom = uom;
    }

}
