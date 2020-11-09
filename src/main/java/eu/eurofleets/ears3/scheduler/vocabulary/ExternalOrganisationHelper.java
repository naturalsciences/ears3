/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.scheduler.vocabulary;

import eu.eurofleets.ears3.domain.Country;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Organisation;
import gnu.trove.map.hash.THashMap;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Thomas Vandenberghe
 */
public class ExternalOrganisationHelper implements IExternalHelper<Organisation> {

    public static final String URL = "https://edmo.seadatanet.org/webservices/edmo/ws_edmo_get_list";

    private Map<String, Organisation> retrieved;
    private int size;

    public ExternalOrganisationHelper() throws Exception {
        this.retrieved = retrieve();
        this.size = this.retrieved.size();
    }

    @Override
    public Map<String, Organisation> retrieve() {
        if (this.retrieved != null) {
            return this.retrieved;
        } else {
            Map<String, Organisation> map = new THashMap();//new ArrayList();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            NodeList nList = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(URL);
                doc.getDocumentElement().normalize();
                nList = doc.getElementsByTagName("Organisation");
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(ExternalOrganisationHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(ExternalOrganisationHelper.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ExternalOrganisationHelper.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (nList != null) {
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node n = nList.item(temp);
                    if (n.getNodeType() == Node.ELEMENT_NODE) {

                        Element el = (Element) n;
                        String countryString = el.getElementsByTagName("c_country").item(0).getTextContent();

                        Country c = new Country(new LinkedDataTerm("fake-id",null, countryString));

                        Organisation o = new Organisation();
                        o._setCity(el.getElementsByTagName("city").item(0).getTextContent());
                        o._setCountry(c);
                        o._setDeliveryPoint(el.getElementsByTagName("address").item(0).getTextContent());
                        o._setEmailAddress(el.getElementsByTagName("email").item(0).getTextContent());
                        o._setFaxNumber(el.getElementsByTagName("fax").item(0).getTextContent());
                        o._setWebsite(el.getElementsByTagName("website").item(0).getTextContent());
                        o._setPostalcode(el.getElementsByTagName("zipcode").item(0).getTextContent());
                        o._setPhoneNumber(el.getElementsByTagName("phone").item(0).getTextContent());

                        LinkedDataTerm ldt = new LinkedDataTerm();

                        ldt.setUrn("SDN:EDMO::" + el.getElementsByTagName("n_code").item(0).getTextContent());
                        ldt.setIdentifier("https://edmo.seadatanet.org/report/" + el.getElementsByTagName("n_code").item(0).getTextContent());
                        ldt.setName(el.getElementsByTagName("name").item(0).getTextContent());
                        o.setTerm(ldt);
                        map.put(ldt.getIdentifier(), o);
                    }
                }
            }

            return map;
        }
    }

}
