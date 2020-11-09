/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.ontology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.ontology.OntModel;
import eu.eurofleets.ears3.Application;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.core.StringContains.containsString;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

/**
 *
 * @author thomas
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = {"eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service"})
public class OntologyControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;

    /**
     * Test of uploadVesselOntology method, of class OntologyController.
     */
    @Test
    public void testUploadVesselOntology() throws Exception {
        System.out.println("uploadVesselOntology");

        MockMultipartFile multipartFile = getMultiPartFile("earsv2-onto-vessel.rdf");
        MockMultipartFile fakeMultipartFile = getMultiPartFile("fake.rdf");
        uploadVesselFile(multipartFile, "ears", "REPLACEME", 202).andExpect(content().string(containsString("File correctly saved")));
        uploadVesselFile(multipartFile, "ears", "wrong", 500).andExpect(content().string(containsString("Authentication failed")));
        uploadVesselFile(fakeMultipartFile, "ears", "wrong", 500).andExpect(content().string(containsString("Authentication failed")));
    }

    private MockMultipartFile getMultiPartFile(String fileName) throws IOException {
        InputStream onto = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileName);
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileName, MediaType.APPLICATION_OCTET_STREAM_VALUE, onto);
        return multipartFile;
    }

    private ResultActions uploadVesselFile(MockMultipartFile multipartFile, String user, String pass, int status) throws Exception {
        return this.mockMvc.perform(multipart("/ontology/vessel/upload").file(multipartFile).header(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString((user + ":" + pass).getBytes())))
                .andDo(print())
                .andExpect(status().is(status));
    }

    private ResultActions uploadProgramFile(MockMultipartFile multipartFile, int status) throws Exception {
        return this.mockMvc.perform(multipart("/ontology/program/upload").file(multipartFile))
                .andDo(print())
                .andExpect(status().is(status));
    }

    /**
     * Test of uploadProgramOntology method, of class OntologyController.
     */
    @Test
    public void testUploadProgramOntology() throws Exception {
        System.out.println("uploadProgramOntology");
        String fileName = "program-abc.rdf";
        MockMultipartFile multipartFile = getMultiPartFile(fileName);
        multipartFile.getOriginalFilename();
        uploadProgramFile(multipartFile, 202).andExpect(content().string(containsString("File correctly saved")));

        fileName = "program-abc_with_problem.rdf";
        multipartFile = getMultiPartFile(fileName);
        uploadProgramFile(multipartFile, 500).andExpect(content().string(containsString("Cannot save file: not recognized as a PROGRAM ontology")));
    }

    /**
     * Test of getVesselOntology method, of class OntologyController.
     */
    @Test
    public void testGetVesselOntology() throws Exception {
        System.out.println("getVesselOntology");
        this.mockMvc.perform(get("/ontology/vessel"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"earsv2-onto-vessel.rdf\""))
                .andExpect(content().string(containsString("<scope rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">VESSEL</scope>")));
    }

    /**
     * Test of getVesselOntologyDate method, of class OntologyController.
     */
    @Test
    public void testGetVesselOntologyDate() throws Exception {
        System.out.println("getVesselOntologyDate");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Accept", "text/plain");
        this.mockMvc.perform(get("/ontology/vessel/date").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")));

    }

    /**
     * Test of getProgramOntology method, of class OntologyController.
     */
    @Test
    public void testGetProgramOntology() throws Exception {
        System.out.println("getProgramOntology");
        this.mockMvc.perform(get("/ontology/program?programIdentifier=program-abc"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"program-abc.rdf\""))
                .andExpect(content().string(containsString("<scope rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">PROGRAM</scope>")));

        this.mockMvc.perform(get("/ontology/program?programIdentifier=program-abc.rdf"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"program-abc.rdf\""))
                .andExpect(content().string(containsString("<scope rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">PROGRAM</scope>")));

        this.mockMvc.perform(get("/ontology/program?programIdentifier=program-ABC"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<scope rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">PROGRAM</scope>")));

    }

    /**
     * Test of getProgramOntologyDate method, of class OntologyController.
     */
    @Test
    public void testGetProgramOntologyDate() throws Exception {
        System.out.println("getProgramOntologyDate");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Accept", "text/plain");
        this.mockMvc.perform(get("/ontology/program/date?programIdentifier=program-ABC").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")));
    }

    /**
     * Test of canAuthenticate method, of class OntologyController.
     */
    @Test
    public void testCanAuthenticate() throws Exception {
        System.out.println("canAuthenticate");
        HttpHeaders httpHeaders = new HttpHeaders();

        String user = "ears";
        String pass = "REPLACEME";
        httpHeaders.add("Accept", "text/plain");
        httpHeaders.add(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString((user + ":" + pass).getBytes()));
        this.mockMvc.perform(get("/ontology/authenticate").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("true")));

        user = "ears";
        pass = "wrong";
        httpHeaders.remove(HttpHeaders.AUTHORIZATION);
        httpHeaders.add(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64Utils.encodeToString((user + ":" + pass).getBytes()));
        this.mockMvc.perform(get("/ontology/authenticate").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("false")));
    }

    /**
     * Test of sparqlEndpoint method, of class OntologyController.
     */
    @Test
    public void testSparqlEndpoint() throws Exception {
        System.out.println("sparqlEndpoint");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Accept", "application/json");

        String q = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
                + "PREFIX skos:<http://www.w3.org/2004/02/skos/core#>\n"
                + "PREFIX ears2:<http://ontologies.ef-ears.eu/ears2/1#>\n"
                + "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>\n"
                + "SELECT DISTINCT ?eid (str(?c) as ?cu) ?cl (str(?t)  as ?tu) ?tl (str(?p) as ?pu) ?pl (str(?a) as ?au) ?al\n"
                + "WHERE {\n"
                + "{\n"
                + "OPTIONAL {\n"
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
                + " }\n"
                + "\n"
                + "}";
        this.mockMvc.perform(get("/ontology/vessel/sparql?q=" + UriUtils.encode(q, "UTF8")).headers(httpHeaders))
                // .andDo(print())
                .andExpect(status().isOk());

    }
}
