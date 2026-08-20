/*
 * To change this license header, choose ILicense Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.cruise.csr;

import be.naturalsciences.bmdc.cruise.model.ICoordinate;
import be.naturalsciences.bmdc.cruise.model.ICruise;
import be.naturalsciences.bmdc.cruise.model.IEvent;
import be.naturalsciences.bmdc.cruise.model.ILicense;
import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IOrganisation;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.ITool;
import be.naturalsciences.bmdc.cruise.csr.model.IKeyword;
import be.naturalsciences.bmdc.cruise.csr.model.Thesaurus;
//import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlElement;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.seadatanet.csr.net.opengis.gml.AbstractGeometryType;
import org.seadatanet.csr.net.opengis.gml.CodeType;
import org.seadatanet.csr.net.opengis.gml.CurvePropertyType;
import org.seadatanet.csr.net.opengis.gml.DirectPositionListType;
import org.seadatanet.csr.net.opengis.gml.LineStringType;
import org.seadatanet.csr.net.opengis.gml.MultiCurveType;
import org.seadatanet.csr.net.opengis.gml.StringOrRefType;
import org.seadatanet.csr.net.opengis.gml.TimePeriodType;
import org.seadatanet.csr.net.opengis.gml.TimePositionType;
import org.seadatanet.csr.org.isotc211._2005.gco.BooleanPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gco.CharacterStringPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gco.CodeListValueType;
import org.seadatanet.csr.org.isotc211._2005.gco.DatePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gco.DateTimePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gco.DecimalPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.AbstractDQElementType;
import org.seadatanet.csr.org.isotc211._2005.gmd.AbstractDQResultType;
import org.seadatanet.csr.org.isotc211._2005.gmd.AbstractEXGeographicExtentType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIAddressPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIAddressType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CICitationPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CICitationType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIContactPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIContactType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIDatePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIDateType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIDateTypeCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIOnlineResourcePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIOnlineResourceType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIResponsiblePartyPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIResponsiblePartyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CIRoleCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CITelephonePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.CITelephoneType;
import org.seadatanet.csr.org.isotc211._2005.gmd.DQConformanceResultType;
import org.seadatanet.csr.org.isotc211._2005.gmd.DQDataQualityPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.DQDataQualityType;
import org.seadatanet.csr.org.isotc211._2005.gmd.DQDomainConsistencyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.DQElementPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.DQResultPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.DQScopePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.DQScopeType;
import org.seadatanet.csr.org.isotc211._2005.gmd.EXBoundingPolygonType;
import org.seadatanet.csr.org.isotc211._2005.gmd.EXExtentPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.EXExtentType;
import org.seadatanet.csr.org.isotc211._2005.gmd.EXGeographicBoundingBoxType;
import org.seadatanet.csr.org.isotc211._2005.gmd.EXGeographicExtentPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.EXTemporalExtentPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.EXTemporalExtentType;
import org.seadatanet.csr.org.isotc211._2005.gmd.LILineagePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.LILineageType;
import org.seadatanet.csr.org.isotc211._2005.gmd.LanguageCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDBrowseGraphicPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDBrowseGraphicType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDCharacterSetCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDConstraintsPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDConstraintsType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDIdentificationPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDIdentifierPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDIdentifierType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDKeywordTypeCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDKeywordsPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDKeywordsType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDLegalConstraintsPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDLegalConstraintsType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDMetadataExtensionInformationPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDProgressCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDRestrictionCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDScopeCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDTopicCategoryCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmd.MDTopicCategoryCodeType;
import org.seadatanet.csr.org.isotc211._2005.gmd.URLPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIAcquisitionInformationPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIAcquisitionInformationType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIContextCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIEventPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIEventType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIInstrumentPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIInstrumentType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIMetadataType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIObjectivePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIObjectiveType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIObjectiveTypeCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIOperationPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIOperationType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIPlatformPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIPlatformType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MISequenceCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmi.MITriggerCodePropertyType;
import org.seadatanet.csr.org.isotc211._2005.gmx.AnchorType;
import org.seadatanet.csr.org.isotc211._2005.gss.GMObjectPropertyType;
import org.seadatanet.csr.org.isotc211._2005.gts.TMPrimitivePropertyType;
import org.seadatanet.csr.org.seadatanet.MDKeywordsPropertyTypeSDN;
import org.seadatanet.csr.org.seadatanet.MDKeywordsTypeSDN;
import org.seadatanet.csr.org.seadatanet.SDNCSRUnitCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNCountryCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNDataCategoryCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNDataIdentificationType;
import org.seadatanet.csr.org.seadatanet.SDNDeviceCategoryCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNEDMERPCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNEDMOCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNHierarchyLevelNameCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNKeyword;
import org.seadatanet.csr.org.seadatanet.SDNMarsdenCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNObjectiveType;
import org.seadatanet.csr.org.seadatanet.SDNParameterDiscoveryCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNPlatformCategoryCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNPlatformCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNPortCodePropertyType;
import org.seadatanet.csr.org.seadatanet.SDNSamplingActivityType;
import org.seadatanet.csr.org.seadatanet.SDNWaterBodyCodePropertyType;

/**
 *
 * @author thomas
 */
public class CSRBuilder {

    public static final Date GEMET_INSPIRE_THEMES_PUBLICATION_DATE = new GregorianCalendar(2008, Calendar.JUNE, 1)
            .getTime();
    public static final String GEMET_INSPIRE_THEMES_THESAURUS_URL = "http://www.eionet.europa.eu/gemet/inspire_themes";

    private DQDataQualityPropertyType getInspireDataQuality() {
        DQDataQualityPropertyType dqProp = gmd.createDQDataQualityPropertyType();
        DQDataQualityType dq = gmd.createDQDataQualityType();
        dqProp.setDQDataQuality(dq);
        MDScopeCodePropertyType scopeCode = new MDScopeCodePropertyType();
        scopeCode.setMDScopeCode(
                gmd.createMDScopeCode(getCodeListValue(REALM_ISO_GMX, "MD_ScopeCode", "series", "series")));
        DQScopePropertyType dqScopeProp = gmd.createDQScopePropertyType();
        DQScopeType dqScope = gmd.createDQScopeType();
        dqScopeProp.setDQScope(dqScope);
        dqScope.setLevel(scopeCode);
        dq.setScope(dqScopeProp);

        LILineagePropertyType lineageProp = gmd.createLILineagePropertyType();
        dq.setLineage(lineageProp);
        LILineageType lineage = gmd.createLILineageType();
        lineageProp.setLILineage(lineage);
        lineage.setStatement(getCS(
                "The data centres apply standard data quality control procedures on all data that the centres manage. Ask the data centre for details."));

        DQElementPropertyType dqElProp = gmd.createDQElementPropertyType();
        dq.getReport().add(dqElProp);
        //DQDomainConsistencyPropertyType domainConsistencyProp = gmd.createDQDomainConsistencyPropertyType();
        DQDomainConsistencyType domainConsistency = gmd.createDQDomainConsistencyType();
        //domainConsistencyProp.setDQDomainConsistency(domainConsistency);
        JAXBElement<AbstractDQElementType> abstractDQElement = gmd.createAbstractDQElement(domainConsistency);
        dqElProp.setAbstractDQElement(abstractDQElement);
        DQResultPropertyType resultProp = gmd.createDQResultPropertyType();
        domainConsistency.getResult().add(resultProp);
        DQConformanceResultType conformance = gmd.createDQConformanceResultType();
        JAXBElement<? extends AbstractDQResultType> abstractDQResult = gmd.createDQConformanceResult(conformance);
        resultProp.setAbstractDQResult(abstractDQResult);
        conformance.setExplanation(getCS("See the referenced specification"));
        conformance.setPass(getBoolean(true));
        conformance.setSpecification(getShortCitation(
                "COMMISSION REGULATION (EC) No 1205/2008 of 3 December 2008 implementing Directive 2007/2/EC of the European Parliament and of the Council as regards metadata",
                new GregorianCalendar(2008, Calendar.DECEMBER, 4).getTime(), Thesaurus.PUBLICATION));

        DQElementPropertyType dqElProp1089 = gmd.createDQElementPropertyType();
        dq.getReport().add(dqElProp1089);
        //DQDomainConsistencyPropertyType domainConsistencyProp = gmd.createDQDomainConsistencyPropertyType();
        DQDomainConsistencyType domainConsistency1089 = gmd.createDQDomainConsistencyType();
        //domainConsistencyProp.setDQDomainConsistency(domainConsistency);
        JAXBElement<AbstractDQElementType> abstractDQElement1089 = gmd.createAbstractDQElement(domainConsistency1089);
        dqElProp1089.setAbstractDQElement(abstractDQElement1089);
        DQResultPropertyType resultProp1089 = gmd.createDQResultPropertyType();
        domainConsistency1089.getResult().add(resultProp1089);
        DQConformanceResultType conformance1089 = gmd.createDQConformanceResultType();
        JAXBElement<? extends AbstractDQResultType> abstractDQResult1089 = gmd
                .createDQConformanceResult(conformance1089);
        resultProp1089.setAbstractDQResult(abstractDQResult1089);
        conformance1089.setExplanation(getCS("See the referenced specification"));
        conformance1089.setPass(getBoolean(true));
        conformance1089.setSpecification(getShortCitation(
                "COMMISSION REGULATION (EU) No 1089/2010 of 23 November 2010 implementing Directive 2007/2/EC of the European Parliament and of the Council as regards interoperability of spatial data sets and services",
                new GregorianCalendar(2008, Calendar.DECEMBER, 8).getTime(), Thesaurus.PUBLICATION));

        return dqProp;
    }

    private BooleanPropertyType getBoolean(boolean b) {
        BooleanPropertyType booleanProp = gco.createBooleanPropertyType();
        booleanProp.setBoolean(b);
        return booleanProp;
    }

    public enum Role {
        DISTRIBUTOR, RESOURCEPROVIDER, COAUTHOR, EDITOR, CONTRIBUTOR, OWNER, USER, STAKEHOLDER, RIGHTS_HOLDER, FUNDER,
        PUBLISHER, AUTHOR, POINT_OF_CONTACT, PRINCIPAL_INVESTIGATOR, MEDIATOR, PROCESSOR, ORIGINATOR, CUSTODIAN,
        COLLABORATOR, SPONSOR
    }

    public static org.seadatanet.csr.org.isotc211._2005.gmd.ObjectFactory gmd = new org.seadatanet.csr.org.isotc211._2005.gmd.ObjectFactory();
    public static org.seadatanet.csr.org.isotc211._2005.gmi.ObjectFactory gmi = new org.seadatanet.csr.org.isotc211._2005.gmi.ObjectFactory();
    public static org.seadatanet.csr.net.opengis.gml.ObjectFactory gml = new org.seadatanet.csr.net.opengis.gml.ObjectFactory();
    public static org.seadatanet.csr.org.isotc211._2005.gco.ObjectFactory gco = new org.seadatanet.csr.org.isotc211._2005.gco.ObjectFactory();
    public static org.seadatanet.csr.org.seadatanet.ObjectFactory sdn = new org.seadatanet.csr.org.seadatanet.ObjectFactory();
    public static org.seadatanet.csr.org.isotc211._2005.gmx.ObjectFactory gmx = new org.seadatanet.csr.org.isotc211._2005.gmx.ObjectFactory();
    public static org.seadatanet.csr.org.isotc211._2005.gts.ObjectFactory gts = new org.seadatanet.csr.org.isotc211._2005.gts.ObjectFactory();
    public static org.seadatanet.csr.org.isotc211._2005.gss.ObjectFactory gss = new org.seadatanet.csr.org.isotc211._2005.gss.ObjectFactory();

    public static String GMX_CODELISTS = "http://vocab.nerc.ac.uk/isoCodelists/sdnCodelists/gmxCodeLists.xml";
    public static String GMI_CODELISTS = "http://www.isotc211.org/2005/resources/Codelist/gmiCodelists.xml";
    public static String CDI_CSR_CODELISTS = "http://vocab.nerc.ac.uk/isoCodelists/sdnCodelists/cdicsrCodeList.xml";
    public static String EDMO_EDMERP_CODELISTS = "https://edmo.seadatanet.org/isocodelists/edmo-edmerp-codelists.xml";

    public static CodeListRealm REALM_ISO_GMX = new CodeListRealm("ISOTC211/19115", GMX_CODELISTS);
    public static CodeListRealm REALM_ISO_GMI = new CodeListRealm("ISOTC211/19115", GMI_CODELISTS);
    public static CodeListRealm REALM_SDN_EDMO_EDMERP = new CodeListRealm("SeaDataNet", EDMO_EDMERP_CODELISTS);
    public static CodeListRealm REALM_SDN_GMX = new CodeListRealm("SeaDataNet", GMX_CODELISTS);
    public static CodeListRealm REALM_SDN_CDI_CSR = new CodeListRealm("SeaDataNet", CDI_CSR_CODELISTS);

    public static Map<String, Class> KEYWORD_TYPES = new HashMap();

    public static Map<String, String> CODE_SPACES_URLS = new HashMap<>();
    public static Map<Role, CIRoleCodePropertyType> ROLES = new HashMap<>();
    public static MDKeywordsPropertyType OCEANOGRAPHIC_GEOGRAPHIC_FEATURES;

    public static Thesaurus THESAURUS_GEMET = new Thesaurus("GEMET - INSPIRE themes, version 1.0", null,
            GEMET_INSPIRE_THEMES_THESAURUS_URL, GEMET_INSPIRE_THEMES_PUBLICATION_DATE, null, Thesaurus.PUBLICATION);
    public static Thesaurus THESAURUS_C38 = new Thesaurus("SeaDataNet Ports Gazetteer", "C38", "",
            new GregorianCalendar(2017, Calendar.DECEMBER, 5).getTime(), "61", Thesaurus.REVISION);
    public static Thesaurus THESAURUS_C32 = new Thesaurus("International Standards Organisation countries", "C32", "",
            new GregorianCalendar(2020, Calendar.NOVEMBER, 18).getTime(), "10", Thesaurus.REVISION);
    public static Thesaurus THESAURUS_C17 = new Thesaurus("ICES Platform Codes", "C17", "",
            new GregorianCalendar(2018, Calendar.JUNE, 9).getTime(), "748", Thesaurus.REVISION);
    public static Thesaurus THESAURUS_L06 = new Thesaurus("SeaVoX Platform Categories", "L06", "",
            new GregorianCalendar(2018, Calendar.MARCH, 14).getTime(), "14", Thesaurus.REVISION);
    public static Thesaurus THESAURUS_EDMERP = new Thesaurus(
            "European Directory of Marine Environmental Research Projects", "EDMERP", "",
            new GregorianCalendar(2013, Calendar.APRIL, 16).getTime(), null, Thesaurus.REVISION);
    public static Thesaurus THESAURUS_C19 = new Thesaurus("SeaVoX salt and fresh water body gazetteer", "C19", "",
            new GregorianCalendar(2018, Calendar.FEBRUARY, 21).getTime(), "17", Thesaurus.REVISION);
    public static Thesaurus THESAURUS_P02 = new Thesaurus("SeaDataNet Parameter Discovery Vocabulary", "P02", "",
            new GregorianCalendar(2018, Calendar.APRIL, 5).getTime(), "108", Thesaurus.REVISION);
    public static Thesaurus THESAURUS_L05 = new Thesaurus("SeaDataNet device categories", "L05", "",
            new GregorianCalendar(2018, Calendar.APRIL, 7).getTime(), "64", Thesaurus.REVISION);
    public static Thesaurus THESAURUS_C37 = new Thesaurus("Ten-degree Marsden Squares", "C37", "",
            new GregorianCalendar(2009, Calendar.JANUARY, 9).getTime(), "3", Thesaurus.REVISION);
    public static ILicense UN;

    private ICruise cruise;

    private ILicense license;

    private List<String> defaultMessages = new ArrayList<>();

    public ICruise getCruise() {
        return cruise;
    }

    public ILicense getLicense() {
        return license;
    }

    public List<String> getDefaultMessages() {
        return defaultMessages;
    }

    public CSRBuilder(ICruise cruise, ILicense license, boolean fillErrorsWithDefaults)
            throws IllegalCSRArgumentException {

        if (cruise.getDataUrl() != null && (cruise.getDataUrl().equals("null") || cruise.getDataUrl().equals(""))) {
            cruise.setDataUrl(null);
        }
        if (cruise.getFinalReportUrl() != null
                && (cruise.getFinalReportUrl().equals("null") || cruise.getFinalReportUrl().equals(""))) {
            cruise.setFinalReportUrl(null);
        }
        if (cruise.getObjectives() != null
                && (cruise.getObjectives().equals("null") || cruise.getObjectives().equals(""))) {
            cruise.setObjectives(null);
        }
        if (cruise.getPlanningUrl() != null
                && (cruise.getPlanningUrl().equals("null") || cruise.getPlanningUrl().equals(""))) {
            cruise.setPlanningUrl(null);
        }
        if (cruise.getPurpose() != null && (cruise.getPurpose().equals("null") || cruise.getPurpose().equals(""))) {
            cruise.setPurpose(null);
        }
        if (cruise.getTrackGmlUrl() != null
                && (cruise.getTrackGmlUrl().equals("null") || cruise.getTrackGmlUrl().equals(""))) {
            cruise.setTrackGmlUrl(null);
        }
        if (cruise.getTrackImageUrl() != null
                && (cruise.getTrackImageUrl().equals("null") || cruise.getTrackImageUrl().equals(""))) {
            cruise.setTrackImageUrl(null);
        }

        List<String> invalidArguments = new ArrayList<>();
        if (cruise.getIsCancelled()) {
            invalidArguments.add("Can't build a CSR for a cancelled cruise.");
        }
        if (cruise.getIdentifier() == null) {
            invalidArguments.add("Cruise must have an identifier.");
        }
        if (cruise.getName() == null) {
            invalidArguments.add("Cruise must have a name.");
        }
        if (cruise.getStartDate() == null) {
            invalidArguments.add("Cruise must have a start date.");
        }
        if (cruise.getEndDate() == null) {
            invalidArguments.add("Cruise must have an end date.");
        }
        if (cruise.getObjectives() == null
                && !cruise.getPrograms().stream().anyMatch(p -> p.getDescription() != null)) {
            invalidArguments.add("Cruise or at least one of the programs must have an objective.");
        }
        if (cruise.getDepartureHarbour() == null) {
            invalidArguments.add("Cruise must have a departure harbour.");
        }
        if (cruise.getArrivalHarbour() == null) {
            invalidArguments.add("Cruise must have a arrival harbour.");
        }
        if (cruise.getDepartureHarbour().getTerm() == null
                && cruise.getDepartureHarbour().getTerm().getIdentifier() == null) {
            invalidArguments.add("Cruise must have a departure harbour with a valid identifier.");
        }
        if (cruise.getArrivalHarbour().getTerm() == null
                && cruise.getArrivalHarbour().getTerm().getIdentifier() == null) {
            invalidArguments.add("Cruise must have a arrival harbour with a valid identifier.");
        }
        if (cruise.getPlatform() == null) {
            invalidArguments.add("Cruise must have a platform.");
        }
        if (cruise.getChiefScientists() == null || cruise.getChiefScientists().isEmpty()) {
            invalidArguments.add("Cruise must have at least one Chief Scientist.");
        }
        if (cruise.getPlatform().getVesselOperator() == null) {
            invalidArguments.add("Platform must have a vessel operator.");
        }
        if (cruise.getCollateCentre() == null) {
            invalidArguments.add("Cruise must have a collate centre.");
        }
        if (cruise.getSeaAreas() == null || cruise.getSeaAreas().isEmpty()) {
            invalidArguments.add("Cruise must have one or more sea areas.");
        }
        if (cruise.getSouthBoundLatitude() == 0 || cruise.getNorthBoundLatitude() == 0
                || cruise.getWestBoundLongitude() == 0 || cruise.getEastBoundLongitude() == 0) {
            if (fillErrorsWithDefaults) {
                cruise.setSouthBoundLatitude(-90);
                cruise.setNorthBoundLatitude(90);
                cruise.setWestBoundLongitude(-180);
                cruise.setEastBoundLongitude(180);
                defaultMessages
                        .add("No valid bounding box provided. Bounding box filled with defaults!: -180, -90, 180, 90");
            } else {
                invalidArguments.add("Cruise must have a bounding box.");
            }
        }
        if (license == null) {
            invalidArguments.add("Cruise Summary report must receive a list of licenses.");
        }
        if (!invalidArguments.isEmpty()) {
            throw new IllegalCSRArgumentException(invalidArguments);
        }

        StringBuilder sb = new StringBuilder();
        if (cruise.getObjectives() == null) {
            for (IProgram p : cruise.getPrograms()) {
                if (p.getDescription() != null) {
                    sb.append(p.getDescription());
                    if (!p.getDescription().endsWith(".")) {
                        sb.append(".");
                    }
                    sb.append(" ");
                }
            }
            cruise.setObjectives(sb.toString().trim());
        }
        if (cruise.getObjectives().length() > 4000) {
            String objectives = cruise.getObjectives().substring(0, 3999);
            int lastDot = objectives.lastIndexOf(".") + 1;
            objectives = objectives.substring(0, lastDot);
            cruise.setObjectives(objectives.trim());
        }
        this.cruise = cruise;
        this.license = license;
        CIRoleCodePropertyType resourceProvider = new CIRoleCodePropertyType();
        resourceProvider
                .setCIRoleCode(getCodeListValue(REALM_ISO_GMX, "CI_RoleCode", "resourceProvider", "resourceProvider"));

        CIRoleCodePropertyType publisher = new CIRoleCodePropertyType();
        publisher.setCIRoleCode(getCodeListValue(REALM_ISO_GMX, "CI_RoleCode", "publisher", "publisher"));

        CIRoleCodePropertyType author = new CIRoleCodePropertyType();
        author.setCIRoleCode(getCodeListValue(REALM_ISO_GMX, "CI_RoleCode", "author", "author"));

        CIRoleCodePropertyType custodian = new CIRoleCodePropertyType();
        custodian.setCIRoleCode(getCodeListValue(REALM_ISO_GMX, "CI_RoleCode", "custodian", "custodian"));

        CIRoleCodePropertyType owner = new CIRoleCodePropertyType();
        owner.setCIRoleCode(getCodeListValue(REALM_ISO_GMX, "CI_RoleCode", "owner", "owner"));

        CIRoleCodePropertyType user = new CIRoleCodePropertyType();
        user.setCIRoleCode(getCodeListValue(REALM_ISO_GMX, "CI_RoleCode", "user", "user"));

        CIRoleCodePropertyType distributor = new CIRoleCodePropertyType();
        distributor.setCIRoleCode(getCodeListValue(REALM_ISO_GMX, "CI_RoleCode", "distributor", "distributor"));

        CIRoleCodePropertyType originator = new CIRoleCodePropertyType();
        originator.setCIRoleCode(getCodeListValue(REALM_ISO_GMX, "CI_RoleCode", "originator", "originator"));

        CIRoleCodePropertyType pointOfContact = new CIRoleCodePropertyType();
        pointOfContact
                .setCIRoleCode(getCodeListValue(REALM_ISO_GMX, "CI_RoleCode", "pointOfContact", "pointOfContact"));

        CIRoleCodePropertyType principalInvestigator = new CIRoleCodePropertyType();
        principalInvestigator.setCIRoleCode(
                getCodeListValue(REALM_ISO_GMX, "CI_RoleCode", "principalInvestigator", "principalInvestigator"));

        CIRoleCodePropertyType processor = new CIRoleCodePropertyType();
        processor.setCIRoleCode(getCodeListValue(REALM_ISO_GMX, "CI_RoleCode", "processor", "processor"));

        ROLES.put(Role.RESOURCEPROVIDER, resourceProvider);
        ROLES.put(Role.PUBLISHER, publisher);
        ROLES.put(Role.AUTHOR, author);
        ROLES.put(Role.CUSTODIAN, custodian);
        ROLES.put(Role.OWNER, owner);
        ROLES.put(Role.USER, user);
        ROLES.put(Role.DISTRIBUTOR, distributor);
        ROLES.put(Role.ORIGINATOR, originator);
        ROLES.put(Role.POINT_OF_CONTACT, pointOfContact);
        ROLES.put(Role.PRINCIPAL_INVESTIGATOR, principalInvestigator);
        ROLES.put(Role.PROCESSOR, processor);

        OCEANOGRAPHIC_GEOGRAPHIC_FEATURES = getKeyword(
                Arrays.asList(new String[]{"Oceanographic geographical features"}), "theme", THESAURUS_GEMET);

        KEYWORD_TYPES
                .put("departure_place", SDNPortCodePropertyType.class);
        KEYWORD_TYPES.put(
                "arrival_place", SDNPortCodePropertyType.class);
        KEYWORD_TYPES.put(
                "departure_country", SDNCountryCodePropertyType.class);
        KEYWORD_TYPES.put(
                "arrival_country", SDNCountryCodePropertyType.class);
        KEYWORD_TYPES.put(
                "platform", SDNPlatformCodePropertyType.class);
        KEYWORD_TYPES.put(
                "platform_class", SDNPlatformCategoryCodePropertyType.class);
        KEYWORD_TYPES.put(
                "project", SDNEDMERPCodePropertyType.class);
        KEYWORD_TYPES.put(
                "place", SDNWaterBodyCodePropertyType.class);
        KEYWORD_TYPES.put(
                "marsden_square", SDNMarsdenCodePropertyType.class);
        KEYWORD_TYPES.put(
                "parameter", SDNParameterDiscoveryCodePropertyType.class);
        KEYWORD_TYPES.put(
                "instrument", SDNDeviceCategoryCodePropertyType.class);
    }

    public JAXBElement<String> getJaxbCS(String string) {
        CharacterStringPropertyType cs = new CharacterStringPropertyType();
        JAXBElement<String> jaxbElement = gco.createCharacterString(string);
        return jaxbElement;
    }

    public CharacterStringPropertyType getCS(String string) {
        CharacterStringPropertyType cs = new CharacterStringPropertyType();
        JAXBElement<String> jaxbElement = gco.createCharacterString(string);
        cs.setCharacterString(jaxbElement);
        return cs;
    }

    public List<CharacterStringPropertyType> getCS(Collection<String> strings) {
        List<CharacterStringPropertyType> r = new ArrayList();
        for (String string : strings) {
            CharacterStringPropertyType cs = new CharacterStringPropertyType();
            JAXBElement<String> jaxbElement = gco.createCharacterString(string);
            cs.setCharacterString(jaxbElement);
            r.add(cs);
        }
        return r;
    }

    public CodeListValueType getCodeListValue(String codeSpace, String codeList, String codeListValue, String value) {
        CodeListValueType r = new CodeListValueType();
        r.setCodeSpace(codeSpace);
        r.setCodeList(codeList);
        r.setCodeListValue(codeListValue);

        r.setValue(value);
        return r;
    }

    public CodeListValueType getCodeListValue(CodeListRealm codeListRealm, String codeListHashtag, String codeListValue,
                                              String value) {
        String codespaceUrl = codeListRealm.codeList;
        return getCodeListValue(codeListRealm.codeSpace, codespaceUrl + "#" + codeListHashtag, codeListValue, value);
    }

    public CIResponsiblePartyPropertyType getResponsibleParty(IOrganisation organisation, Role roleCode) {
        if (organisation == null) {
            throw new IllegalArgumentException("Organisation must not be null.");
        }
        String phone = organisation._getPhoneNumber();
        String fax = organisation._getFaxNumber();
        String email = organisation._getEmailAddress();

        IPerson person = new IPerson() {
            @Override
            public String getFirstName() {
                return null;
            }

            @Override
            public String getLastName() {
                return null;
            }

            @Override
            public IOrganisation getOrganisation() {
                return organisation;
            }

            @Override
            public void setFirstName(String firstName) {

            }

            @Override
            public void setLastName(String lastName) {

            }

            @Override
            public void setOrganisation(IOrganisation organisation) {

            }

            @Override
            public String _getPhoneNumber() {
                return phone;
            }

            @Override
            public void _setPhoneNumber(String phoneNumber) {

            }

            @Override
            public String _getFaxNumber() {
                return fax;
            }

            @Override
            public void setFaxNumber(String faxNumber) {

            }

            @Override
            public String getEmail() {
                return email;
            }

            @Override
            public void setEmail(String email) {

            }

            @Override
            public String getFirstNameLastName() {
                return this.getFirstName() + " " + this.getLastName();
            }
        };
        return getResponsibleParty(person, roleCode);
    }

    public CIResponsiblePartyPropertyType getResponsibleParty(IPerson person, Role roleCode) {

        IOrganisation organisation = person.getOrganisation();
        String firstName = person.getFirstName();
        String lastName = person.getLastName();
        String phoneNumber = null;
        String faxNumber = null;
        String email = null;
        String organisationName = null;
        String organisationIdentifier = null;
        String deliveryPoint = null;
        String city = null;
        String postalCode = null;
        String country = null;
        String sdnCountryCode = null;
        String website = null;

        if (organisation != null) {
            organisationName = organisation.getTerm().getName();
            organisationIdentifier = ILinkedDataTerm
                    .getIdentifierFromNERCorSDNUrlOrUrn(organisation.getTerm().getIdentifier());
            deliveryPoint = organisation._getDeliveryPoint();
            city = organisation._getCity();
            postalCode = organisation._getPostalcode();
            country = organisation._getCountry().getTerm().getName();
            sdnCountryCode = ILinkedDataTerm
                    .getIdentifierFromNERCorSDNUrlOrUrn(organisation._getCountry().getTerm().getIdentifier());
            website = organisation._getWebsite();

            phoneNumber = organisation._getPhoneNumber();
            faxNumber = organisation._getFaxNumber();
            email = organisation._getEmailAddress();
        }
        phoneNumber = person._getPhoneNumber() != null ? person._getPhoneNumber() : phoneNumber;
        faxNumber = person._getFaxNumber() != null ? person._getFaxNumber() : faxNumber;
        email = person.getEmail() != null ? person.getEmail() : email;

        CIRoleCodePropertyType role = null;

        if (roleCode == null) {
            role = ROLES.get(Role.POINT_OF_CONTACT);
        } else {
            role = ROLES.get(roleCode);
        }

        CIResponsiblePartyType party = new CIResponsiblePartyType();
        if (organisationName != null) {
            CodeListValueType edmoCode = getCodeListValue(REALM_SDN_EDMO_EDMERP, "SDN_EDMOCode", organisationIdentifier,
                    organisationName);
            SDNEDMOCodePropertyType sdnEdmoCode = new SDNEDMOCodePropertyType();
            sdnEdmoCode.set(edmoCode);
            party.setOrganisationName(sdnEdmoCode);
        }
        if (firstName != null && lastName != null) {
            party.setIndividualName(getCS(firstName + " " + lastName));
        }
        if (phoneNumber != null || faxNumber != null || deliveryPoint != null || city != null || postalCode != null
                || sdnCountryCode != null || website != null) {
            CIContactPropertyType contactProp = new CIContactPropertyType();
            CIContactType contact = new CIContactType();
            CITelephonePropertyType phoneProp = new CITelephonePropertyType();

            CITelephoneType phone = new CITelephoneType();
            phone.getVoice().add(getCS(phoneNumber));
            phone.getFacsimile().add(getCS(faxNumber));
            phoneProp.setCITelephone(phone);
            contact.setPhone(phoneProp);
            contactProp.setCIContact(contact);
            CIAddressPropertyType addProp = new CIAddressPropertyType();
            CIAddressType add = new CIAddressType();
            addProp.setCIAddress(add);
            contact.setAddress(addProp);
            if (deliveryPoint != null || city != null || postalCode != null || sdnCountryCode != null) {
                if (deliveryPoint != null) {
                    add.getDeliveryPoint().add(getCS(deliveryPoint));
                }
                if (city != null) {
                    add.setCity(getCS(city));
                }
                if (postalCode != null) {
                    add.setPostalCode(getCS(postalCode));
                }
                if (sdnCountryCode != null) {
                    CodeListValueType countryCode = getCodeListValue(REALM_SDN_CDI_CSR, "SDN_CountryCode", country,
                            sdnCountryCode);
                    SDNCountryCodePropertyType countryCodeSdn = new SDNCountryCodePropertyType();
                    countryCodeSdn.set(countryCode);
                    add.setCountry(countryCodeSdn);
                }
            }
            if (email != null) {
                add.getElectronicMailAddress().add(getCS(email));
            }
            if (website != null) {
                CIOnlineResourcePropertyType onlineResourceProp = new CIOnlineResourcePropertyType();
                CIOnlineResourceType onlineResource = new CIOnlineResourceType();
                URLPropertyType urlProp = new URLPropertyType();
                urlProp.setURL(website);
                onlineResource.setLinkage(urlProp);
                onlineResourceProp.setCIOnlineResource(onlineResource);
                contact.setOnlineResource(onlineResourceProp);
            }
            party.setContactInfo(contactProp);
            party.setRole(role);
        }
        CIResponsiblePartyPropertyType partyType = new CIResponsiblePartyPropertyType();
        partyType.setCIResponsibleParty(party);
        return partyType;
    }

    public MDIdentifierPropertyType getIdentifier(String idString) {
        MDIdentifierPropertyType idProp = new MDIdentifierPropertyType();
        MDIdentifierType id = new MDIdentifierType();
        id.setCode(getCS(idString));
        JAXBElement<MDIdentifierType> mdIdentifier = gmd.createMDIdentifier(id);
        idProp.setMDIdentifier(mdIdentifier);
        return idProp;
    }

    public String getCSRIdentifier() {
        return "urn:SDN:CSR:LOCAL:" + cruise.getIdentifier().replaceAll("[ !\"§$%&\\/\\\\()=?]", "_");
    }

    public String getCSRIdentifierWithDots() {
        return "urn.SDN.CSR.LOCAL." + cruise.getIdentifier().replaceAll("[ !\"§$%&\\/\\\\()=?]", "_");
    }

    private class RealmAndHashTag {

        String codeListHashTag;
        CodeListRealm realm;

        public RealmAndHashTag(String codeListHashTag, CodeListRealm realm) {
            this.codeListHashTag = codeListHashTag;
            this.realm = realm;
        }

    }

    private RealmAndHashTag SDNKeywordTypeToRealmAndHashTag(String type) {
        Class<SDNKeyword> sdnKeywordClass = KEYWORD_TYPES.get(type);
        if (sdnKeywordClass != null) {
            String codeListHashTag = null;
            try {

                Field field = sdnKeywordClass.getDeclaredField("code");
                XmlElement x = field.getAnnotation(XmlElement.class);
                codeListHashTag = x.name();
            } catch (NoSuchFieldException ex) {
                Logger.getLogger(CSRBuilder.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(CSRBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
            CodeListRealm realm = null;
            if (codeListHashTag.equals("SDN_EDMERPCode")) {
                realm = REALM_SDN_EDMO_EDMERP;
            } else {
                realm = REALM_SDN_CDI_CSR;
            }
            return new RealmAndHashTag(codeListHashTag, realm);
        }
        return null;
    }

    /**
     * *
     * This works but the final order of the these keywords is below
     * additionalDocumentation which is not correct.
     *
     * @param <S>
     * @param keywords
     * @param type
     * @param thesaurus
     * @return
     */
    public <S extends SDNKeyword> MDKeywordsPropertyTypeSDN getSDNKeyword(
            Collection<? extends ILinkedDataTerm> keywords, String type, Thesaurus thesaurus) {
        MDKeywordsPropertyTypeSDN individualKeywordProp = null;
        if (keywords != null && !keywords.isEmpty()) {

            MDKeywordsTypeSDN individualKeyword = new MDKeywordsTypeSDN();
            individualKeyword.setThesaurusName(getThesaurusCitation(thesaurus));
            MDKeywordTypeCodePropertyType keywordTypeProp = new MDKeywordTypeCodePropertyType();
            CodeListValueType keywordType = getCodeListValue(REALM_SDN_GMX, "MD_KeywordTypeCode", type, type);
            keywordTypeProp.setMDKeywordTypeCode(keywordType);
            individualKeyword.setType(keywordTypeProp);

            RealmAndHashTag realmAndHashTag = SDNKeywordTypeToRealmAndHashTag(type);
            Class<S> sdnKeywordClass = KEYWORD_TYPES.get(type);
            S sdnKeyword = null;
            try {
                sdnKeyword = sdnKeywordClass.newInstance();
            } catch (InstantiationException ex) {
                Logger.getLogger(CSRBuilder.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(CSRBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
            for (ILinkedDataTerm keyword : keywords) {
                sdnKeyword.set(getCodeListValue(realmAndHashTag.realm, realmAndHashTag.codeListHashTag,
                        ILinkedDataTerm.getIdentifierFromNERCorSDNUrlOrUrn(keyword.getIdentifier()),
                        keyword.getName()));
                individualKeyword.getKeyword().add(sdnKeyword);

            }
            individualKeywordProp.setMDKeywords(individualKeyword);

        }
        return individualKeywordProp;
    }

    public MDKeywordsPropertyType getAnchorKeyword(Collection<? extends ILinkedDataTerm> keywords, String type,
                                                   Thesaurus thesaurus) {
        if (keywords != null && !keywords.isEmpty()) {
            MDKeywordsPropertyType individualKeywordProp = new MDKeywordsPropertyType();
            MDKeywordsType individualKeyword = new MDKeywordsType();

            RealmAndHashTag realmAndHashTag = SDNKeywordTypeToRealmAndHashTag(type);
            for (ILinkedDataTerm keyword : keywords) {
                String url = null;
                if ("instrument".equals(type) && keyword.getTransitiveIdentifier() != null) {
                    url = keyword.getTransitiveIdentifier();
                } else {
                    url = keyword.getIdentifier();
                }
                String text = keyword.getName();
                AnchorType anchor = new AnchorType();

                anchor.setHref(ILinkedDataTerm.getIdentifierFromNERCorSDNUrlOrUrn(url));
                anchor.setValue(text);
                anchor.setRole(realmAndHashTag.realm.codeList + "#" + realmAndHashTag.codeListHashTag);

                JAXBElement<AnchorType> a = gmx.createAnchor(anchor);
                CharacterStringPropertyType c = new CharacterStringPropertyType();
                c.setCharacterString(a);

                individualKeyword.getKeyword().add(c);
            }
            if (thesaurus != null && !thesaurus.equals(Thesaurus.NO_THESAURUS)) {
                individualKeyword.setThesaurusName(getThesaurusCitation(thesaurus));
            }
            CodeListValueType keywordType = getCodeListValue(REALM_SDN_GMX, "MD_KeywordTypeCode", type, type);
            MDKeywordTypeCodePropertyType keywordTypeProp = new MDKeywordTypeCodePropertyType();
            keywordTypeProp.setMDKeywordTypeCode(keywordType);
            individualKeyword.setType(keywordTypeProp);
            individualKeywordProp.setMDKeywords(individualKeyword);
            return individualKeywordProp;
        }
        return null;
    }

    public MDKeywordsPropertyType getKeyword(Collection<String> keywords, String type, Thesaurus thesaurus) {
        MDKeywordsType individualKeyword = new MDKeywordsType();
        MDKeywordsPropertyType individualKeywordProp = new MDKeywordsPropertyType();
        individualKeyword.getKeyword().addAll(getCS(keywords));

        if (thesaurus != null && !thesaurus.equals(Thesaurus.NO_THESAURUS)) {
            individualKeyword.setThesaurusName(getThesaurusCitation(thesaurus));
        }
        CodeListValueType keywordType = getCodeListValue(REALM_SDN_GMX, "MD_KeywordTypeCode", type, type);
        MDKeywordTypeCodePropertyType keywordTypeProp = new MDKeywordTypeCodePropertyType();
        keywordTypeProp.setMDKeywordTypeCode(keywordType);
        individualKeyword.setType(keywordTypeProp);
        individualKeywordProp.setMDKeywords(individualKeyword);
        return individualKeywordProp;
    }

    public Collection<MDKeywordsPropertyType> getKeywords(Collection<IKeyword> keywords) {
        Collection<MDKeywordsPropertyType> allKeywords = new LinkedHashSet<>();
        if (keywords != null && !keywords.isEmpty()) {
            Map<Thesaurus, List<IKeyword>> intKeywords = new HashMap<>(); //order by thesaurus
            for (IKeyword keyword : keywords) {
                Thesaurus thesaurus = keyword.getThesaurus();
                List<IKeyword> get = intKeywords.get(thesaurus);
                if (get != null) {
                    get.add(keyword);
                    intKeywords.put(thesaurus, get);
                } else {
                    List<IKeyword> subKeywords = new ArrayList<>();
                    subKeywords.add(keyword);
                    intKeywords.put(thesaurus, subKeywords);
                }
            }
            for (Map.Entry<Thesaurus, List<IKeyword>> entry : intKeywords.entrySet()) {
                Thesaurus thesaurus = entry.getKey();
                List<IKeyword> keywordList = entry.getValue();
                for (IKeyword iKeyword : keywordList) {
                    String prefLabel = iKeyword.getPrefLabel();
                    String type = iKeyword.getType();
                    allKeywords.add(getKeyword(Arrays.asList(new String[]{prefLabel}), type, thesaurus));
                }
            }
        }
        return allKeywords;
    }

    private MDBrowseGraphicPropertyType getBrowseGraphic(String url, String description, String fileType) {
        MDBrowseGraphicPropertyType browseGraphicProp = gmd.createMDBrowseGraphicPropertyType();
        MDBrowseGraphicType browseGraphic = gmd.createMDBrowseGraphicType();
        browseGraphicProp.setMDBrowseGraphic(browseGraphic);
        browseGraphic.setFileDescription(getCS(description));
        browseGraphic.setFileName(getCS(url));
        browseGraphic.setFileType(getCS(fileType));
        return browseGraphicProp;
    }

    private MIEventPropertyType getEvent(String identifier, CodeListValueType trigger, CodeListValueType context,
                                         CodeListValueType sequence, OffsetDateTime dateTime) {

        MIEventPropertyType eventProp = gmi.createMIEventPropertyType();
        MIEventType event = gmi.createMIEventType();
        event.setId(identifier);
        event.setIdentifier(getIdentifier(identifier));
        MITriggerCodePropertyType triggerProp = gmi.createMITriggerCodePropertyType();
        triggerProp.setMITriggerCode(trigger);
        MIContextCodePropertyType contextProp = gmi.createMIContextCodePropertyType();
        contextProp.setMIContextCode(context);
        MISequenceCodePropertyType sequenceProp = gmi.createMISequenceCodePropertyType();
        sequenceProp.setMISequenceCode(sequence);
        event.setTrigger(triggerProp);
        event.setContext(contextProp);
        event.setSequence(sequenceProp);

        event.setTime(getDateTime(dateTime));
        eventProp.setMIEvent(event);
        return eventProp;
    }

    private MIOperationPropertyType getCruiseOperation() {
        MIOperationPropertyType operationProp = gmi.createMIOperationPropertyType();
        MIOperationType operation = gmi.createMIOperationType();
        operation.setId(getCSRIdentifierWithDots());
        operation.setDescription(getCS(cruise.getObjectives()));
        operation.setCitation(getShortCruiseCitation());
        MDProgressCodePropertyType progressCodeProp = gmd.createMDProgressCodePropertyType();
        progressCodeProp
                .setMDProgressCode(getCodeListValue(REALM_SDN_GMX, "MD_ProgressCode", "completed", "completed"));
        operation.setStatus(progressCodeProp);
        CodeListValueType trigger = getCodeListValue(REALM_ISO_GMI, "MI_TriggerCode", "manual", "manual");
        CodeListValueType context = getCodeListValue(REALM_ISO_GMI, "MI_ContextCode", "wayPoint", "wayPoint");
        CodeListValueType sequenceStart = getCodeListValue(REALM_ISO_GMI, "MI_SequenceCode", "start", "start");
        CodeListValueType sequenceEnd = getCodeListValue(REALM_ISO_GMI, "MI_SequenceCode", "end", "end");
        operation.getSignificantEvent()
                .add(getEvent("cruise-start", trigger, context, sequenceStart, cruise.getStartDate()));
        operation.getSignificantEvent().add(getEvent("cruise-end", trigger, context, sequenceEnd, cruise.getEndDate()));
        operationProp.setMIOperation(operation);
        return operationProp;
    }

    //group all events by their tool models and sampling/deployment date and expose a grouping of all events for that day
    private MIObjectivePropertyType getEventObjective(CSRObjective cSRobjective, OffsetDateTime day) {
        String toolName = cSRobjective.getTool().getTerm().getName();
        String toolIdentifier = ILinkedDataTerm
                .getIdentifierFromNERCorSDNUrlOrUrn(cSRobjective.getTool().getTerm().getIdentifier());
        String c77Identifier = ILinkedDataTerm
                .getIdentifierFromNERCorSDNUrlOrUrn(cSRobjective.getPurpose().getIdentifier());
        String c77Name = cSRobjective.getPurpose().getName();
        String objectiveIdentifier = getCSRIdentifierWithDots() + ".objective." + c77Identifier;
        String eventIdentifier = getCSRIdentifierWithDots() + ".event." + c77Identifier;
        Set<String> objectives = cSRobjective.getEvents().stream().map(IEvent::getDescription)
                .collect(Collectors.toSet());
        String functionName = c77Name + " with " + toolName;
        String objective1 = (String) objectives.toArray()[0];
        if (objective1 != null && objective1.equals("-") || (objectives.size() == 1 && cSRobjective.getEvents().size() > 1)) { //if all the events have the same description and there is more than 1 event, then set the function to the descriptions.
            functionName = (String) objective1;
        }

        String sensingInstrumentIdentifier = c77Identifier;//getCSRIdentifierWithDots() + ".objective." + c77Identifier + ".instrument." + toolIdentifier;
        String instituteIdentifier = cSRobjective.getPi() == null ? null
                : "SDN:EDMO::" + ILinkedDataTerm.getIdentifierFromNERCorSDNUrlOrUrn(
                cSRobjective.getPi().getOrganisation().getTerm().getIdentifier());
        String instituteName = cSRobjective.getPi() == null ? null
                : cSRobjective.getPi().getOrganisation().getTerm().getName();

        String platformIdentifier = c77Identifier + "/" + ILinkedDataTerm
                .getIdentifierFromNERCorSDNUrlOrUrn(cSRobjective.getPi().getOrganisation().getTerm().getIdentifier());
        String platformDescription = c77Name + ", conducted by " + instituteName;
        if (objectives.toArray()[0].equals("-") || (objectives.size() == 1 && cSRobjective.getEvents().size() > 1)) { //if all the events have the same description and there is more than 1 event, then set the function to the descriptions.
            platformDescription = (String) objectives.toArray()[0];
        }
        MIObjectivePropertyType objectiveProp = gmi.createMIObjectivePropertyType();
        SDNObjectiveType objective = sdn.createSDNObjectiveType();
        final JAXBElement<MIObjectiveType> MIObjective = gmi.createMIObjective(objective);
        objectiveProp.setMIObjective(MIObjective);

        objective.setId(objectiveIdentifier); //event identifier; example uses "objective-instrument-7-B18-1478"

        List<MDIdentifierPropertyType> identifiers = objective.getIdentifier();
        identifiers.add(getIdentifier(objectiveIdentifier)); //event identifier; example uses "objective-instrument-7-B18-1478"

        final List<MIObjectiveTypeCodePropertyType> types = objective.getType();
        final MIObjectiveTypeCodePropertyType MIObjectiveTypeCodePropertyType = gmi
                .createMIObjectiveTypeCodePropertyType();
        if (cSRobjective.getProcessName().equals("Deployment")) {
            MIObjectiveTypeCodePropertyType.setMIObjectiveTypeCode(
                    getCodeListValue(REALM_ISO_GMI, "MI_ObjectiveTypeCode", "persistentView", "persistentView"));
        } else {
            MIObjectiveTypeCodePropertyType.setMIObjectiveTypeCode(getCodeListValue(REALM_ISO_GMI,
                    "MI_ObjectiveTypeCode", "instantaneousCollection", "instantaneousCollection"));
        }
        types.add(MIObjectiveTypeCodePropertyType);

        final List<CharacterStringPropertyType> functions = objective.getFunction();
        functions.add(getCS(functionName)); //tool name; example uses "macrobenthos - Van Veen grab"

        final MIInstrumentPropertyType sensingInstrumentProperty = gmi.createMIInstrumentPropertyType();
        final MIInstrumentType sensingInstrument = gmi.createMIInstrumentType();
        sensingInstrument.setId(sensingInstrumentIdentifier);
        sensingInstrument.setIdentifier(getIdentifier(sensingInstrumentIdentifier));

        final SDNDataCategoryCodePropertyType sDNDataCategoryCodePropertyType = sdn
                .createSDNDataCategoryCodePropertyType();
        sDNDataCategoryCodePropertyType.setSDNDataCategoryCode(
                getCodeListValue(REALM_SDN_CDI_CSR, "SDN_DataCategoryCode", c77Identifier, c77Name));
        sensingInstrument.setType(sDNDataCategoryCodePropertyType);
        if (instituteIdentifier != null) {
            final MIPlatformPropertyType mIPlatformPropertyType = gmi.createMIPlatformPropertyType();
            final MIPlatformType mIPlatformType = gmi.createMIPlatformType();
            mIPlatformType.setIdentifier(getIdentifier(platformIdentifier));
            mIPlatformType.setDescription(getCS(platformDescription));
            mIPlatformType.getSponsor().add(getResponsibleParty(cSRobjective.getPi(), Role.PRINCIPAL_INVESTIGATOR));
            final MIInstrumentPropertyType mIInstrumentPropertyType = gmi.createMIInstrumentPropertyType();
            mIInstrumentPropertyType.setHref(sensingInstrumentIdentifier);
            mIPlatformType.getInstrument().add(mIInstrumentPropertyType);
            mIPlatformPropertyType.setMIPlatform(mIPlatformType);
            sensingInstrument.setMountedOn(mIPlatformPropertyType);
        }
        sensingInstrumentProperty.setMIInstrument(sensingInstrument);
        objective.getSensingInstrument().add(sensingInstrumentProperty);

        final List<MIEventPropertyType> objectiveOccurrences = objective.getObjectiveOccurrence();
        CodeListValueType trigger = getCodeListValue(REALM_ISO_GMI, "MI_TriggerCode", "manual", "manual");
        CodeListValueType context = getCodeListValue(REALM_ISO_GMI, "MI_ContextCode", "acquisition", "acquisition");
        CodeListValueType sequenceInstantaneous = getCodeListValue(REALM_ISO_GMI, "MI_SequenceCode", "instantaneous",
                "instantaneous");
        objectiveOccurrences.add(getEvent(eventIdentifier, trigger, context, sequenceInstantaneous, day));
        SDNSamplingActivityType SDNSamplingActivityType = sdn.createSDNSamplingActivityType();
        final BigDecimal nbEvents = BigDecimal.valueOf(cSRobjective.getEvents().size());
        SDNSamplingActivityType.setQuantity(nbEvents);
        SDNCSRUnitCodePropertyType SDNCSRUnitCodePropertyType = sdn.createSDNCSRUnitCodePropertyType();
        SDNCSRUnitCodePropertyType.setSDNCSRUnitCode(getCodeListValue(REALM_SDN_CDI_CSR, "SDN_CSRUnitCode",
                cSRobjective.getL18Identifier(), cSRobjective.getL18Name()));
        SDNSamplingActivityType.setUnit(SDNCSRUnitCodePropertyType);
        objective.setSDNSamplingActivity(SDNSamplingActivityType);
        return objectiveProp;
    }

    private MIAcquisitionInformationPropertyType getAcquisitionInformation(Map<MultiKey, CSRObjective> objectives) {
        MIAcquisitionInformationPropertyType acqProp = gmi.createMIAcquisitionInformationPropertyType();
        MIAcquisitionInformationType acq = gmi.createMIAcquisitionInformationType();
        acq.getOperation().add(getCruiseOperation());
        for (Map.Entry<MultiKey, CSRObjective> entry : objectives.entrySet()) {
            MultiKey key = entry.getKey();
            CSRObjective objective = entry.getValue();
            acq.getObjective().add(getEventObjective(objective, key.day));
        }

        acqProp.setMIAcquisitionInformation(acq);
        return acqProp;
    }

    public JAXBElement<MIMetadataType> getMetadata() throws JAXBException {
        MIMetadataType csr = new MIMetadataType();
        JAXBElement<MIMetadataType> result = gmi.createMIMetadata(csr);
        /*Identifier*/
        csr.setFileIdentifier(getCS(getCSRIdentifier()));
        /*MD Language*/
        LanguageCodePropertyType language = new LanguageCodePropertyType();
        language.setLanguageCode(getCodeListValue(REALM_SDN_GMX, "LanguageCode", "eng", "English"));
        csr.setLanguage(language);
        /*MD Characterset*/
        MDCharacterSetCodePropertyType characterSet = new MDCharacterSetCodePropertyType();
        characterSet.setMDCharacterSetCode(getCodeListValue(REALM_ISO_GMX, "MD_CharacterSetCode", "utf8", "utf8"));
        csr.setCharacterSet(characterSet);
        /*Hierarchy Level*/
        MDScopeCodePropertyType scopeCode = new MDScopeCodePropertyType();
        scopeCode.setMDScopeCode(
                gmd.createMDScopeCode(getCodeListValue(REALM_ISO_GMX, "MD_ScopeCode", "series", "series")));
        csr.getHierarchyLevel().add(scopeCode);
        /*Hierarchy Level Name*/
        CodeListValueType hierarchyLevelNameCode = getCodeListValue(REALM_SDN_CDI_CSR, "SDN_HierarchyLevelNameCode",
                "CSR", "Cruise Summary record");
        SDNHierarchyLevelNameCodePropertyType createSDNHierarchyLevelNameCodePropertyType = new SDNHierarchyLevelNameCodePropertyType();
        createSDNHierarchyLevelNameCodePropertyType.setSDNHierarchyLevelNameCode(hierarchyLevelNameCode);
        csr.getHierarchyLevelName().add(createSDNHierarchyLevelNameCodePropertyType);
        /*Metadata contact*/
        IOrganisation metadataResponsible = cruise.getCollateCentre();
        CIResponsiblePartyPropertyType responsibleParty = getResponsibleParty(metadataResponsible,
                Role.POINT_OF_CONTACT);
        csr.getContact().add(responsibleParty);

        /*Datestamp*/
        csr.setDateStamp(getDate(new Date()));
        /*Metadata standard name, version and extension*/
        csr.setMetadataStandardName(getCS("ISO 19115/SeaDataNet profile"));
        csr.setMetadataStandardVersion(getCS("1.0"));
        MDMetadataExtensionInformationPropertyType mdExtProp = new MDMetadataExtensionInformationPropertyType();
        mdExtProp.setHref(
                "http://schemas.seadatanet.org/Standards-Software/Metadata-formats/csrExtensionInformation.xml");
        csr.getMetadataExtensionInfo().add(mdExtProp);
        /*IdentificationInfo*/
        csr.getIdentificationInfo().add(getIdentificationInfo());

        csr.getDataQualityInfo().add(getInspireDataQuality());

        /*   final MIAcquisitionInformationPropertyType MIAcquisitionInformationPropertyType = gmi.createMIAcquisitionInformationPropertyType();
        final MIAcquisitionInformationType MIAcquisitionInformationType = gmi.createMIAcquisitionInformationType();
        MIAcquisitionInformationPropertyType.setMIAcquisitionInformation(MIAcquisitionInformationType);
        csr.getAcquisitionInformation().add(MIAcquisitionInformationPropertyType);*/
        Map<MultiKey, CSRObjective> objectives = new HashMap<>(); //a data structure where the tool, the C77 purpose and the date are the keys

        //extract all events, grouped by their tool, C77 category, day date and principal investigator. Do this for each group of events separately (deployments, samplings,...)
        if (this.cruise.getEvents() != null) {
            for (IEvent event : this.cruise.getEvents()) {
                if (event.getPlatform().equals(this.cruise.getPlatform())) {
                    ITool tool = event.getTool();
                    for (CSRObjective cSRObjectiveTemplate : CSRObjective.OBJECTIVES) {
                        if (event.getProcess().getIdentifier().equals(cSRObjectiveTemplate.getProcessIdentifier())) {
                            OffsetDateTime day = event.getTimeStamp().truncatedTo(ChronoUnit.DAYS);
                            List<IPerson> pis = new ArrayList();
                            if (event.getProgram() != null) {
                                pis = new ArrayList(event.getProgram().getPrincipalInvestigators());
                            }
                            IPerson pi = pis.isEmpty() ? null : pis.get(0);
                            MultiKey key = new MultiKey(tool, event.getSubject(), day, pi);

                            CSRObjective cSRObjective = objectives.get(key);
                            if (cSRObjective == null) {
                                cSRObjective = new CSRObjective(cSRObjectiveTemplate);
                            }
                            cSRObjective.setTool(tool);//every time again even though it may be the same...
                            cSRObjective.setPi(pi);
                            cSRObjective.setPurpose(event.getSubject());
                            cSRObjective.addEvent(event);
                            if (event.getToolCategory().getIdentifier()
                                    .equals(cSRObjective.getToolCategoryIdentifier())) {
                                cSRObjective.setTool(tool);
                                cSRObjective.setPi(pi);
                                cSRObjective.setPurpose(event.getSubject());
                                cSRObjective.addEvent(event);
                            }

                            objectives.put(key, cSRObjective);
                        }
                    }
                }
            }
        }

        csr.getAcquisitionInformation().add(getAcquisitionInformation(objectives));
        return result;

    }

    private class MultiKey {

        ITool tool;
        ILinkedDataTerm purpose;
        OffsetDateTime day;
        IPerson pi;

        public MultiKey(ITool tool, ILinkedDataTerm purpose, OffsetDateTime day, IPerson p) {
            this.tool = tool;
            this.purpose = purpose;
            this.day = day;
            this.pi = pi;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 37 * hash + Objects.hashCode(this.tool.getTerm().getIdentifier());
            hash = 37 * hash + Objects.hashCode(this.purpose.getIdentifier());
            hash = 37 * hash + Objects.hashCode(this.day);
            if (this.pi != null) {
                hash = 37 * hash + Objects.hashCode(this.pi.getFirstNameLastName());
            }
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MultiKey other = (MultiKey) obj;
            if (!Objects.equals(this.tool.getTerm().getIdentifier(), other.tool.getTerm().getIdentifier())) {
                return false;
            }
            if (!Objects.equals(this.purpose.getIdentifier(), other.purpose.getIdentifier())) {
                return false;
            }
            if (!Objects.equals(this.day, other.day)) {
                return false;
            }
            if (this.pi != null && !Objects.equals(this.pi.getFirstNameLastName(), other.pi.getFirstNameLastName())) {
                return false;
            }
            return true;
        }

    }

    private void addToMapOfLists(Map<ITool, List<IEvent>> map, ITool tool, IEvent event) {
        if (map.get(tool) == null) {
            List<IEvent> events = new ArrayList<>();
            events.add(event);
            map.put(tool, events);
        } else {
            List<IEvent> events = map.get(tool);
            events.add(event);
            map.put(tool, events);
        }
    }

    public DatePropertyType getDate(OffsetDateTime offsetdt) {
        long epochMilli = offsetdt.toInstant().toEpochMilli();
        Date date = new Date(epochMilli);
        return getDate(date);
    }

    public DatePropertyType getDate(Date date) {
        DatePropertyType dateProp = new DatePropertyType();
        String dateString = new SimpleDateFormat("yyyy-MM-dd").format(date);
        dateProp.setDate(dateString);
        return dateProp;
    }

    public DateTimePropertyType getDateTime(OffsetDateTime offsetdt) {
        DateTimePropertyType dateTimeProp = gco.createDateTimePropertyType();
        //XMLGregorianCalendar xmlGregorianCalendar = null; //new XMLGregorianCalendarImpl();
        //try {
        GregorianCalendar gregorianCalendar = GregorianCalendar.from(offsetdt.toZonedDateTime());
        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(gregorianCalendar);
            dateTimeProp.setDateTime(xmlGregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        /*cal.setYear(offsetdt.getYear());
        cal.setMonth(offsetdt.getMonthValue());
        cal.setDay(offsetdt.getDayOfMonth());
        cal.setHour(offsetdt.getHour());
        cal.setMinute(offsetdt.getMinute());
        cal.setSecond(offsetdt.getSecond());
        cal.setTimezone(offsetdt.getOffset().getTotalSeconds() / 60);
        */
        //} catch (DatatypeConfigurationException e) {
        //    e.printStackTrace();
        //}

        //  dateTimeProp.setDateTime(new XMLGregorianCalendarImpl(new GregorianCalendar(offsetdt.getYear(), offsetdt.getMonthValue() - 1, offsetdt.getDayOfMonth(), offsetdt.getHour(), offsetdt.getMinute(), offsetdt.getSecond())));
        return dateTimeProp;
    }

    public CIDatePropertyType getDate(Date date, String dateTypeString) {
        CIDatePropertyType dateProp = new CIDatePropertyType();
        CIDateType cidate = new CIDateType();
        cidate.setDate(getDate(date));
        CIDateTypeCodePropertyType dateType = new CIDateTypeCodePropertyType();
        dateType.setCIDateTypeCode(getCodeListValue(REALM_ISO_GMX, "CI_DateTypeCode", dateTypeString, dateTypeString));
        cidate.setDateType(dateType);
        dateProp.setCIDate(cidate);
        return dateProp;
    }

    public MDIdentificationPropertyType getIdentificationInfo() {
        MDIdentificationPropertyType id = new MDIdentificationPropertyType();
        SDNDataIdentificationType sdnId = new SDNDataIdentificationType();
        JAXBElement<SDNDataIdentificationType> SDNDataIdentificationJaxb = sdn.createSDNDataIdentification(sdnId);

        id.setAbstractMDIdentification(SDNDataIdentificationJaxb);

        sdnId.setCitation(getCruiseCitation());
        sdnId.setAbstract(getCS(cruise.getObjectives()));
        //sdnId.setPurpose(getCS(cruise.getPurpose())); //not CSR-compliant
        //sdnId.setId(getCSRIdentifier()); //not CSR-compliant
        sdnId.getAdditionalDocumentation().add(null);
        // sdnId.setSupplementalInformation(getCS(cruise.getFinalReportUrl()));
        EXExtentPropertyType extentProp = new EXExtentPropertyType();
        EXExtentType extent = new EXExtentType();
        extentProp.setEXExtent(extent);

        extent.getTemporalElement().add(getTemporalExtent(cruise.getStartDate(), cruise.getEndDate()));

        extent.getGeographicElement().add(getGeographicExtent(cruise.getSouthBoundLatitude(),
                cruise.getNorthBoundLatitude(), cruise.getWestBoundLongitude(), cruise.getEastBoundLongitude()));
        if (cruise.getTrack() != null) {
            extent.getGeographicElement().add(getBoundingPolygon(cruise.getTrack()));
        }
        sdnId.getExtent().add(extentProp);
        Collection<? extends IPerson> chiefScientists = cruise.getChiefScientists();

        Set<IPerson> principalInvestigators = new HashSet(); //so that the same person is not evaluated twice
        if (cruise.getPrograms() != null) {
            for (IProgram program : cruise.getPrograms()) {
                principalInvestigators.addAll(program.getPrincipalInvestigators());
            }
        }
        for (IPerson chiefScientist : chiefScientists) {
            sdnId.getPointOfContact().add(getResponsibleParty(chiefScientist, Role.POINT_OF_CONTACT));
        }

        /*if (chiefScientists != null) {
            principalInvestigators.addAll(chiefScientists);
        }
        for (IPerson principalInvestigator : principalInvestigators) {
            sdnId.getPointOfContact().add(getResponsibleParty(principalInvestigator, Role.POINT_OF_CONTACT));
        }*/
        /*Dataset browsegraphic*/
        //Ifremer CSR site doesn't like this
        /* if (cruise.getTrackImageUrl() != null) {
            sdnId.getGraphicOverview().add(getBrowseGraphic(cruise.getTrackImageUrl(), "Image of the track", cruise.getTrackImageUrl().substring(cruise.getTrackImageUrl().length() - 3, cruise.getTrackImageUrl().length())));
        } else if (cruise.getTrackGmlUrl() != null) {
            sdnId.getGraphicOverview().add(getBrowseGraphic(cruise.getTrackGmlUrl(), "GML track", "GML"));
        }*/
        /*Dataset Keywords*/
        if (OCEANOGRAPHIC_GEOGRAPHIC_FEATURES != null) {
            sdnId.getDescriptiveKeywords().add(OCEANOGRAPHIC_GEOGRAPHIC_FEATURES);
        }

        List<ILinkedDataTerm> departureHarbours = Arrays
                .asList(new ILinkedDataTerm[]{cruise.getDepartureHarbour().getTerm()});
        MDKeywordsPropertyType kw = getAnchorKeyword(departureHarbours, "departure_place", THESAURUS_C38);
        if (kw != null) {
            sdnId.getDescriptiveKeywords().add(kw);
        }
        //sdnId.getDescriptiveKeywordsSDN().add(getSDNKeyword(departureHarbours, "departure_place", THESAURUS_C38));

        List<ILinkedDataTerm> arrivalHarbours = Arrays
                .asList(new ILinkedDataTerm[]{cruise.getArrivalHarbour().getTerm()});
        MDKeywordsPropertyType kw2 = getAnchorKeyword(arrivalHarbours, "arrival_place", THESAURUS_C38);
        if (kw2 != null) {
            sdnId.getDescriptiveKeywords().add(kw2);
        }
        //sdnId.getDescriptiveKeywordsSDN().add(getSDNKeyword(arrivalHarbours, "arrival_place", THESAURUS_C38));

        List<ILinkedDataTerm> departureCountries = Arrays
                .asList(new ILinkedDataTerm[]{cruise.getDepartureHarbour()._getCountry().getTerm()});
        MDKeywordsPropertyType kw3 = getAnchorKeyword(departureCountries, "departure_country", THESAURUS_C32);
        if (kw3 != null) {
            sdnId.getDescriptiveKeywords().add(kw3);
        }
        //sdnId.getDescriptiveKeywordsSDN().add(getSDNKeyword(departureCountries, "departure_country", THESAURUS_C32));

        List<ILinkedDataTerm> arrivalCountries = Arrays
                .asList(new ILinkedDataTerm[]{cruise.getArrivalHarbour()._getCountry().getTerm()});
        MDKeywordsPropertyType kw4 = getAnchorKeyword(arrivalCountries, "arrival_country", THESAURUS_C32);
        if (kw4 != null) {
            sdnId.getDescriptiveKeywords().add(kw4);
        }
        //sdnId.getDescriptiveKeywordsSDN().add(getSDNKeyword(arrivalCountries, "arrival_country", THESAURUS_C32));

        List<ILinkedDataTerm> platforms = Arrays.asList(new ILinkedDataTerm[]{cruise.getPlatform().getTerm()});
        //sdnId.getDescriptiveKeywordsSDN().add(getSDNKeyword(platforms, "platform", THESAURUS_C17));
        MDKeywordsPropertyType kw5 = getAnchorKeyword(platforms, "platform", THESAURUS_C17);
        if (kw5 != null) {
            sdnId.getDescriptiveKeywords().add(kw5);
        }

        List<ILinkedDataTerm> platformClasses = Arrays
                .asList(new ILinkedDataTerm[]{cruise.getPlatform().getPlatformClass()});
        //sdnId.getDescriptiveKeywordsSDN().add(getSDNKeyword(platformClasses, "platform_class", THESAURUS_L06));
        MDKeywordsPropertyType kw6 = getAnchorKeyword(platformClasses, "platform_class", THESAURUS_L06);
        if (kw6 != null) {
            sdnId.getDescriptiveKeywords().add(kw6);
        }

        Collection<? extends ILinkedDataTerm> projects = cruise.getProjectTerms();
        //sdnId.getDescriptiveKeywordsSDN().add(getSDNKeyword(projects, "project", THESAURUS_EDMERP));
        MDKeywordsPropertyType kw7 = getAnchorKeyword(projects, "project", THESAURUS_EDMERP);
        if (kw7 != null) {
            sdnId.getDescriptiveKeywords().add(kw7);
        }

        Collection<? extends ILinkedDataTerm> seaAreas = cruise.getSeaAreaTerms();
        Collection<ILinkedDataTerm> marsdenSquares = new ArrayList<>();
        for (Iterator<? extends ILinkedDataTerm> iterator = seaAreas.iterator(); iterator.hasNext(); ) {
            ILinkedDataTerm seaArea = iterator.next();
            if (seaArea != null) {
                if (seaArea.getUrn().contains("C37")) { //MARSDEN SQUARES
                    marsdenSquares.add(seaArea.getTerm());
                    iterator.remove();
                }
            }
        }

        //sdnId.getDescriptiveKeywordsSDN().add(getSDNKeyword(seaAreas, "place", THESAURUS_C19));
        //sdnId.getDescriptiveKeywordsSDN().add(getSDNKeyword(marsdenSquares, "marsden_square", THESAURUS_C19));
        MDKeywordsPropertyType kw8 = getAnchorKeyword(seaAreas, "place", THESAURUS_C19);
        if (kw8 != null) {
            sdnId.getDescriptiveKeywords().add(kw8);
        }
        MDKeywordsPropertyType kw9 = getAnchorKeyword(marsdenSquares, "marsden_square", THESAURUS_C19);
        if (kw9 != null) {
            sdnId.getDescriptiveKeywords().add(kw9);
        }

        Collection<? extends ILinkedDataTerm> parameters = cruise.getP02();
        MDKeywordsPropertyType kw10 = getAnchorKeyword(parameters, "parameter", THESAURUS_P02);
        if (kw10 != null) {
            sdnId.getDescriptiveKeywords().add(kw10);
        }

        Collection<? extends ILinkedDataTerm> instruments = cruise.getInstrumentTypes();
        MDKeywordsPropertyType kw11 = getAnchorKeyword(instruments, "instrument", THESAURUS_L05);
        if (kw11 != null) {
            sdnId.getDescriptiveKeywords().add(kw11);
        }

        /*Dataset Constraints*/
        sdnId.getResourceConstraints().add(getUseLimitation("Not applicable"));
        sdnId.getResourceConstraints()
                .add(getAccessConstraint(license.getLicenseTerm().getIdentifier(), license.getLicenseTerm().getName()));

        /*Dataset Language*/
        LanguageCodePropertyType language = new LanguageCodePropertyType();
        language.setLanguageCode(getCodeListValue(REALM_SDN_GMX, "LanguageCode", "eng", "English"));
        sdnId.getLanguage().add(language);

        /*Dataset Characterset*/
        MDCharacterSetCodePropertyType characterSet = new MDCharacterSetCodePropertyType();
        characterSet.setMDCharacterSetCode(getCodeListValue(REALM_ISO_GMX, "MD_CharacterSetCode", "utf8", "utf8"));
        sdnId.getCharacterSet().add(characterSet);

        /*Dataset Topic category*/
        MDTopicCategoryCodePropertyType topicProp = new MDTopicCategoryCodePropertyType();
        topicProp.setMDTopicCategoryCode(MDTopicCategoryCodeType.OCEANS);
        sdnId.getTopicCategory().add(topicProp);

        return id;
    }

    private TimePositionType getTimeInstant(String time) {
        //TimeInstantPropertyType timeInstantProp = gml.createTimeInstantPropertyType();
        //TimeInstantType timeInstant = gml.createTimeInstantType();
        TimePositionType timePositionType = gml.createTimePositionType();
        timePositionType.getValue().add(time);
        //timeInstant.setTimePosition(timePositionType);
        //timeInstantProp.setTimeInstant(timeInstant);
        return timePositionType;
    }

    private EXTemporalExtentPropertyType getTemporalExtent(Date begin, Date end) {

        EXTemporalExtentPropertyType tempExProp = new EXTemporalExtentPropertyType();
        EXTemporalExtentType tempEx = new EXTemporalExtentType();
        TMPrimitivePropertyType tmProp = new TMPrimitivePropertyType();
        TimePeriodType timePeriod = new TimePeriodType();
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        timePeriod.setId("ID");
        timePeriod.setBeginPosition(getTimeInstant(fm.format(begin)));
        timePeriod.setEndPosition(getTimeInstant(fm.format(end)));

        tmProp.setAbstractTimePrimitive(gml.createTimePeriod(timePeriod));
        tempEx.setExtent(tmProp);
        tempExProp.setEXTemporalExtent(gmd.createEXTemporalExtent(tempEx));
        return tempExProp;
    }

    private EXTemporalExtentPropertyType getTemporalExtent(OffsetDateTime begin, OffsetDateTime end) {

        EXTemporalExtentPropertyType tempExProp = new EXTemporalExtentPropertyType();
        EXTemporalExtentType tempEx = new EXTemporalExtentType();
        TMPrimitivePropertyType tmProp = new TMPrimitivePropertyType();
        TimePeriodType timePeriod = new TimePeriodType();

        timePeriod.setId("ID");
        timePeriod.setBeginPosition(getTimeInstant(begin.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        timePeriod.setEndPosition(getTimeInstant(end.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));

        tmProp.setAbstractTimePrimitive(gml.createTimePeriod(timePeriod));
        tempEx.setExtent(tmProp);
        tempExProp.setEXTemporalExtent(gmd.createEXTemporalExtent(tempEx));
        return tempExProp;
    }

    private DecimalPropertyType getDecimal(double doublev) {
        DecimalPropertyType decimalProp = new DecimalPropertyType();
        decimalProp.setDecimal(BigDecimal.valueOf(doublev));
        return decimalProp;
    }

    private EXGeographicExtentPropertyType getGeographicExtent(double southBoundLatitude, double northBoundLatitude,
                                                               double westBoundLongitude, double eastBoundLongitude) {
        EXGeographicExtentPropertyType geoExProp = new EXGeographicExtentPropertyType();
        EXGeographicBoundingBoxType bb = new EXGeographicBoundingBoxType();
        bb.setSouthBoundLatitude(getDecimal(southBoundLatitude));
        bb.setNorthBoundLatitude(getDecimal(northBoundLatitude));
        bb.setWestBoundLongitude(getDecimal(westBoundLongitude));
        bb.setEastBoundLongitude(getDecimal(eastBoundLongitude));
        JAXBElement<EXGeographicBoundingBoxType> createAbstractEXGeographicExtent = gmd
                .createEXGeographicBoundingBox(bb);
        geoExProp.setAbstractEXGeographicExtent(createAbstractEXGeographicExtent);
        return geoExProp;
    }

    private EXGeographicExtentPropertyType getBoundingPolygon(Collection<? extends ICoordinate> trackList) {
        if (trackList != null) {
            EXGeographicExtentPropertyType geoExProp = new EXGeographicExtentPropertyType();
            EXBoundingPolygonType track = gmd.createEXBoundingPolygonType();
            JAXBElement<AbstractEXGeographicExtentType> abstractEXGeographicExtent = gmd
                    .createAbstractEXGeographicExtent(track);
            geoExProp.setAbstractEXGeographicExtent(abstractEXGeographicExtent);
            GMObjectPropertyType gm = gss.createGMObjectPropertyType();
            track.getPolygon().add(gm);

            MultiCurveType multiCurve = gml.createMultiCurveType();
            JAXBElement<AbstractGeometryType> abstractGeometry = gml.createAbstractGeometry(multiCurve);
            gm.setAbstractGeometry(abstractGeometry);

            CurvePropertyType curveProp = gml.createCurvePropertyType();
            multiCurve.getCurveMember().add(curveProp);
            multiCurve.setId("multiCurve_1");
            LineStringType lineString = gml.createLineStringType();
            curveProp.setAbstractCurve(gml.createLineString(lineString));
            CodeType name = gml.createCodeType();
            name.setValue("Track curve of campaign " + cruise.getName());
            lineString.getName().add(name);
            StringOrRefType description = gml.createStringOrRefType();
            description.setValue("Track curve of campaign " + cruise.getName());
            lineString.setDescription(description);
            lineString.setId("lineString_1");
            DirectPositionListType posList = gml.createDirectPositionListType();
            posList.setSrsName("http://www.opengis.net/gml/srs/epsg.xml#4326");
            posList.setSrsDimension(new BigInteger("2"));
            for (ICoordinate c : trackList) {
                if (c.isValid()) {
                    posList.getValue().add(c.getX());
                    posList.getValue().add(c.getY());

                } else {
                    Logger.getLogger(CSRBuilder.class
                            .getName()).log(Level.SEVERE, "Erroneous coordinate found: " + c.getX() + "," + c.getY());
                }
            }

            lineString.setPosList(posList);
            return geoExProp;
        }
        return null;
    }

    private MDConstraintsPropertyType getUseLimitation(String freeText) {
        MDConstraintsPropertyType constraintsProp = new MDConstraintsPropertyType();
        MDConstraintsType constraints = new MDConstraintsType();
        constraints.getUseLimitation().add(getCS(freeText));
        constraintsProp.setMDConstraints(gmd.createMDConstraints(constraints));
        return constraintsProp;
    }

    private MDLegalConstraintsPropertyType getAccessConstraint(String url, String text) {
        MDLegalConstraintsPropertyType constraintsProp = new MDLegalConstraintsPropertyType();
        MDLegalConstraintsType constraints = new MDLegalConstraintsType();
        MDRestrictionCodePropertyType restrictionCode = new MDRestrictionCodePropertyType();

        restrictionCode.setMDRestrictionCode(
                getCodeListValue(REALM_SDN_GMX, "MD_RestrictionCode", "otherRestrictions", "otherRestrictions"));
        constraints.getAccessConstraints().add(restrictionCode);
        constraintsProp.setMDLegalConstraints(constraints);
        AnchorType anchor = new AnchorType();
        anchor.setHref(url);
        anchor.setValue(text);

        JAXBElement<AnchorType> a = gmx.createAnchor(anchor);
        CharacterStringPropertyType c = new CharacterStringPropertyType();
        c.setCharacterString(a);
        constraints.getOtherConstraints().add(c);

        return constraintsProp;
    }

    private CICitationPropertyType getShortCitation(String title, Date publication, String revisionOrPublication) {
        CICitationPropertyType citationProp = new CICitationPropertyType();
        CICitationType citation = new CICitationType();
        citationProp.setCICitation(citation);
        citation.setTitle(getCS(title));
        citation.getDate().add(getDate(publication, Thesaurus.PUBLICATION));
        return citationProp;
    }

    private CICitationPropertyType getCruiseCitation() {
        CICitationPropertyType citationProp = new CICitationPropertyType();
        CICitationType citation = new CICitationType();
        citationProp.setCICitation(citation);
        citation.setTitle(getCS(this.cruise.getIdentifier()));
        citation.getAlternateTitle().add((getCS(this.cruise.getName())));
        citation.getIdentifier().add(getIdentifier(getCSRIdentifier()));
        citation.getCitedResponsibleParty()
                .add(getResponsibleParty(this.cruise.getPlatform().getVesselOperator(), Role.ORIGINATOR));

        citation.getDate().add(getDate(new Date(), Thesaurus.REVISION));
        return citationProp;
    }

    private CICitationPropertyType getShortCruiseCitation() {
        CICitationPropertyType citationProp = new CICitationPropertyType();
        CICitationType citation = new CICitationType();
        citationProp.setCICitation(citation);
        citation.setTitle(getCS(this.cruise.getIdentifier()));
        citation.getAlternateTitle().add((getCS(this.cruise.getName())));
        citation.getDate().add(getDate(new Date(), Thesaurus.REVISION));
        return citationProp;
    }

    private CICitationPropertyType getThesaurusCitation(Thesaurus thesaurus) {
        CICitationPropertyType citationProp = new CICitationPropertyType();
        CICitationType citation = new CICitationType();
        citationProp.setCICitation(citation);
        citation.setTitle(getCS(thesaurus.getThesaurusTitle()));
        String thesaurusCode = thesaurus.getThesaurusAltTitle();
        if (thesaurusCode != null) {
            citation.getAlternateTitle().add(getCS(thesaurusCode));
            citation.getIdentifier().add(getIdentifier("http://www.seadatanet.org/urnurl/SDN:" + thesaurusCode));
        }
        if (thesaurus.getThesaurusVersion() != null) {
            citation.setEdition(getCS(thesaurus.getThesaurusVersion()));
        }
        if (thesaurus.getThesaurusDate() != null) {
            citation.getDate().add(getDate(thesaurus.getThesaurusDate(), thesaurus.getPublicationOrRevision()));
        }
        return citationProp;
    }
}
