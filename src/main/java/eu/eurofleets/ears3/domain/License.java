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
 * @author Thomas Vandenberghe
 */
@Entity
public class License implements ILicense, Serializable {

    private static final License CCL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::CC", null, "collection cost charge"));
    private static final License FEL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::FE", null, "commercial charge"));
    private static final License LIL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::LI", null, "licence"));
    private static final License LSL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::LS", null, "SeaDataNet licence"));
    private static final License MOL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::MO", null, "moratorium"));
    private static final License NAL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::NA", null, "no access"));
    private static final License NCL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::NC", null, "distribution cost charge"));
    private static final License OGL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::OG", null, "organisation"));
    private static final License RSL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::RS", null, "by negotiation"));
    private static final License SRL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::SR", null, "academic"));
    private static final License UKL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::UK", null, "unknown"));
    private static final License UNL = new License(new LinkedDataTerm("https://www.seadatanet.org/urnurl/SDN:L08::UN", null, "unrestricted"));

    public enum Licenses {
        CC(CCL),
        FE(FEL),
        LI(LIL),
        LS(LSL),
        MO(MOL),
        NA(NAL),
        NC(NCL),
        OG(OGL),
        RS(RSL),
        SR(SRL),
        UK(UKL),
        UN(UNL);
        public License license;

        Licenses(License license) {
            this.license = license;
        }
    };
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
