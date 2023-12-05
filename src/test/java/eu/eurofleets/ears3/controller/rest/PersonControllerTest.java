/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import static eu.eurofleets.ears3.controller.rest.EventControllerTest.deleteEvent;
import static eu.eurofleets.ears3.controller.rest.EventControllerTest.getTestEvent;
import static eu.eurofleets.ears3.controller.rest.EventControllerTest.postEvent;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import static org.hamcrest.core.StringContains.containsString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Thomas Vandenberghe
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = { "eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service" })
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD) //reset the database to base state before each test method
public class PersonControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        /*   MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/sync/all")) //first we need all the harbours, countries etc in the system
                //.andDo(print())
                .andExpect(status().isOk())
                .andReturn();*/
    }

    @Autowired
    private ObjectMapper objectMapper;

    public static void postPerson(String jsonPerson, MockMvc mockMvc) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/person").contentType(MediaType.APPLICATION_JSON)
                .content(jsonPerson))
                // .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    /**
     * Test of getPersonByFullName method, of class PersonController.
     */
    @Test
    public void testGetPersonByFullName() throws Exception {
        //first run the EventControllerTest test.

        EventDTO e = getTestEvent();
        String programIdentifier = "2020-MF";
        ProgramDTO pr = ProgramControllerTest.getTestProgram1(programIdentifier);

        String json = objectMapper.writeValueAsString(pr);

        MvcResult mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/api/program").contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn();

        String firstName = "Adalbert";
        String lastName = "Hoogendrave";

        e.getActor().setFirstName(firstName);
        e.getActor().setLastName(lastName);
        e.getActor().setEmail("sol.invictus@hubris.org");

        e.setProgram(programIdentifier);

        String email = e.getActor().getEmail();
        String platform = e.getPlatform();
        json = objectMapper.writeValueAsString(e);

        MvcResult postEvent = postEvent(this.mockMvc, e, this.objectMapper);

        mvcResult = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/person?fullName=" + firstName + " " + lastName))
                //.andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().string(containsString(
                        "<firstName>" + firstName + "</firstName><lastName>" + lastName + "</lastName>")))
                .andReturn();
        deleteEvent(this.mockMvc, postEvent);
    }

    public static void assertPersonCount(MockMvc mockMvc, int testNumber) throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/api/persons").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.persons.length()").value(testNumber))
                .andReturn();
    }

    public static void deleteAllPersons(MockMvc mockMvc) throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/persons").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();
    }
}
