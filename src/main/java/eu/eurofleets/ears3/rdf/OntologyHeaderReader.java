package eu.eurofleets.ears3.rdf;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Extracts just the ontology-level header annotations (rdfs:label, scope, scopedTo,
 * dc:modified, owl:versionInfo) from an EARS ontology RDF/XML file, WITHOUT building
 * a full Jena model.
 *
 * Replaces the previous reliance on the external be.naturalsciences.bmdc.ontology
 * library's IOntologyModel.getStaticStuff(), which built the entire in-memory RDF
 * tree just to read a couple of header values - overkill for something this small,
 * and an unnecessary coupling to that library for what is a purely textual concern.
 *
 * These annotations are written by EARSOntologyCreator.writeOntologyMetadata()
 * directly onto the <owl:Ontology> element, which is always the first element in
 * the RDF/XML serialization Jena produces. This reader uses a streaming StAX parser
 * and stops as soon as that element's closing tag is reached - so regardless of how
 * many thousands of individuals follow in the rest of the file, only the small
 * header block at the very top is ever actually parsed.
 */
public final class OntologyHeaderReader {

    private OntologyHeaderReader() {
    }

    public record OntologyHeader(String label, String scope, String scopedTo,
                                  String dateModified, String versionInfo) {

        /** Mirrors the old getFileDate() precedence: dateModified first, versionInfo as fallback. */
        public String bestDate() {
            return dateModified != null ? dateModified : versionInfo;
        }
    }

    public static OntologyHeader read(InputStream in) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        // Defensive XML parsing settings - this file may originate from an upload.
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);

        XMLStreamReader reader = factory.createXMLStreamReader(in);
        Map<String, String> values = new HashMap<>();

        boolean insideOntology = false;
        int depthInsideOntology = 0;
        String currentLocalName = null;
        StringBuilder currentText = new StringBuilder();

        try {
            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = reader.getLocalName();
                    if (!insideOntology && "Ontology".equals(localName)) {
                        insideOntology = true;
                        depthInsideOntology = 0;
                        continue;
                    }
                    if (insideOntology) {
                        depthInsideOntology++;
                        currentLocalName = localName;
                        currentText.setLength(0);
                    }
                } else if (event == XMLStreamConstants.CHARACTERS && insideOntology && currentLocalName != null) {
                    currentText.append(reader.getText());
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    if (!insideOntology) {
                        continue;
                    }
                    String localName = reader.getLocalName();
                    if ("Ontology".equals(localName) && depthInsideOntology == 0) {
                        // The whole header block has been read - stop here, deliberately
                        // ignoring the rest of the (potentially very large) file.
                        break;
                    }
                    if (currentLocalName != null && currentLocalName.equals(localName)) {
                        values.put(currentLocalName, currentText.toString().trim());
                        currentLocalName = null;
                    }
                    depthInsideOntology--;
                }
            }
        } finally {
            reader.close();
        }

        return new OntologyHeader(
                values.get("label"),
                values.get("scope"),
                values.get("scopedTo"),
                values.get("modified"),      // dc:modified -> local name "modified"
                values.get("versionInfo")    // owl:versionInfo -> local name "versionInfo"
        );
    }
}
