/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler.vocabulary;

import eu.seadatanet.org.edmerp.Citation;
import eu.seadatanet.org.edmerp.DataIdent;
import eu.seadatanet.org.edmerp.Extent;
import eu.seadatanet.org.edmerp.Metadata;
import eu.seadatanet.org.edmerp.ResAltTitle;
import eu.seadatanet.org.edmerp.RespParty;
import eu.seadatanet.org.edmerp.TempExtent;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Project;
import gnu.trove.map.hash.THashMap;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Thomas Vandenberghe
 */
public class ExternalProjectHelper implements IExternalHelper {

    static DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    static DateFormat YYYY_MM_DDT_HH_SS_MM = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
   

    private Map<String, Project> retrieved;
    private int size;

    public ExternalProjectHelper() {
        this.retrieved = retrieve();
        this.size = this.retrieved.size();
    }

    private String getList() throws UnsupportedEncodingException, IOException {
        HttpPost post = new HttpPost("https://edmerp.seadatanet.org/webservices/edmerp/edmerp.asmx");

        /*List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("Content-Type: text/xml", "charset=utf-8"));
        urlParameters.add(new BasicNameValuePair("SOAPAction", "http://seadatanet.maris2.nl/webservices/edmerp/get_list"));*/
        post.setHeader("Content-Type", "text/xml; charset=utf-8");
        post.setHeader("SOAPAction", "http://seadatanet.maris2.nl/webservices/edmerp/get_list");
        post.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
        //post.setHeader("Content-Length", "1000");

        String bodyString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "  <soap:Body>\n"
                + "    <get_list xmlns=\"http://seadatanet.maris2.nl/webservices/edmerp/\" />\n"
                + "  </soap:Body>\n"
                + "</soap:Envelope>";
        StringEntity body = new StringEntity(bodyString, ContentType.APPLICATION_XML);

        post.setEntity(body);

        try ( CloseableHttpClient httpClient = HttpClientBuilder.create().build();  CloseableHttpResponse response = httpClient.execute(post)) {
            //HttpEntity entity = new GzipDecompressingEntity(response.getEntity());
            HttpEntity entity = response.getEntity();
            //return EntityUtils.toString(response.getEntity(), "UTF-8");
            String name = entity.getClass().getName();

            return EntityUtils.toString(entity, Charset.forName("UTF-8").name());
        }
    }

    public Map<String, Project> retrieve() {
        Map<String, Project> result = new THashMap();

        if (retrieved != null) {
            return this.retrieved;
        } else {
            try {

                //Edmerp service = new Edmerp();
                String xml = getList();
                StringBuilder sb = new StringBuilder(xml);
//"<?xml version="1.0" encoding="utf-8"?>"
                sb = stringBuilderReplace(sb, "&lt;?xml version=\"1.0\" encoding=\"utf-8\"?&gt;", "");
                sb = stringBuilderReplace(sb, " xmlns=\"http://seadatanet.maris2.nl/webservices/edmerp/\"", "");
                sb = stringBuilderReplace(sb, "&lt;", "<");
                sb = stringBuilderReplace(sb, "&gt;", ">");
                xml = sb.toString();
//String xml = service.getEdmerpSoap().getList(); //getList();//

//System.out.println(xml);
                Reader reader = new StringReader(xml);
                XMLInputFactory factory = XMLInputFactory.newFactory();

                XMLStreamReader xsr = factory.createXMLStreamReader(reader);
                JAXBContext jaxbContext = JAXBContext.newInstance(Metadata.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                while (xsr.getEventType() != XMLStreamReader.END_DOCUMENT) {

                    if (xsr.isStartElement() && "Metadata".equals(xsr.getLocalName())) {
                        JAXBElement<Metadata> metadataElement = (JAXBElement<Metadata>) jaxbUnmarshaller.unmarshal(xsr);
                        Metadata metadata = metadataElement.getValue();
                        String name = null;//
                        String language = null;//
                        String acronym = null;//
                        String urn = null; //
                        String begin = null; //
                        Date startDate = null; //
                        String end = null; //
                        Date endDate = null; //
                        String country = null;//
                        String mdAuthor = null; //
                        String originator = null;//
                        String pointOfContact = null;//
                        String abs = null;//
                        String mdCreation = null;//
                        Date mdCreationDate = null;//
                        Date mdModificationDate = null;

                        urn = metadata.getMdFileID();
                        name = metadata.getDataIdInfo().getIdCitation().getResTitle();
                        language = metadata.getDataIdInfo().getDataLang().getLanguageCode().getValue();
                        mdCreation = metadata.getMdDateSt();
                        RespParty mdContact = metadata.getMdContact();
                        if (mdContact.getRole().getRoleCd().getValue().equals("author")) {
                            mdAuthor = mdContact.getRpOrgName().getSDNIdent();
                        }

                        DataIdent dataIdInfo = metadata.getDataIdInfo();
                        abs = dataIdInfo.getIdAbs();
                        for (Extent extent : dataIdInfo.getDataExt()) {
                            if (extent.getTempEle() != null) {
                                JAXBElement<?> tempExtentTypes = extent.getTempEle().getTempExtentTypes();
                                Object value = tempExtentTypes.getValue();
                                if (value != null && value instanceof TempExtent) {
                                    TempExtent tempExtent = (TempExtent) value;
                                    if (tempExtent.getExTemp() != null && tempExtent.getExTemp().getTMPrimitive() != null && tempExtent.getExTemp().getTMPrimitive().getValue() != null && tempExtent.getExTemp().getTMPrimitive().getValue().getTMPeriod() != null) {
                                        begin = tempExtent.getExTemp().getTMPrimitive().getValue().getTMPeriod().getBegin();
                                        end = tempExtent.getExTemp().getTMPrimitive().getValue().getTMPeriod().getEnd();
                                    }
                                }
                            }
                        }
                        if (begin != null) {
                            startDate = YYYY_MM_DD.parse(begin);
                        }
                        if (end != null) {
                            endDate = YYYY_MM_DD.parse(end);
                        }
                        if (mdCreation != null) {
                            mdCreationDate = YYYY_MM_DDT_HH_SS_MM.parse(mdCreation);
                        }

                        Citation idCitation = dataIdInfo.getIdCitation();

                        for (RespParty respParty : idCitation.getCitRespParty()) {
                            if (respParty.getRole().getRoleCd().getValue().equals("originator")) {
                                originator = respParty.getRpOrgName().getSDNIdent();
                                country = respParty.getRpCntInfo().getCntAddress().getCountry();
                            }
                            if (respParty.getRole().getRoleCd().getValue().equals("pointOfContact")) {
                                pointOfContact = respParty.getRpOrgName().getSDNIdent();
                            }
                        }

                        for (ResAltTitle altTitle : idCitation.getResAltTitle()) {
                            if (altTitle.getType().equals("project acronym")) {
                                acronym = altTitle.getValue();
                            }
                        }

                        String refDate = idCitation.getResRefDate().getRefDate();
                        mdModificationDate = YYYY_MM_DDT_HH_SS_MM.parse(refDate);
                        Project p = new Project();

                        LinkedDataTerm ldt = new LinkedDataTerm();

                        ldt.setUrn(urn);
                        String id = Arrays.asList(urn.split(":")).get(3);
                        ldt.setIdentifier("https://edmerp.seadatanet.org/report/" + id);

                        ldt.setName(name);
                        p.setTerm(ldt);

                        result.put(urn, p);
                    }
                    xsr.next();
                }
            } catch (IOException ex) {
                Logger.getLogger(ExternalProjectHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JAXBException ex) {
                Logger.getLogger(ExternalProjectHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (XMLStreamException ex) {
                Logger.getLogger(ExternalProjectHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(ExternalProjectHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ExternalProjectHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return result;
    }

    public static StringBuilder stringBuilderReplace(StringBuilder sb, String from, String to) {
        return new StringBuilder(sb.toString().replace(from, to));
    }
}
