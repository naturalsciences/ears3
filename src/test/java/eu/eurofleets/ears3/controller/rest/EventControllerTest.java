/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import eu.eurofleets.ears3.dto.PropertyDTO;
import eu.eurofleets.ears3.dto.ToolDTO;
import java.io.UnsupportedEncodingException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author thomas
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = {"eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service"})
@PropertySource("/test.properties")
@Ignore
public class EventControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private Environment env;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @Ignore
    public void testHome() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/events"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("events")))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(content.contains("events"));
    }

    @Autowired
    private ObjectMapper objectMapper;

    public static EventDTO getTestEvent() {
        PersonDTO joan = new PersonDTO("Joan", "Backers", null, null, null, "joan.backers@naturalsciences.be");
        EventDTO event = new EventDTO();
        event.identifier = null; //this is a purely new event.
        event.eventDefinitionId = "e3c8df0d-02e9-446d-a59b-224a14b89f9a";
        event.timeStamp = OffsetDateTime.parse("2019-04-25T11:08:00Z");
        event.toolCategory = new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L05/current/50/", null, "sediment grabs");
        event.tool = new ToolDTO(new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653/", null, "Van Veen grab"), null);
        event.process = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_1", null, "Sampling");
        event.action = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#act_2", null, "End");
        event.subject = new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/C77/current/G71/", null, "In-situ seafloor measurement/sampling");
        event.actor = joan;
        event.platform = "SDN:C17::11BE";
        List<PropertyDTO> properties = new ArrayList<>();
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.orr.org/fish_count", null, "fish_count"), "89", null));
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_21", null, "depth_m"), "3", "m"));
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_4", null, "label"), "W04", null));
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_16", null, "sampleId"), "20190506_12", null));
        event.properties = properties;
        return event;
    }

    public static EventDTO getTestEvent2() {
        PersonDTO joan = new PersonDTO("Joan", "Backers", null, null, null, "jb@rbins.be");
        EventDTO event = new EventDTO();
        event.identifier = null;//UUID.randomUUID().toString();
        event.eventDefinitionId = "e3c8ff9d-02e9-446d-a59b-224a14b89f9a";
        event.timeStamp = OffsetDateTime.parse("2008-11-07T12:08:00Z");
        //event.timeStamp = OffsetDateTime.of(2008, 11, 8, 23, 20, 40, 0, ZoneOffset.UTC);
        event.toolCategory = new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L05/current/50/", null, "sediment grabs");
        event.tool = new ToolDTO(new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653/", null, "Van Veen grab"), null);
        event.process = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_1", null, "Sampling");
        event.action = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#act_2", null, "End");
        event.subject = new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/C77/current/G71/", null, "In-situ seafloor measurement/sampling");
        event.actor = joan;
        event.platform = "SDN:C17::11BE";

        List<PropertyDTO> properties = new ArrayList<>();
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.orr.org/fish_count", null, "fish_count"), "89", null));
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_21", null, "depth_m"), "3", "m"));
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_4", null, "label"), "W04", null));
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_16", null, "sampleId"), "20190506_12", null));
        event.properties = properties;
        return event;
    }

    @Test
    public void testPostAndDeleteEvent() throws Exception {
        EventDTO e = getTestEvent();
        e.program = "2020-MF";
        ProgramDTO pr = ProgramControllerTest.getTestProgram1("MF-2020");
        ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

        String json = objectMapper.writeValueAsString(e);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/event").accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("<eventDefinitionId>e3c8df0d-02e9-446d-a59b-224a14b89f9a</eventDefinitionId>")))
                .andExpect(content().string(containsString("<subject>")))
                .andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C77/current/G71/</identifier><name>In-situ seafloor measurement/sampling</name>")))
                .andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653</identifier><name>Van Veen grab</name><urn>SDN:L22::TOOL0653</urn></term>")))
                .andExpect(content().string(containsString("</tool><toolCategory>")))
                .andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/L05/current/50</identifier><name>sediment grabs</name><urn>SDN:L05::50</urn></toolCategory><process>")))
                .andExpect(content().string(containsString("<identifier>http://ontologies.ef-ears.eu/ears2/1#pro_1</identifier><name>Sampling</name><urn>ears:pro::1</urn></process><action>")))
                .andExpect(content().string(containsString("<identifier>http://ontologies.ef-ears.eu/ears2/1#act_2</identifier><name>End</name><urn>ears:act::2</urn></action>")))
                .andExpect(content().string(containsString("<property><key><id>")))
                .andExpect(content().string(containsString("<identifier>http://ontologies.orr.org/fish_count</identifier><name>fish_count</name></key><value>89</value><id>")))
                .andExpect(content().string(containsString("SDN:C17::11BE")))
                .andExpect(content().string(not(containsString("<creationTime/>"))))
                .andExpect(content().string(not(containsString("<timeStamp/>"))))
                .andExpect(content().string(not(containsString("<instrumentTime/>"))))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        Pattern p = Pattern.compile("</id><identifier>(.*?)<\\/identifier>");
        Matcher m = p.matcher(contentAsString);
        String eventIdentifier = null;
        if (m.find()) {
            eventIdentifier = m.group(1);
        }
        assertTrue(StringUtils.countOccurrencesOf(contentAsString, "<property>") == 4);

        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/events")).andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("<identifier>" + eventIdentifier + "</identifier>"))).andReturn();


        /*  mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.delete("/event?identifier=" + eventIdentifier))
                .andDo(print())
                .andExpect(status().is(204)).andReturn();

        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/event?identifier=" + eventIdentifier))
                .andDo(print())
                .andExpect(status().is(404))
                .andReturn();*/
    }

    /**
     * *
     * Test if the test framework is capable of reading the test.properties
     * file.
     *
     * @throws Exception
     */
    @Test
    @Ignore
    public void posteventTestTestProperties() throws Exception {
        EventDTO e = getTestEvent();
        e.program = "2020-MF";
        String licenseString = env.getProperty("ears.csr.license");
        if (licenseString == null) {
            fail();
        }
        ProgramDTO pr = ProgramControllerTest.getTestProgram1("MF-2020");
        ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);
        e.platform = null;
        postEvent(this.mockMvc, e, this.objectMapper);
    }

    public static String getIdentifier(MvcResult mvcResult) throws UnsupportedEncodingException {
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Pattern p = Pattern.compile("</id><identifier>(.*?)<\\/identifier>");
        Matcher m = p.matcher(contentAsString);
        String eventIdentifier = null;
        if (m.find()) {
            eventIdentifier = m.group(1);
        }
        return eventIdentifier;
    }

    public static void deleteEvent(MockMvc mockMvc, MvcResult mvcResult) throws Exception {
        String eventId = getIdentifier(mvcResult);

        MvcResult mvcResult2 = mockMvc.perform(MockMvcRequestBuilders.delete("/event?identifier=" + eventId))
                .andDo(print())
                .andExpect(status().is(204)).andReturn();
    }

    public static MvcResult postEvent(MockMvc mockMvc, EventDTO e, ObjectMapper objectMapper) throws Exception {
        String json = objectMapper.writeValueAsString(e);
        return mockMvc.perform(MockMvcRequestBuilders.post("/event").contentType(MediaType.APPLICATION_JSON).content(json))
                .andReturn();

    }

    @Test
    @Ignore
    public void testGetEventsCSV() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/events.csv").accept(MediaType.valueOf("text/csv")))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @Ignore
    public void testGetEventsByActorAndProgram() throws Exception {
        EventDTO e = getTestEvent();
        String programIdentifier = "2020-MF";
        e.program = programIdentifier;
        ProgramDTO pr = ProgramControllerTest.getTestProgram1(programIdentifier);

        String json = objectMapper.writeValueAsString(pr);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/program").contentType(MediaType.APPLICATION_JSON).content(json))
                .andReturn();

        json = objectMapper.writeValueAsString(e);

        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/events").contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        int eventsBefore = StringUtils.countOccurrencesOf(contentAsString, "<event>");

        MvcResult mvcResultCrE1 = this.mockMvc.perform(MockMvcRequestBuilders.post("/event").contentType(MediaType.APPLICATION_JSON).content(json))
                .andReturn();

        e.actor.firstName = "Adalbert";
        e.actor.lastName = "Hoogendrave";
        e.actor.email = "sol.invictus@hubris.org";

        String email = e.actor.email;
        String platform = e.platform;
        json = objectMapper.writeValueAsString(e);
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/events").contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        MvcResult mvcResultCrE2 = this.mockMvc.perform(MockMvcRequestBuilders.post("/event").contentType(MediaType.APPLICATION_JSON).content(json))
                .andReturn();

        MvcResult mvcResultCrE3 = this.mockMvc.perform(MockMvcRequestBuilders.post("/event").contentType(MediaType.APPLICATION_JSON).content(json))
                .andReturn();

        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/events").contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
        int eventsAfter = StringUtils.countOccurrencesOf(contentAsString, "<event>");
        assertTrue(eventsAfter - eventsBefore == 3); //one joan, two adalbert
        int nbEmailBefore = StringUtils.countOccurrencesOf(contentAsString, e.actor.email);
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/events?actorEmail=" + email + "&platformIdentifier=" + platform + "&programIdentifier=" + programIdentifier).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();

        int nbEmailAfter = StringUtils.countOccurrencesOf(contentAsString, e.actor.email);

        assertTrue(nbEmailAfter == 2 && nbEmailBefore == 2);

        deleteEvent(this.mockMvc, mvcResultCrE1);
        deleteEvent(this.mockMvc, mvcResultCrE2);
        deleteEvent(this.mockMvc, mvcResultCrE3);
    }
}
