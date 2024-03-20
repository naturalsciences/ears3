package eu.eurofleets.ears3.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Property;
import eu.eurofleets.ears3.dto.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import eu.eurofleets.ears3.service.EventRepository;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.LinkedDataTermService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.util.SocketUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.sound.midi.Soundbank;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = { "eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service" })
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) //reset the database to base state before each test method
public class EventExcelInputControllerTest {

        @Autowired
        private WebApplicationContext wac;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private LinkedDataTermService linkedDataTermService;

        @Autowired
        private EventService eventService;

        private MockMvc mockMvc;

        /* public EventExcelInputControllerTest() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        
        } */

        @Before
        public void setup() throws Exception {
                this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        }

        PersonDTO joan = new PersonDTO("Joan", "Backers", null, null, null, "joan.backers@naturalsciences.be");
        PersonDTO notJoan = new PersonDTO("NotJoan", "NotJoan", null, null, null, "Notjoan.backers@naturalsciences.be");

        @Test
        public void validateOkSubmitter() throws Exception {
                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                        "file",
                        "test-5problems.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // MediaType.APPLICATION_OCTET_STREAM_VALUE,
                        new ClassPathResource("test-5problems.xlsx").getInputStream());
                assertTrue(this.mockMvc != null);


                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/excelImport")
                        .file(mockMultipartFile).accept(MediaType.APPLICATION_JSON)
                        .param("person", objectMapper.writeValueAsString(this.joan)))
                        .andDo(print())
                        .andExpect(status().is(409));  //we used the 5problems excel as inputfile so it is expected to get a 409
        }

        /* //Test no longer useful as Thomas mentioned we don't need to concern ourselves with whether the user exists in DB
        @Test
        public void validateNietOkSubmitter() throws Exception {
                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                        "file",
                        "test-5problems.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // MediaType.APPLICATION_OCTET_STREAM_VALUE,
                        new ClassPathResource("test-5problems.xlsx").getInputStream());
                assertTrue(this.mockMvc != null);


                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/excelImport")

                                .file(mockMultipartFile).accept(MediaType.APPLICATION_JSON)
                                //.header("person", joan.getFirstName() + '#' + joan.getLastName()))
                                .param("person", objectMapper.writeValueAsString(this.notJoan)))
                        .andDo(print())
                        .andExpect(status().is(417)); //notJoan does not exist in the testdatabase (only joan was inserted) so we expect 417
        }
        */
        @Test
        public void validateErrorFile() throws Exception {
                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                                "file",
                                "test-5problems.xlsx",
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // MediaType.APPLICATION_OCTET_STREAM_VALUE,
                                new ClassPathResource("test-5problems.xlsx").getInputStream());
                assertTrue(this.mockMvc != null);

                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/excelImport")
                        .file(mockMultipartFile).accept(MediaType.APPLICATION_JSON)
                        //.header("person", joan.getFirstName() + '#' + joan.getLastName()))
                        .param("person", objectMapper.writeValueAsString(this.joan)))
                        .andDo(print())
                        .andExpect(status().is(409))
                        .andExpect(content().string(containsString("\"row\":1")))
                        .andExpect(content().string(containsString("\"row\":2")))
                        .andExpect(content().string(containsString("\"row\":3")))
                        .andExpect(content().string(containsString("\"row\":8")))
                        .andExpect(content().string(containsString("\"row\":9")))
                        .andExpect(content().string(containsString("\"row\":10")))
                        .andExpect(content().string(containsString("\"row\":11")))
                        .andExpect(content().string(containsString("\"row\":13")))
                        .andExpect(content().string(containsString("\"row\":16")));
        }

        @Test
        public void validateErrorHourFile() throws Exception {
                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                        "file",
                        "test-hrproblems.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // MediaType.APPLICATION_OCTET_STREAM_VALUE,
                        new ClassPathResource("test-hrproblems.xlsx").getInputStream());
                assertTrue(this.mockMvc != null);

                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/excelImport")

                                .file(mockMultipartFile).accept(MediaType.APPLICATION_JSON)
                                //.header("person", joan.getFirstName() + '#' + joan.getLastName()))
                                .param("person", objectMapper.writeValueAsString(this.joan)))
                        .andDo(print())
                        .andExpect(status().is(409))
                        .andExpect(content().string(containsString("\"row\":1")))
                        .andExpect(content().string(containsString("\"row\":2")))
                        .andExpect(content().string(containsString("\"row\":3")))
                        .andExpect(content().string(containsString("\"row\":4")))
                        .andExpect(content().string(containsString("\"row\":5")))
                        .andExpect(content().string(containsString("\"row\":6")))
                        .andExpect(content().string(containsString("\"row\":7")))
                        .andExpect(content().string(containsString("\"row\":8")))
                        .andExpect(content().string(containsString("\"row\":9")));
        }



        public static ProgramDTO getTestProgram(String identifier) {
                List<PersonDTO> principalInvestigators1 = Arrays.asList(new PersonDTO[] {
                                new PersonDTO("Seppe", "Machiels", "SDN:EDMO::3330", null, null,
                                                "seppe.machiels@mil.be") });
                return new ProgramDTO(identifier, principalInvestigators1,
                                "General activities of the Belgica, such as navigation, personnel operations,...",
                                null, "Belgica operations", null);
        }

        @Test
        public void validateOkFile() throws Exception {
                ProgramDTO pr = getTestProgram("11BU_operations");
                ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                                "file",
                                "test-noproblems.xlsx",
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // MediaType.APPLICATION_OCTET_STREAM_VALUE,
                                new ClassPathResource("test-noproblems.xlsx").getInputStream());
                assertTrue(this.mockMvc != null);

                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/excelImport")

                                .file(mockMultipartFile).accept(MediaType.APPLICATION_JSON)
                                //.header("person", joan.getFirstName() + '#' + joan.getLastName()))
                                //.header("person", objectMapper.writeValueAsString(this.joan)))
                                .param("person", objectMapper.writeValueAsString(this.joan)))
                                .andDo(print())
                                .andExpect(status().is(201));

                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/events"))
                                .andExpect(status().is(200))
                                .andDo(print())
                                .andExpect(content().string(containsString("<name>All persons</name>")))
                                .andExpect(content().string(containsString("<name>Belgica</name>")))
                                .andExpect(content().string(containsString("<name>Unknown sparker</name>")))
                                .andExpect(content().string(containsString("<name>Crew</name>")))
                                .andExpect(content().string(containsString("<name>Command</name>")))
                                .andExpect(content().string(containsString("<name>Scientists</name>")))

                                .andExpect(content().string(containsString("<name>Exercise</name>")))
                                .andExpect(content().string(containsString("<name>Meeting</name>")))
                                .andExpect(content().string(containsString("<name>Recreation</name>")))
                                .andExpect(content().string(containsString("<name>Testing</name>")))
//@TODO Dit komt niet in die xslx voor   //.andExpect(content().string(containsString("<name>Sheltering</name>")))

                                .andExpect(content().string(containsString("<identifier>11BU_operations</identifier>")))
                                .andExpect(content().string(containsString("<name>Belgica operations</name>")))

                                //@TODO Verify with Thomas    //.andExpect(content().string(containsString("<timeStamp>2023-10-04T14:30:00+02</timeStamp>")))
                                .andReturn();
        }

        @Test
        public void validateOkFileExtraColumn() throws Exception {
                ProgramDTO pr = getTestProgram("11BU_operations");
                ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                        "file",
                        "test-noproblemsExtraColAndSwitchedCol.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // MediaType.APPLICATION_OCTET_STREAM_VALUE,
                        new ClassPathResource("test-noproblemsExtraColAndSwitchedCol.xlsx").getInputStream());
                assertTrue(this.mockMvc != null);

                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/excelImport")

                                .file(mockMultipartFile).accept(MediaType.APPLICATION_JSON)
                                //.header("person", joan.getFirstName() + '#' + joan.getLastName()))
                                .param("person", objectMapper.writeValueAsString(this.joan)))
                        .andDo(print())
                        .andExpect(status().is(201));
        }
        @Test
        public void validateOkFileMissingHourColumn() throws Exception {
                ProgramDTO pr = getTestProgram("11BU_operations");
                ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                        "file",
                        "test-missingHourCol.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // MediaType.APPLICATION_OCTET_STREAM_VALUE,
                        new ClassPathResource("test-missingHourCol.xlsx").getInputStream());
                assertTrue(this.mockMvc != null);

                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/excelImport")

                                .file(mockMultipartFile).accept(MediaType.APPLICATION_JSON)
                                //.header("person", joan.getFirstName() + '#' + joan.getLastName()))
                                .param("person", objectMapper.writeValueAsString(this.joan)))
                        .andDo(print())
                        .andExpect(status().is(409)); //Since we are missing a required header we expect a failure to create the Excel Event
        }

        @Test
        public void validateVarious() throws Exception {
                ProgramDTO pr = getTestProgram("11BU_operations");
                ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

                MockMultipartFile mockMultipartFile = new MockMultipartFile(
                        "file",
                        "test-various.xlsx",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // MediaType.APPLICATION_OCTET_STREAM_VALUE,
                        new ClassPathResource("test-various.xlsx").getInputStream());
                assertTrue(this.mockMvc != null);

                this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/excelImport")

                                .file(mockMultipartFile).accept(MediaType.APPLICATION_JSON)
                                //.header("person", joan.getFirstName() + '#' + joan.getLastName()))
                                .param("person", objectMapper.writeValueAsString(this.joan)))
                        .andDo(print())
                        .andExpect(status().is(409)); //Since we are checking for various problems
        }



        //@Test
        void processSpreadsheetEvents() {
        }

        //@Test
        void saveSpreadsheetEvents() {
        }

        public static EventDTO getTestEvent() {
                PersonDTO joan = new PersonDTO("Joan", "Backers", null, null, null, "joan.backers@naturalsciences.be");
                EventDTO event = new EventDTO();
                event.setIdentifier(null); // this is a purely new event.
                event.setEventDefinitionId("e3c8df0d-02e9-446d-a59b-224a14b89f9a");
                event.setTimeStamp(OffsetDateTime.parse("2019-04-25T11:08:00Z"));
                event.setToolCategory(new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L05/current/50/", null,
                                "sediment grabs"));
                event.setTool(new ToolDTO(
                                new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653/", null,
                                                "Van Veen grab"),
                                null));
                event.setProcess(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_1", null, "Sampling"));
                event.setAction(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#act_2", null, "End"));
                event.setSubject(new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/C77/current/G71/", null,
                                "In-situ seafloor measurement/sampling"));
                event.setActor(joan);
                event.setPlatform("SDN:C17::11BE");
                List<PropertyDTO> properties = new ArrayList<>();
                properties.add(new PropertyDTO(
                                new LinkedDataTermDTO("http://ontologies.orr.org/fish_count", null, "fish_count"), "89",
                                null));
                properties.add(new PropertyDTO(
                                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_21", null, "depth_m"),
                                "3", "m"));
                properties.add(new PropertyDTO(
                                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_4", null, "label"),
                                "W04", null));
                properties.add(new PropertyDTO(
                                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_16", null, "sampleId"),
                                "20190506_12", null));
                event.setProperties(properties);
                return event;
        }

}