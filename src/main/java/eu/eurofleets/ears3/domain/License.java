/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.domain;

import be.naturalsciences.bmdc.cruise.model.ILicense;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;

/**
 *
 * @author thomas
 */
@Entity
public class License implements ILicense, Serializable {

    @ManyToOne
    private LinkedDataTerm term;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public License(LinkedDataTerm term) {
        this.term = term;
    }

    @Override
    public ILinkedDataTerm getLicenseTerm() {
        return term;
    }

    @Override
    public void setLicenseTerm(ILinkedDataTerm licenseTerm) {
        this.term = (LinkedDataTerm) licenseTerm;
    }

    public License(ILinkedDataTerm licenseTerm) {
        this.term = (LinkedDataTerm) licenseTerm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
