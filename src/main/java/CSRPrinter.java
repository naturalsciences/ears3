/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.cruise.csr;

import java.io.File;
import java.io.StringWriter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.seadatanet.csr.org.isotc211._2005.gmi.MIMetadataType;

/**
 *
 * @author thomas
 */
public class CSRPrinter {

    private CSRBuilder builder;
    private Marshaller marshaller;

    public CSRBuilder getBuilder() {
        return builder;
    }

    public CSRPrinter(CSRBuilder builder){
        try {
            this.builder = builder;
            JAXBContext jaxbContext = JAXBContext.newInstance(MIMetadataType.class);
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
                    "http://www.seadatanet.org http://schemas.seadatanet.org/Standards-Software/Metadata-formats/SDN2_CSR_ISO19139_3.0.0.xsd");

            process();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private void process() throws JAXBException {

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        //  marshaller.marshal(mdr, System.out);
    }

    public void createFile(File file, boolean overwrite) {
        try {
            marshaller.marshal(builder.getMetadata(), file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    //SDN_PortCode
    private String replaceAnchors(String xml, String SDNCodeType) {
        xml = xml.replaceAll(
                "<gmx:Anchor xlink:href=\"(.*?)\" xlink:role=\".*?#" + SDNCodeType + "\">(.*?)</gmx:Anchor>",
                "<sdn:" + SDNCodeType
                        + " codeList=\"http://vocab.nerc.ac.uk/isoCodelists/sdnCodelists/cdicsrCodeList.xml#"
                        + SDNCodeType + "\" codeListValue=\"$1\" codeSpace=\"SeaDataNet\">$2</sdn:" + SDNCodeType
                        + ">");
        return xml;
    }

    public String getResult() {
        StringWriter writer = new StringWriter();
        try {
            marshaller.marshal(builder.getMetadata(), writer);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        String result = writer.toString();
        result = result.replace(" xlink:type=\"simple\"", "");
        result = result.replace("gmd:AbstractEX_GeographicExtent xsi:type=\"gmd:EX_BoundingPolygon_Type\"",
                "gmd:EX_BoundingPolygon");
        result = result.replace("gmd:AbstractEX_GeographicExtent", "gmd:EX_BoundingPolygon");
        result = result.replace("gmd:AbstractDQ_Element xsi:type=\"gmd:DQ_DomainConsistency_Type\"",
                "gmd:DQ_DomainConsistency");
        result = result.replace("gmd:AbstractDQ_Element", "gmd:DQ_DomainConsistency");

        result = result.replace("gml:AbstractGeometry xsi:type=\"gml:MultiCurveType\"", "gml:MultiCurve");
        result = result.replace("gmi:MI_Objective xsi:type=\"sdn:SDN_Objective_Type\"", "sdn:SDN_Objective");
        result = result.replace("gmi:MI_Objective", "sdn:SDN_Objective");

        result = result.replaceAll("xsi:type=\".*?\"", "");

        result = result.replace("sdn:SDN_ObjectiveTypeCode", "gmi:MI_ObjectiveTypeCode");

        result = replaceAnchors(result, "SDN_PortCode");
        result = replaceAnchors(result, "SDN_CountryCode");
        result = replaceAnchors(result, "SDN_PlatformCode");
        result = replaceAnchors(result, "SDN_PlatformCategoryCode");
        result = replaceAnchors(result, "SDN_EDMERPCode");
        result = replaceAnchors(result, "SDN_WaterBodyCode");
        result = replaceAnchors(result, "SDN_DeviceCategoryCode");
        result = replaceAnchors(result, "SDN_ParameterDiscoveryCode");
        result = replaceAnchors(result, "SDN_MarsdenCode");

        result = result.replace("gml:AbstractGeometry", "gml:MultiCurve");
        if (builder.getCruise().getFinalReportUrl() != null) {
            result = result.replace("gmd:additionalDocumentation xsi:nil=\"true\"",
                    "sdn:additionalDocumentation xlink:href=\"" + builder.getCruise().getFinalReportUrl() + "\"");
        }
        result = result.replace("<gmd:additionalDocumentation xsi:nil=\"true\"/>", "");
        if (builder.getDefaultMessages() != null && !builder.getDefaultMessages().isEmpty()) {
            StringBuilder sb = new StringBuilder("<!--");
            for (String msg : builder.getDefaultMessages()) {
                sb.append(msg);
            }
            sb.append("-->");
            result = result.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + sb.toString());
        }
        return result;
    }

    public void print() {
        try {
            marshaller.marshal(builder.getMetadata(), System.out);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
