/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.domain.EventList;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.Property;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.EventDTOList;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import eu.eurofleets.ears3.dto.PropertyDTO;
import eu.eurofleets.ears3.dto.ToolDTO;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.cruise.model.IOrganisation;
import be.naturalsciences.bmdc.cruise.model.IPerson;
import be.naturalsciences.bmdc.cruise.model.IPlatform;
import be.naturalsciences.bmdc.cruise.model.IProgram;
import be.naturalsciences.bmdc.cruise.model.IProperty;
import be.naturalsciences.bmdc.cruise.model.ITool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
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
public class EventControllerTest {

        @Autowired
        private WebApplicationContext wac;

        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Before
        public void setup() throws Exception {
                this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
                objectMapper.registerModule(new PersonModule());
                objectMapper.registerModule(new LinkedDataModule());
                objectMapper.registerModule(new ToolModule());
                objectMapper.registerModule(new PlatformModule());
                objectMapper.registerModule(new OrganisationModule());
                objectMapper.registerModule(new PropertyModule());
                objectMapper.registerModule(new ProgramModule());

        }

        /*
         * All this just to deserialise from json for the objectmapper which i just used
         * in the tests
         */
        class PersonDeserializer extends StdDeserializer<IPerson> {
                public PersonDeserializer() {
                        super(IPerson.class);
                }

                public IPerson deserialize(JsonParser jsonParser, DeserializationContext context)
                                throws IOException, JacksonException {
                        return jsonParser.readValueAs(Person.class);
                }
        }

        class PersonModule extends SimpleModule {
                {
                        addDeserializer(IPerson.class, new PersonDeserializer());
                }
        }

        class LinkedDataDeserializer extends StdDeserializer<ILinkedDataTerm> {
                public LinkedDataDeserializer() {
                        super(ILinkedDataTerm.class);
                }

                public ILinkedDataTerm deserialize(JsonParser jsonParser, DeserializationContext context)
                                throws IOException, JacksonException {
                        return jsonParser.readValueAs(LinkedDataTerm.class);
                }
        }

        class LinkedDataModule extends SimpleModule {
                {
                        addDeserializer(ILinkedDataTerm.class, new LinkedDataDeserializer());
                }
        }

        class ToolDeserializer extends StdDeserializer<ITool> {
                public ToolDeserializer() {
                        super(ITool.class);
                }

                public ITool deserialize(JsonParser jsonParser, DeserializationContext context)
                                throws IOException, JacksonException {
                        return jsonParser.readValueAs(Tool.class);
                }
        }

        class ToolModule extends SimpleModule {
                {
                        addDeserializer(ITool.class, new ToolDeserializer());
                }
        }

        class PlatformDeserializer extends StdDeserializer<IPlatform> {
                public PlatformDeserializer() {
                        super(IPlatform.class);
                }

                public IPlatform deserialize(JsonParser jsonParser, DeserializationContext context)
                                throws IOException, JacksonException {
                        return jsonParser.readValueAs(Platform.class);
                }
        }

        class PlatformModule extends SimpleModule {
                {
                        addDeserializer(IPlatform.class, new PlatformDeserializer());
                }
        }

        class OrganisationDeserializer extends StdDeserializer<IOrganisation> {
                public OrganisationDeserializer() {
                        super(IOrganisation.class);
                }

                public IOrganisation deserialize(JsonParser jsonParser, DeserializationContext context)
                                throws IOException, JacksonException {
                        return jsonParser.readValueAs(Organisation.class);
                }
        }

        class OrganisationModule extends SimpleModule {
                {
                        addDeserializer(IOrganisation.class, new OrganisationDeserializer());
                }
        }

        class PropertyDeserializer extends StdDeserializer<IProperty> {
                public PropertyDeserializer() {
                        super(IProperty.class);
                }

                public IProperty deserialize(JsonParser jsonParser, DeserializationContext context)
                                throws IOException, JacksonException {
                        return jsonParser.readValueAs(Property.class);
                }
        }

        class PropertyModule extends SimpleModule {
                {
                        addDeserializer(IProperty.class, new PropertyDeserializer());
                }
        }

        class ProgramDeserializer extends StdDeserializer<IProgram> {
                public ProgramDeserializer() {
                        super(IProgram.class);
                }

                public IProgram deserialize(JsonParser jsonParser, DeserializationContext context)
                                throws IOException, JacksonException {
                        return jsonParser.readValueAs(Program.class);
                }
        }

        class ProgramModule extends SimpleModule {
                {
                        addDeserializer(IProgram.class, new ProgramDeserializer());
                }
        }

        // Mock events

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

        public static EventDTO getTestEvent2() {
                PersonDTO joan = new PersonDTO("Joan", "Backers", null, null, null, "jb@rbins.be");

                EventDTO event = new EventDTO();
                event.setIdentifier(null);// UUID.randomUUID().toString());
                event.setEventDefinitionId("e3c8ff9d-02e9-446d-a59b-224a14b89f9a");
                event.setTimeStamp(OffsetDateTime.parse("2008-11-07T12:08:00Z"));
                // event.timeStamp = OffsetDateTime.of(2008, 11, 8, 23, 20, 40, 0,
                // ZoneOffset.UTC);
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

        // Utility methods

        public static Set<String> getIdentifiersFromJson(MvcResult mvcResult) throws UnsupportedEncodingException {
                String contentAsString = mvcResult.getResponse().getContentAsString();
                Pattern p = Pattern.compile(
                                "\"identifier\"\\:\"(\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b)");
                Matcher m = p.matcher(contentAsString);
                Set<String> allMatches = new HashSet<String>();

                while (m.find()) {
                        allMatches.add(m.group(1));
                }
                return allMatches;
        }

        /***
         * Delete all event results given in MvcResult mvcResult, which is a GET of all
         * events
         * 
         * @param mockMvc
         * @param mvcResult
         * @throws Exception
         */
        public static void deleteEvent(MockMvc mockMvc, MvcResult mvcResult) throws Exception {
                List<String> tmp = new ArrayList<>(getIdentifiersFromJson(mvcResult));
                String eventId = tmp.get(0);
                mockMvc.perform(MockMvcRequestBuilders.delete("/api/event?identifier=" + eventId))
                                // .andDo(print())
                                .andExpect(status().is(204)).andReturn();
        }

        /***
         * Post a single event
         * 
         * @param mockMvc
         * @param e
         * @param objectMapper
         * @return
         * @throws Exception
         */
        public static MvcResult postEvent(MockMvc mockMvc, EventDTO e, ObjectMapper objectMapper) throws Exception {
                String json = objectMapper.writeValueAsString(e);
                return mockMvc.perform(MockMvcRequestBuilders.post("/api/event").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(json)).andExpect(status().isCreated())
                                .andReturn();
        }

        /**
         * Count all Event events present in the GET response of a given url
         * 
         */
        public static void assertEventCount(String url, int expected, MockMvc mockMvc, ObjectMapper objectMapper)
                        throws Exception {
                MvcResult mvcResult = mockMvc
                                .perform(MockMvcRequestBuilders.get(url)
                                                .accept(MediaType.APPLICATION_JSON))
                                .andReturn();
                // String contentAsString = mvcResult.getResponse().getContentAsString();
                // int count = StringUtils.countOccurrencesOf(contentAsString, "<event>");

                String json = mvcResult.getResponse().getContentAsString();
                EventList events = objectMapper.readValue(json, EventList.class);
                // Gson gson = new Gson();
                int count = events.getEvents().size();
                assertEquals(expected, count);
        }

        /**
         * Count all EventDTO events present in the GET response of a given url
         * 
         */
        public static void assertEventDTOCount(String url, int expected, MockMvc mockMvc, ObjectMapper objectMapper)
                        throws Exception {
                MvcResult mvcResult = mockMvc
                                .perform(MockMvcRequestBuilders.get(url)
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andReturn();

                String json = mvcResult.getResponse().getContentAsString();
                EventDTOList events = objectMapper.readValue(json, EventDTOList.class);
                int count = events.getEvents().size();
                assertEquals(expected, count);
        }

        /**
         * Count all EventDTO events present in the GET response of a given url
         * 
         */
        public static void assertSingleEventDTOTest(String url, MockMvc mockMvc, ObjectMapper objectMapper)
                        throws Exception {
                MvcResult mvcResult = mockMvc
                                .perform(MockMvcRequestBuilders.get(url)
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andReturn();

                String json = mvcResult.getResponse().getContentAsString();
                EventDTO event = objectMapper.readValue(json, EventDTO.class);
                assertNotNull(event);
        }

        /***
         * Delete all events in the system
         */
        public static void deleteAllEvents(MockMvc mockMvc) throws Exception {
                MvcResult mvcResult = mockMvc
                                .perform(MockMvcRequestBuilders.get("/api/events").accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andReturn();

                for (String identifier : getIdentifiersFromJson(mvcResult)) { // delete all previous events
                        mvcResult = mockMvc
                                        .perform(MockMvcRequestBuilders
                                                        .delete(String.format("/api/event?identifier=%s", identifier)))
                                        .andExpect(status().is(204)).andReturn();
                }
        }

        @Test
        public void testHome() throws Exception {
                MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/events"))
                                // .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().string(containsString("events")))
                                .andReturn();

                String content = mvcResult.getResponse().getContentAsString();
                assertTrue(content.contains("events"));
        }

        @Test
        public void testPostAndDeleteEvent() throws Exception {
                // delete all previous events and programs
                deleteAllEvents(this.mockMvc);
                ProgramControllerTest.deleteAllPrograms(this.mockMvc);
                EventDTO e = getTestEvent();
                e.setTimeStamp(OffsetDateTime.now());
                e.setProgram("2020-MF");
                ProgramDTO pr = ProgramControllerTest.getTestProgram1("2020-MF");
                ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

                String json = objectMapper.writeValueAsString(e);

                MvcResult mvcResult = this.mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/event").accept(MediaType.APPLICATION_XML)
                                                .contentType(MediaType.APPLICATION_JSON).content(json))
                                .andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(content().string(containsString(
                                                "<eventDefinitionId>e3c8df0d-02e9-446d-a59b-224a14b89f9a</eventDefinitionId>")))
                                .andExpect(content().string(containsString("<subject>")))
                                .andExpect(content().string(containsString(
                                                "<subject><identifier>http://vocab.nerc.ac.uk/collection/C77/current/G71</identifier><name>In-situ seafloor measurement/sampling</name></subject>")))
                                .andExpect(content().string(containsString(
                                                "<tool><identifier>http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653</identifier><name>Van Veen grab</name></tool>")))
                                .andExpect(content().string(containsString("</tool><toolCategory>")))
                                .andExpect(content().string(containsString(
                                                "<identifier>http://vocab.nerc.ac.uk/collection/L05/current/50</identifier><name>sediment grabs</name></toolCategory><process>")))
                                .andExpect(content().string(containsString(
                                                "<identifier>http://ontologies.ef-ears.eu/ears2/1#pro_1</identifier><name>Sampling</name></process><action>")))
                                .andExpect(content().string(containsString(
                                                "<identifier>http://ontologies.ef-ears.eu/ears2/1#act_2</identifier><name>End</name></action>")))
                                // .andExpect(content().string(containsString("<property><key><id>")))
                                .andExpect(content().string(containsString(
                                                "<key><identifier>http://ontologies.orr.org/fish_count</identifier><name>fish_count</name></key><value>89</value>")))
                                .andExpect(content().string(containsString(
                                                "<platform>http://vocab.nerc.ac.uk/collection/C17/current/11BE</platform>")))
                                .andExpect(content().string(not(containsString("<creationTime/>"))))
                                .andExpect(content().string(not(containsString("<timeStamp/>"))))
                                .andExpect(content().string(not(containsString("<instrumentTime/>"))))
                                .andReturn();

                String contentAsString = mvcResult.getResponse().getContentAsString();
                Pattern p = Pattern.compile("<event><identifier>(.*?)</identifier>");
                Matcher m = p.matcher(contentAsString);
                String eventIdentifier = null;
                if (m.find()) {
                        eventIdentifier = m.group(1);
                }
                // assertTrue(StringUtils.countOccurrencesOf(contentAsString, "<properties>") ==
                // 4);

                // Assert that the XML is correctly rendered
                mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/events"))
                                .andExpect(status().is(200))
                                .andExpect(content().string(
                                                containsString("<identifier>" + eventIdentifier + "</identifier>")))
                                .andExpect(content().string(containsString("</key><value>20190506_12</value>")))
                                .andExpect(content().string(containsString("</key><value>3</value>")))
                                .andExpect(content().string(containsString("</key><value>89</value>")))
                                .andExpect(content().string(containsString("</key><value>W04</value>")))
                                .andExpect(content().string(containsString(
                                                "</property><property><key><identifier>http://ontologies.ef-ears.eu/ears2/1#pry_16</identifier>")))
                                .andExpect(content().string(containsString(
                                                "</property><property><key><identifier>http://ontologies.ef-ears.eu/ears2/1#pry_21</identifier>")))
                                .andExpect(content().string(containsString(
                                                "</property><property><key><identifier>http://ontologies.ef-ears.eu/ears2/1#pry_4</identifier>")))
                                .andExpect(content().string(containsString(
                                                "<action><identifier>http://ontologies.ef-ears.eu/ears2/1#act_2</identifier>")))
                                .andExpect(content().string(containsString(
                                                "<description>validating the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. </description>")))
                                .andExpect(content().string(containsString(
                                                "<identifier>http://vocab.nerc.ac.uk/collection/C32/current/BE</identifier>")))
                                .andExpect(content().string(containsString("<lastName>Baetens</lastName>")))
                                .andExpect(content().string(containsString("<lastName>Dulière</lastName>")))
                                .andExpect(content().string(containsString("<name>Belgica</name>")))
                                .andExpect(content().string(containsString("<name>Belgium</name>")))
                                .andExpect(content().string(containsString("<name>depth_m</name>")))
                                .andExpect(content().string(containsString("<name>End</name>")))
                                .andExpect(content().string(containsString("<name>fish_count</name>")))
                                .andExpect(content().string(containsString(
                                                "<name>In-situ seafloor measurement/sampling</name>")))
                                .andExpect(content().string(containsString("<name>label</name>")))
                                .andExpect(content().string(containsString("<name>research vessel</name>")))
                                .andExpect(content().string(containsString(
                                                "<name>Royal Belgian Institute of Natural Sciences, Operational Directorate Natural Environment, Belgian Marine Data Centre</name>")))
                                .andExpect(content().string(containsString(
                                                "<name>Royal Belgian Institute of Natural Sciences, Operational Directorate Natural Environment</name>")))
                                .andExpect(content().string(containsString("<name>RUBBER-DUCK</name>")))
                                .andExpect(content().string(containsString("<name>sampleId</name>")))
                                .andExpect(content().string(containsString("<name>Sampling</name>")))
                                .andExpect(content().string(containsString("<name>sediment grabs</name>")))
                                .andExpect(content().string(containsString("<name>Van Veen grab</name>")))
                                .andExpect(content().string(containsString(
                                                "<organisation><term><identifier>https://edmo.seadatanet.org/report/3330</identifier>")))
                                .andExpect(content().string(containsString(
                                                "<organisation><term><identifier>https://edmo.seadatanet.org/report/3330</identifier>")))
                                .andExpect(content().string(containsString(
                                                "<platform><term><identifier>http://vocab.nerc.ac.uk/collection/C17/current/11BE</identifier>")))
                                .andExpect(content().string(containsString(
                                                "<platformClass><identifier>http://vocab.nerc.ac.uk/collection/L06/current/31</identifier>")))
                                .andExpect(content().string(containsString(
                                                "<principalInvestigators><firstName>Katrijn</firstName>")))
                                .andExpect(content().string(containsString(
                                                "<principalInvestigators><firstName>Valérie</firstName>")))
                                .andExpect(content().string(containsString(
                                                "<process><identifier>http://ontologies.ef-ears.eu/ears2/1#pro_1</identifier>")))
                                .andExpect(content().string(
                                                containsString("<program><identifier>2020-MF</identifier>")))
                                .andExpect(content().string(containsString(
                                                "<properties><property><key><identifier>http://ontologies.orr.org/fish_count</identifier>")))
                                .andExpect(content().string(containsString("<sampling>No sampling</sampling>")))
                                .andExpect(content().string(containsString(
                                                "<subject><identifier>http://vocab.nerc.ac.uk/collection/C77/current/G71</identifier>")))
                                .andExpect(content().string(containsString(
                                                "<tool><term><identifier>http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653</identifier>")))
                                .andExpect(content().string(containsString(
                                                "<toolCategory><identifier>http://vocab.nerc.ac.uk/collection/L05/current/50</identifier>")))
                                .andExpect(content().string(containsString("<uom>m</uom>")))
                                .andExpect(content().string(containsString("<urn>ears:act::2</urn>")))
                                .andExpect(content().string(containsString("<urn>ears:pro::1</urn>")))
                                .andExpect(content().string(containsString("<urn>ears:pry::16</urn>")))
                                .andExpect(content().string(containsString("<urn>ears:pry::21</urn>")))
                                .andExpect(content().string(containsString("<urn>ears:pry::4</urn>")))
                                .andExpect(content().string(containsString("<urn>SDN:C17::11BE</urn>")))
                                .andExpect(content().string(containsString("<urn>SDN:C32::BE</urn>")))
                                .andExpect(content().string(containsString("<urn>SDN:C32::BE</urn>")))
                                .andExpect(content().string(containsString("<urn>SDN:C32::BE</urn>")))
                                .andExpect(content().string(containsString("<urn>SDN:C77::G71</urn>")))
                                .andExpect(content().string(containsString("<urn>SDN:EDMO::3327</urn>")))
                                .andExpect(content().string(containsString("<urn>SDN:EDMO::3330</urn>")))
                                .andExpect(content().string(containsString("<urn>SDN:EDMO::3330</urn>")))
                                .andExpect(content().string(containsString("<urn>SDN:L05::50</urn>")))
                                .andExpect(content().string(containsString("<urn>SDN:L06::31</urn>")))
                                .andExpect(content().string(containsString("<urn>SDN:L22::TOOL0653</urn>")))
                                .andExpect(content().string(containsString(
                                                "<vesselOperator><term><identifier>https://edmo.seadatanet.org/report/3327</identifier>")))
                                .andReturn();

                // Assert that the JSON is correctly rendered
                mvcResult = this.mockMvc
                                .perform(MockMvcRequestBuilders.get("/api/event?identifier=" + eventIdentifier)
                                                .accept(MediaType.APPLICATION_JSON))
                                .andDo(print())
                                .andExpect(status().is(200))
                                .andExpect(content().string(containsString("\"identifier\":\"" + eventIdentifier)))
                                .andExpect(content().string(containsString("actor")))
                                .andReturn();

                // delete all previous events and programs
                deleteAllEvents(this.mockMvc);
                ProgramControllerTest.deleteAllPrograms(this.mockMvc);
        }

        @Test
        public void testGetEventsCSV() throws Exception {
                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/events.csv")
                                .accept(MediaType.valueOf("text/csv")))
                                // .andDo(print())
                                .andExpect(status().is(200))
                                .andReturn();
        }

        @Test
        public void testGetEventsByActorAndProgram() throws Exception {
                // delete all previous events and programs
                deleteAllEvents(this.mockMvc);
                ProgramControllerTest.deleteAllPrograms(this.mockMvc);

                // create a program and an event for it. The event has Joan as an actor
                EventDTO e = getTestEvent();
                String programIdentifier = "2020-MF";
                e.setProgram(programIdentifier);
                ProgramDTO pr = ProgramControllerTest.getTestProgram1(programIdentifier);

                // post the program
                ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

                // count all events and verify there is none.
                assertEventCount("/api/events", 0, this.mockMvc, this.objectMapper);

                // post the event (Joan)
                postEvent(mockMvc, e, objectMapper);

                // count all events and verify there is 1.
                assertEventCount("/api/events", 1, this.mockMvc, this.objectMapper);

                // modify the event's actor

                String firstName = "Adalbert";
                String lastName = "Hoogendrave";

                e.getActor().setFirstName(firstName);
                e.getActor().setLastName(lastName);
                e.getActor().setEmail("sol.invictus@hubris.org");

                // temporarily store the email address and platform
                String adalbertEmail = e.getActor().getEmail();
                String platform = e.getPlatform();

                // post the modified event (Adalbert)
                postEvent(mockMvc, e, objectMapper);

                // count all events and verify there are 2.
                assertEventCount("/api/events", 2, this.mockMvc, this.objectMapper);

                // post the modified event (Adalbert) again
                postEvent(mockMvc, e, objectMapper);

                // count all events and verify there are 3.
                assertEventCount("/api/events", 3, this.mockMvc, this.objectMapper);

                // post the modified event (Adalbert) again
                postEvent(mockMvc, e, objectMapper);

                // count all events and verify there are 4.
                assertEventCount("/api/events", 4, this.mockMvc, this.objectMapper);

                assertEventCount(String.format("/api/events?actorEmail=%s&platformIdentifier=%s&programIdentifier=%s",
                                adalbertEmail, platform, programIdentifier), 3, this.mockMvc, this.objectMapper);

                // delete all previous events and programs
                deleteAllEvents(this.mockMvc);
                ProgramControllerTest.deleteAllPrograms(this.mockMvc);

        }

        @Test
        public void testGetEventsByProgram() throws Exception {
                // delete all previous events and programs
                deleteAllEvents(this.mockMvc);
                ProgramControllerTest.deleteAllPrograms(this.mockMvc);

                // create a program and an event for it.
                EventDTO e = getTestEvent();
                String programIdentifier = "2020-MF";
                e.setProgram(programIdentifier);
                ProgramDTO pr = ProgramControllerTest.getTestProgram1(programIdentifier);

                ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);
                postEvent(mockMvc, e, objectMapper);

                // create another program and an event for it.
                EventDTO e2 = getTestEvent();
                programIdentifier = "2025-MF";
                e2.setProgram(programIdentifier);
                ProgramDTO pr2 = ProgramControllerTest.getTestProgram1(programIdentifier);

                ProgramControllerTest.postProgram(this.mockMvc, pr2, objectMapper);
                postEvent(mockMvc, e2, objectMapper);

                // find all events of the first program

                assertEventCount("/api/events?programIdentifier=" + e.getProgram(), 1, this.mockMvc, this.objectMapper);

                assertEventCount("/api/events?programIdentifier=" + e2.getProgram(), 1, this.mockMvc,
                                this.objectMapper);

                // delete all previous events and programs
                deleteAllEvents(this.mockMvc);
                ProgramControllerTest.deleteAllPrograms(this.mockMvc);
        }
}
