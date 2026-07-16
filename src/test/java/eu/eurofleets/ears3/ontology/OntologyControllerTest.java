/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.ontology;

import eu.eurofleets.ears3.Application;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.core.StringContains.containsString;

import eu.eurofleets.ears3.utilities.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.springframework.web.util.UriUtils;

@ActiveProfiles("test")
@SpringBootTest(classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = {"eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
//reset the database to base state before each test method
public class OntologyControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testGetVesselOntology() throws Exception {
        this.mockMvc.perform(get("/api/ontology"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"earsv2-onto-vessel.rdf\""))
                .andExpect(content().string(containsString(
                        "<scope rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">VESSEL</scope>")));
    }

    @Test
    public void testGetVesselOntologyDate() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Accept", "text/plain");
        this.mockMvc.perform(get("/api/ontology/date").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(matchesPattern("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*")));

    }

    @Test
    public void testSparqlEndpoint() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Accept", "application/json");

        String q = """
                PREFIX owl: <http://www.w3.org/2002/07/owl#>
                PREFIX dc: <http://purl.org/dc/elements/1.1/>
                PREFIX skos:<http://www.w3.org/2004/02/skos/core#>
                PREFIX ears2:<http://ontologies.ef-ears.eu/ears2/1#>
                PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>
                SELECT DISTINCT ?eid (str(?c) as ?cu) ?cl (str(?t)  as ?tu) ?tl (str(?p) as ?pu) ?pl (str(?a) as ?au) ?al
                WHERE {
                {
                OPTIONAL {
                ?c a ears2:ToolCategory.
                ?t a ears2:Tool.
                ?e ears2:hasProcess ?p.
                ?e ears2:hasAction ?a.\s
                ?e ears2:withTool ?t.\s
                ?t ears2:isMemberOf ?c.
                ?e ears2:asConcept ?ec.
                ?ec dc:identifier ?eid.
                ?c ears2:asConcept ?cc.
                ?cc skos:prefLabel ?cl .
                ?t ears2:asConcept ?tc.
                ?tc skos:prefLabel ?tl .
                ?p ears2:asConcept ?pc.
                ?pc skos:prefLabel ?pl .
                ?a ears2:asConcept ?ac.
                ?ac skos:prefLabel ?al  }
                 }
                }""";
        q = UriUtils.encode(q, "UTF8");
        System.out.println(q);
        this.mockMvc.perform(
                        get("/api/ontology/sparql?q=" + q).headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"cu\": { \"type\": \"literal\" , \"value\": \"http://ontologies.ef-ears.eu/ears2/1#ctg_19\" }")));

    }

    @Test
    public void testCanAuthenticate() throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        String user = "earsontology";
        String pass = "REPLACEME";
        httpHeaders.add("Accept", "text/plain");
        httpHeaders.add(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes()));
        this.mockMvc.perform(get("/api/ontology/authenticate").headers(httpHeaders))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("true")));

        user = "ears";
        pass = "wrong";
        httpHeaders.remove(HttpHeaders.AUTHORIZATION);
        httpHeaders.add(HttpHeaders.AUTHORIZATION,
                "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes()));
        this.mockMvc.perform(get("/api/ontology/authenticate").headers(httpHeaders))
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("false")));
    }

    @Test
    public void testUploadVesselOntology() throws Exception {
        MockMultipartFile multipartFile = getMultiPartFile("earsv2-onto-vessel.rdf");
        MockMultipartFile fakeMultipartFile = getMultiPartFile("fake.rdf");
        uploadVesselFile(multipartFile, "earsontology", "REPLACEME", 202)
                .andExpect(content().string(containsString("File correctly saved")));
        uploadVesselFile(multipartFile, "ears", "wrong", 401)
                .andExpect(content().string(containsString("401 UNAUTHORIZED \"Invalid or missing credentials for this ontology operation.\"")));
        uploadVesselFile(fakeMultipartFile, "ears", "wrong", 401)
                .andExpect(content().string(containsString("401 UNAUTHORIZED \"Invalid or missing credentials for this ontology operation.\"")));
    }

    @Test
    public void testIngestVesselOntology() throws Exception {
        MockMultipartFile multipartFile = getMultiPartFile("earsv2-onto-vessel.rdf");
        ingestVesselFile(multipartFile, "earsontology", "REPLACEME", 202)
                .andExpect(content().string(containsString("File correctly saved")));
    }


    private MockMultipartFile getMultiPartFile(String fileName) throws IOException {
        InputStream onto = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(fileName);
        MockMultipartFile multipartFile = new MockMultipartFile("file", fileName,
                MediaType.APPLICATION_OCTET_STREAM_VALUE, onto);
        return multipartFile;
    }

    private ResultActions uploadVesselFile(MockMultipartFile multipartFile, String user, String pass, int status)
            throws Exception {
        return this.mockMvc
                .perform(multipart("/api/ontology/stage-from-file").file(multipartFile).accept(Constants.APPLICATION_XML_UTF8).header(
                        HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes())))
                .andDo(print())
                .andExpect(status().is(status));
    }

    private ResultActions ingestVesselFile(MockMultipartFile multipartFile, String user, String pass, int status)
            throws Exception {
        return this.mockMvc
                .perform(multipart("/api/ontology/ingest").file(multipartFile).accept(Constants.APPLICATION_XML_UTF8).header(
                        HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes())))
                .andDo(print())
                .andExpect(status().is(status));
    }



}
