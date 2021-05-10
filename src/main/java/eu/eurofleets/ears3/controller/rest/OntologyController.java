package eu.eurofleets.ears3.controller.rest;

import be.naturalsciences.bmdc.ontology.IOntologyModel;
import be.naturalsciences.bmdc.ontology.OntologyConstants;
import be.naturalsciences.bmdc.ontology.writer.ScopeMap;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import eu.eurofleets.ears3.domain.Message;
import eu.eurofleets.ears3.ontology.storage.StorageService;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
//import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * This example shows how to build Java REST web-service to upload files
 * accepting POST requests with encoding type "multipart/form-data". For more
 * details please read the full tutorial on
 * https://javatutorial.net/java-file-upload-rest-service
 *
 * @author javatutorial.net
 */
@RestController
@RequestMapping(value = "/ontology")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OntologyController {

    private final StorageService storageService;

    @Autowired
    public OntologyController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    private Environment env;

    private String getUsername() {
        return env.getProperty("ears.ontology.username");
    }

    private String getPassword() {
        return env.getProperty("ears.ontology.password");
    }

    public boolean authenticate(String username, String password) {
        return username.equals(getUsername()) && password.equals(getPassword());
    }

    /**
     * The path to the folder where we want to store the uploaded files
     */
    private static final String ONTOLOGY_DIR = "/var/www/ears2/";

    private static final String VESSEL_ONTOLOGY_FILE_NAME = "earsv2-onto-vessel.rdf";

    private static final String VESSEL_ONTOLOGY_FILE_LOCATION = ONTOLOGY_DIR + VESSEL_ONTOLOGY_FILE_NAME;

    @PostMapping(value = {"vessel/upload"}/*, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {"application/xml", "application/json"}*/)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Message> uploadVesselOntology(@RequestHeader("authorization") String authorization, @RequestParam("file") MultipartFile file) throws IOException {
        return uploadOntology(file, authorization, true, ScopeMap.Scope.VESSEL, VESSEL_ONTOLOGY_FILE_NAME);
    }

    @PostMapping(value = {"program/upload"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {"application/xml; charset=utf-8", "application/json"})
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Message> uploadProgramOntology(@RequestParam("file") MultipartFile file) throws IOException {
        return uploadOntology(file, null, false, ScopeMap.Scope.PROGRAM, file.getOriginalFilename());
    }

    private ResponseEntity<Message> uploadOntology(MultipartFile file, String authorization, boolean authenticate, ScopeMap.Scope matchScope, String overwriteWithFileName) throws IOException {
        if (authenticate) {
            if (authorization != null && authorization.startsWith("Basic")) {
                String encodedString = authorization.replaceAll("Basic ", "");
                Map<String, String> decode = decodeBase64(encodedString);
                if (!authenticate(decode.get("username"), decode.get("password"))) {
                    Message m = new Message("Authentication failed: wrong credentials", 500, null, null, null);
                    return ResponseEntity.status(500).contentType(MediaType.APPLICATION_XML).body(m);//.build();
                }
            } else {
                Message m = new Message("Cannot authenticate", 500, null, null, null);
                return ResponseEntity.status(500).contentType(MediaType.APPLICATION_XML).body(m);//.build();
            }
        }
        Map<String, String> staticStuff = IOntologyModel.getStaticStuff(file.getInputStream());
        if (staticStuff == null || (staticStuff.get(IOntologyModel.SCOPE) != null && !staticStuff.get(IOntologyModel.SCOPE).equals(matchScope.name()))) {
            Message m = new Message("Cannot save file: not recognized as a " + matchScope.name() + " ontology", 500, null, null, null);
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_XML).body(m);//.build();
        }
        storageService.store(file, overwriteWithFileName);
        Message m = new Message("File correctly saved", 202, overwriteWithFileName, null, null);
        //  return ResponseEntity.status(202).contentType(MediaType.APPLICATION_XML).body(m);
        return new ResponseEntity<Message>(m, HttpStatus.ACCEPTED);

    }

    private ResponseEntity getFile(String fileName, String type) {
        Resource file;
        try {
            file = storageService.loadAsResource(fileName);
            if (file.exists()) {
                return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename() + "\"").body(file);
            } else {
                Message m = new Message("Cannot return the " + type + " ontology file because it can't be found on the server.", 500, null, null, null);
                return ResponseEntity.status(500).contentType(MediaType.APPLICATION_XML).body(m);
            }
        } catch (IOException ex) {
            Message m = new Message("Cannot return the " + type + " ontology file because it can't be found on the server.", 500, null, null, null);
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_XML).body(m);
        }
    }

    private ResponseEntity<Message> getFileDate(String fileName, String type) {
        Resource file;
        try {
            file = storageService.loadAsResource(fileName);
            if (file.exists()) {
                try {
                    String dateModified = IOntologyModel.getStaticStuff(file.getInputStream()).get(IOntologyModel.DATEMODIFIED);
                    String dateVersionInfo = IOntologyModel.getStaticStuff(file.getInputStream()).get(IOntologyModel.VERSIONINFO);
                    ResponseEntity response = null;
                    if (dateModified != null) {
                        response = ResponseEntity.ok(dateModified);
                    } else if (dateVersionInfo != null) {
                        response = ResponseEntity.ok(dateVersionInfo);
                    }
                    //response.header("Content-Type", "text/plain;charset=utf-8");
                    return response;
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(OntologyController.class.getName()).log(Level.SEVERE, null, ex);
                    Message m = new Message("Cannot return the " + type + " ontology date because the file can't be found on the server.", 500, null, null, null);
                    return ResponseEntity.status(500).contentType(MediaType.APPLICATION_XML).body(m);
                }
            } else {
                Message m = new Message("Cannot return the " + type + " ontology date because the file can't be found on the server.", 500, null, null, null);
                return ResponseEntity.status(500).contentType(MediaType.APPLICATION_XML).body(m);
            }
        } catch (IOException ex) {
            Message m = new Message("Cannot return the " + type + " ontology date because the file can't be found on the server.", 500, null, null, null);
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_XML).body(m);
        }

    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"vessel"})
    public ResponseEntity getVesselOntology() {
        return getFile(VESSEL_ONTOLOGY_FILE_LOCATION, "vessel");
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"vessel/date"}, produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Message> getVesselOntologyDate() {
        return getFileDate(VESSEL_ONTOLOGY_FILE_LOCATION, "vessel");
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"program"})
    public ResponseEntity getProgramOntology(@RequestParam("programIdentifier") String programIdentifier) {
        if (programIdentifier == null) {
            Message m = new Message("Cannot return reponse as the get parameter 'name' is not provided.", 500, null, null, null);
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_XML).body(m);
        } else {
            String fileName = null;
            if (programIdentifier.contains(".rdf")) {
                fileName = cleanProgramName(programIdentifier);
            } else {
                fileName = cleanProgramName(programIdentifier + ".rdf");
            }
            return getFile(ONTOLOGY_DIR + fileName, "program");
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"program/date"}, produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<Message> getProgramOntologyDate(@RequestParam("programIdentifier") String programIdentifier) {
        if (programIdentifier == null) {
            Message m = new Message("Cannot return date as the get parameter 'name' is not provided.", 500, null, null, null);
            return ResponseEntity.status(500).contentType(MediaType.APPLICATION_XML).body(m);
        } else {
            String fileName = null;
            if (programIdentifier.contains(".rdf")) {
                fileName = cleanProgramName(programIdentifier);
            } else {
                fileName = cleanProgramName(programIdentifier + ".rdf");
            }
            return getFileDate(ONTOLOGY_DIR + fileName, "program");
        }
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"authenticate"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public String canAuthenticate(@RequestHeader("Authorization") String authorization) {
        if (authorization != null) {
            if (authorization != null && authorization.startsWith("Basic")) {
                String encodedString = authorization.replaceAll("Basic ", "");
                Map<String, String> decode = decodeBase64(encodedString);

                String username = decode.get("username");
                String password = decode.get("password");
                if (username != null && password != null && authenticate(decode.get("username"), decode.get("password"))) {
                    return Boolean.TRUE.toString();
                } else {
                    return Boolean.FALSE.toString();
                }
            } else {
                return Boolean.FALSE.toString();
            }
        }
        return Boolean.FALSE.toString();
    }

    public OntModel getOntModel(File ontologyFile) throws IOException {
        java.nio.file.Path ontologyPath = ontologyFile.toPath();

        OntModel jenaModel = ModelFactory.createOntologyModel();

        //  com.hp.hpl.jena.rdf.model.Resource ontology = jenaModel.getResource(OntologyConstants.EARS2_NS);
        InputStream in = FileManager.get().open(ontologyPath.toString());
        if (in == null) {
            throw new FileNotFoundException("The provided file (" + ontologyPath.getFileName().toString() + ") was not found.");
        }

        try {
            jenaModel.read(in, OntologyConstants.EARS2_NS);
        } catch (Exception e) {
            throw new IOException("There was an error with creating the jena model from the rdf file.", e);
        }
        return jenaModel;
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"vessel/sparql"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String vesselSparqlEndpoint(@RequestParam(required = true, value = "q") String sparqlQuery, @RequestParam(required = false, value = "program") String program) throws IOException {
        sparqlQuery = UriUtils.decode(sparqlQuery, "UTF8");
        ResultSet rs;
        if (program == null) {
            Query qry = QueryFactory.create(sparqlQuery);
            File ontologyFile = new File(VESSEL_ONTOLOGY_FILE_LOCATION);
            QueryExecution qe = QueryExecutionFactory.create(qry, getOntModel(ontologyFile));
            rs = qe.execSelect();
        } else {
            rs = combineOntologyModels(sparqlQuery, program);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, rs);
        return new String(outputStream.toByteArray());
    }

    public ResultSet combineOntologyModels(String sparqlQuery, String program) throws IOException {
        Model results = ModelFactory.createDefaultModel();
        results.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
        results.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
        results.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
        results.setNsPrefix("ears2", "http://ontologies.ef-ears.eu/ears2/1#");
        results.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");

        String query = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
                + "PREFIX skos:<http://www.w3.org/2004/02/skos/core#>\n"
                + "PREFIX ears2:<http://ontologies.ef-ears.eu/ears2/1#>\n"
                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>\n"
                + "CONSTRUCT WHERE {\n"
                + "\n"
                + "?c a ears2:ToolCategory.\n"
                + "?t a ears2:Tool.\n"
                + "?e ears2:hasProcess ?p.\n"
                + "?e ears2:hasAction ?a. \n"
                + "?e ears2:withTool ?t. \n"
                + "?t ears2:isMemberOf ?c.\n"
                + "?e ears2:asConcept ?ec.\n"
                + "?ec dc:identifier ?eid.\n"
                + "?c ears2:asConcept ?cc.\n"
                + "?cc skos:prefLabel ?cl .\n"
                + "?t ears2:asConcept ?tc.\n"
                + "?tc skos:prefLabel ?tl .\n"
                + "?p ears2:asConcept ?pc.\n"
                + "?pc skos:prefLabel ?pl .\n"
                + "?a ears2:asConcept ?ac.\n"
                + "?ac skos:prefLabel ?al  }\n"
                + " \n"
                + "ORDER BY ?pl ?al";

        Query qry = QueryFactory.create(sparqlQuery);
        File vesselOntologyFile = new File(VESSEL_ONTOLOGY_FILE_LOCATION);
        QueryExecution qe = QueryExecutionFactory.create(query, getOntModel(vesselOntologyFile));
        Model vesselResult = qe.execConstruct();
        results.add(vesselResult);

        File programOntologyFile = new File(ONTOLOGY_DIR, program + ".rdf");
        qe = QueryExecutionFactory.create(query, getOntModel(programOntologyFile));
        Model programResult = qe.execConstruct();
        results.add(programResult);

        programResult.write(System.out, "RDF/XML-ABBREV");
        //Model union = ModelFactory.createUnion(vesselResult, programResult);
        qe = QueryExecutionFactory.create(qry, results);
        return qe.execSelect();
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"program/sparql"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public String programSparqlEndpoint(@RequestParam(required = true, value = "q") String sparqlQuery, @RequestParam(required = true, value = "programIdentifier") String programIdentifier) throws IOException {
        sparqlQuery = UriUtils.decode(sparqlQuery, "UTF8");
        Query qry = QueryFactory.create(sparqlQuery);
        String cleanProgramName = cleanProgramName(programIdentifier);
        File ontologyFile = new File(ONTOLOGY_DIR, cleanProgramName + ".rdf");
        QueryExecution qe = QueryExecutionFactory.create(qry, getOntModel(ontologyFile));
        ResultSet rs = qe.execSelect();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(outputStream, rs);
        return new String(outputStream.toByteArray());
    }

    public String cleanProgramName(String programName) {
        List<String> replacements = new ArrayList<>();
        replacements.add(" ");
        replacements.add("_");
        replacements.add("<");
        replacements.add(">");
        replacements.add(":");
        replacements.add("\"");
        replacements.add("/");
        replacements.add("\\");
        replacements.add("|");
        replacements.add("?");
        replacements.add("*");
        for (String replacement : replacements) {
            programName = programName.replace(replacement, "_");
        }
        return programName.toLowerCase();
    }

    /**
     * *
     * Decode an Base64-encoded username:password pair to a Map<String,String>.
     *
     * @param encodedString
     * @return
     */
    private static Map decodeBase64(final String encodedString) {
        final byte[] decodedBytes = Base64.decodeBase64(encodedString.getBytes());
        final String pair = new String(decodedBytes);
        final String[] userDetails = pair.split(":", 2);
        Map<String, String> map = new HashMap();
        if (userDetails.length == 2) {
            map.put("username", userDetails[0]);
            map.put("password", userDetails[1]);
        }
        return map;
    }
}
