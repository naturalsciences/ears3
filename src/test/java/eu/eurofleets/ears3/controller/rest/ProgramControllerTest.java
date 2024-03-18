/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
public class ProgramControllerTest {

        @Autowired
        private WebApplicationContext wac;

        private MockMvc mockMvc;

        @Before
        public void setup() throws Exception {
                this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        }

        @After
        public void after() throws Exception {

        }

        @Autowired
        private ObjectMapper objectMapper;

        // Methods to get mock objects

        public static ProgramDTO getTestProgram1(String identifier) {
                List<PersonDTO> principalInvestigators1 = Arrays.asList(new PersonDTO[] {
                                new PersonDTO("Katrijn", "Baetens", "SDN:EDMO::3330", "02/2209091", "02/2208081",
                                                "kbaetens@naturalsciences.be"),
                                new PersonDTO("Valérie", "Dulière", "SDN:EDMO::3330", "02/2209090", "02/2208080",
                                                "vduliere@naturalsciences.be") });
                return new ProgramDTO(identifier, principalInvestigators1,
                                "validating the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. ",
                                null, "RUBBER-DUCK", "No sampling");
        }

        public static ProgramDTO getTestProgram2(String identifier) {
                List<PersonDTO> principalInvestigators2 = Arrays.asList(new PersonDTO[] {
                                new PersonDTO("Kris", "Hostens", "SDN:EDMO::ILVO", "057/2209091", "057/2208081",
                                                "khostens@ilvo.be") });
                return new ProgramDTO("KH1", principalInvestigators2, "Fisheries monitoring", null,
                                "Fisheries monitoring",
                                "Beam trawl 4 and 8m");
        }

        // Utility Methods

        public static MvcResult postProgram(MockMvc mockMvc, ProgramDTO pr, ObjectMapper objectMapper)
                        throws Exception {
                String json = objectMapper.writeValueAsString(pr);
                return mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/program")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isCreated())
                                .andReturn();
        }

        public static void deleteProgram(String identifier, MockMvc mockMvc) throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.delete("/api/program?identifier=" + identifier))
                                // .andDo(print())
                                .andExpect(status().is(204)).andReturn();
        }

        public static void deleteAllPrograms(MockMvc mockMvc) throws Exception {

                MvcResult mvcResult = mockMvc
                                .perform(MockMvcRequestBuilders.get("/api/programs").accept(MediaType.APPLICATION_JSON))
                                .andReturn();

                for (String identifier : getIdentifiersFromJson(mvcResult)) { // delete all previous events
                        mvcResult = mockMvc
                                        .perform(MockMvcRequestBuilders
                                                        .delete(String.format("/api/program?identifier=%s",
                                                                        identifier)))
                                        .andExpect(status().is(204)).andReturn();
                }
        }

        private static Set<String> getIdentifiersFromJson(MvcResult mvcResult) throws UnsupportedEncodingException {
                String contentAsString = mvcResult.getResponse().getContentAsString();
                Pattern p = Pattern.compile("\\{\"identifier\":\"(.*?)\"");
                Matcher m = p.matcher(contentAsString);
                Set<String> allMatches = new HashSet<String>();

                while (m.find()) {
                        allMatches.add(m.group(1));
                }
                return allMatches;
        }

        /**
         * Test of getPrograms method, of class ProgramController.
         */
        @Test
        public void testGetPrograms() throws Exception {
                ProgramDTO pr = getTestProgram1("2020-MF");
                postProgram(this.mockMvc, pr, objectMapper);

                MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/programs"))
                                // .andDo(print())
                                .andExpect(status().is(200))
                                .andExpect(content().string(containsString("<identifier>2020-MF</identifier>")))
                                .andExpect(content().string(not(containsString("ValÃ©rie")))).andReturn();
        }

        @Autowired
        private RequestMappingHandlerMapping requestMappingHandlerMapping;

        @Test
        public void testGetProgramsByDate() throws Exception {
                // delete all previous events and programs
                EventControllerTest.deleteAllEvents(this.mockMvc);
                deleteAllPrograms(this.mockMvc);

                requestMappingHandlerMapping.getHandlerMethods().keySet();

                CruiseDTO testCruise = CruiseControllerTest.getTestCruise1("SEASHELL-23");
                OffsetDateTime start = Instant.now().minus(20, ChronoUnit.DAYS).atOffset(ZoneOffset.UTC);
                OffsetDateTime end = Instant.now().atOffset(ZoneOffset.UTC);
                OffsetDateTime lateStart = start;
                testCruise.startDate = start;
                testCruise.endDate = end;
                testCruise.programs = new ArrayList<>();
                testCruise.programs.add("PR-SEASHELL-20");
                ProgramDTO pr = getTestProgram1("PR-SEASHELL-20");
                postProgram(this.mockMvc, pr, objectMapper);
                CruiseControllerTest.postCruise(this.mockMvc, testCruise, this.objectMapper);

                testCruise = CruiseControllerTest.getTestCruise1("SEASHELL-19");
                start = OffsetDateTime.parse("2019-04-25T11:08:00Z");
                end = OffsetDateTime.parse("2019-04-29T11:08:00Z");
                OffsetDateTime earlyStart = start;
                testCruise.startDate = start;
                testCruise.endDate = end;
                testCruise.programs = new ArrayList<>();
                testCruise.programs.add("2019-ILVO-FISHY");
                pr = getTestProgram1("2019-ILVO-FISHY");
                postProgram(this.mockMvc, pr, objectMapper);
                CruiseControllerTest.postCruise(this.mockMvc, testCruise, this.objectMapper);

                /*
                 * this.mockMvc.perform(MockMvcRequestBuilders.get(
                 * "/api/programs?cruiseIdentifier=SEASHELL-20"))
                 * // .andDo(print())
                 * .andExpect(status().is(200))
                 * .andExpect(content().string(containsString(
                 * "<identifier>PR-SEASHELL-20</identifier>")))
                 * .andExpect(content().string(not(containsString(
                 * "<identifier>2019-ILVO-FISHY</identifier>")))).andReturn();
                 */
                this.mockMvc
                                .perform(MockMvcRequestBuilders
                                                .get("/api/programs?startDate="
                                                                + lateStart.format(DateTimeFormatter.ISO_DATE_TIME))
                                                .contentType(MediaType.APPLICATION_JSON)) // + "&endDate=" +
                                // end.format(DateTimeFormatter.ISO_DATE_TIME)
                                .andDo(print())
                                .andExpect(status().is(200))
                                .andExpect(content().string(containsString("<identifier>PR-SEASHELL-20</identifier>")))
                                .andExpect(content().string(
                                                not(containsString("<identifier>2019-ILVO-FISHY</identifier>"))))
                                .andReturn();
                CruiseControllerTest.deleteCruise(testCruise.identifier, mockMvc);

                // delete all previous events and programs
                EventControllerTest.deleteAllEvents(this.mockMvc);
                deleteAllPrograms(this.mockMvc);

        }

        /**
         * Test of getProgramById method, of class ProgramController.
         */
        @Test
        public void testGetProgramById() throws Exception {
                ProgramDTO pr = getTestProgram1("2020-MF");
                postProgram(this.mockMvc, pr, objectMapper);
                ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/program?identifier=2020-MF"))
                                // .andDo(print())
                                .andExpect(status().is(200))
                                .andExpect(content().string(containsString("<identifier>2020-MF</identifier>")))
                                .andReturn();
        }

        private static UUID programUUID;
        private static String programId;

        @Test
        public void testPostProgram() throws Exception {
                // delete all previous events and programs
                EventControllerTest.deleteAllEvents(this.mockMvc);
                deleteAllPrograms(this.mockMvc);

                programUUID = UUID.randomUUID();
                ProgramDTO program = getTestProgram1("KB_" + programUUID);
                String json = objectMapper.writeValueAsString(program);

                this.mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/program")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                // .andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(content().string(
                                                containsString("<identifier>KB_" + programUUID + "</identifier>")))
                                .andExpect(content().string(containsString(
                                                "<description>" + program.description + "</description>")))
                                .andReturn();

                // delete all previous events and programs
                EventControllerTest.deleteAllEvents(this.mockMvc);
                deleteAllPrograms(this.mockMvc);
        }

        @Test
        public void testPostProgram2() throws Exception {
                // delete all previous events and programs
                EventControllerTest.deleteAllEvents(this.mockMvc);
                deleteAllPrograms(this.mockMvc);

                programUUID = UUID.randomUUID();

                PersonControllerTest.assertPersonCount(this.mockMvc, 0);

                //Create someone
                PersonDTO person = new PersonDTO("Bob", "Rumes", "SDN:EDMO::3327", null, null,
                                "brumes@naturalsciences.be");
                String jsonPerson = objectMapper.writeValueAsString(person);
                PersonControllerTest.postPerson(jsonPerson, this.mockMvc);

                //Create a program with a mistaken email address. Someone uses the email adress of another person.
                //This person should be created in the database as a new person
                //And the program should be accepted.
                String json = "{\"principalInvestigators\":[{\"firstName\":\"Danae\",\"lastName\":\"Kapasakali\",\"organisation\":\"SDN:EDMO::3327\",\"email\":\"brumes@naturalsciences.be\"},{\"firstName\":\"Steven\",\"lastName\":\"Degraer\",\"organisation\":\"SDN:EDMO::3327\",\"email\":\"sdegraer@naturalsciences.be\"}],\"identifier\":\""
                                + programUUID + "\",\"sampling\":\"deployment and retrieval of ARMS\"" +
                                ",\"name\":\"EDEN2000: Exploring options for a nature-proof development\",\"description\":\"In the framework of the 'EDEN2000' project (2019-2022; FPS Health, Food Chain Safety and Environment) a number of studies have been drafted.\"}";
                this.mockMvc.perform(MockMvcRequestBuilders.post("/api/program").contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                // .andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(content().string(
                                                containsString("<identifier>" + programUUID + "</identifier>")))
                                .andExpect(content().string(containsString(
                                                "<description>In the framework of the 'EDEN2000' project (2019-2022")))
                                .andReturn();

                PersonControllerTest.assertPersonCount(this.mockMvc, 3);
                // delete all previous events and programs
                EventControllerTest.deleteAllEvents(this.mockMvc);
                //PersonControllerTest.deleteAllPersons(this.mockMvc);

                deleteAllPrograms(this.mockMvc);
        }

        @Test
        @Ignore
        public void testGetProgramByidentifier() {
                String identifier = "";
                ProgramController instance = new ProgramController();
                Program expResult = null;
                Program result = instance.getProgramByidentifier(identifier);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                // fail("The test case is a prototype.");
        }

        @Test
        @Ignore
        public void testRemoveProgramByIdentifier() {
                String identifier = "";
                ProgramController instance = new ProgramController();
                String expResult = "";
                String result = instance.removeProgramByIdentifier(identifier);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                // fail("The test case is a prototype.");
        }

        @Test
        @Ignore
        public void testRemoveProgramById() {
                String id = "";
                ProgramController instance = new ProgramController();
                String expResult = "";
                String result = instance.removeProgramById(id);
                assertEquals(expResult, result);
                // TODO review the generated test code and remove the default call to fail.
                // fail("The test case is a prototype.");
        }

}
