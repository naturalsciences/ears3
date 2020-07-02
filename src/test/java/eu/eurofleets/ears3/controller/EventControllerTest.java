/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Property;
import eu.eurofleets.ears3.domain.SamplingEvent;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import eu.eurofleets.ears3.dto.PropertyDTO;
import eu.eurofleets.ears3.dto.ToolDTO;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.core.StringContains.containsString;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
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
public class EventControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
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

    private EventDTO getTestEvent() {
        PersonDTO joan = new PersonDTO("Joan", "Backers", null, null, null, null);
        EventDTO event = new EventDTO();
        event.identifier = "e3c8df0d-02e9-446d-a59b-224a14b89f9a";
        event.timeStamp = OffsetDateTime.parse("2019-05-06T16:44:18Z");
        event.toolCategory = new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L05/current/50/", null, "sediment grabs");
        event.tool = new ToolDTO(new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653/", null, "Van Veen grab"), null);
        event.process = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_1", null, "Sampling");
        event.action = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#act_2", null, "End");
        event.subject = new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/C77/current/G71/", null, "In-situ seafloor measurement/sampling");
        event.actor = joan;

        List<PropertyDTO> properties = new ArrayList<>();
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.orr.org/fish_count", null, "fish_count"), "89", null));
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1/11BE#pry_21", null, "depth_m"), "3", "m"));
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_4", null, "label"), "W04", null));
        properties.add(new PropertyDTO(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_16", null, "sampleId"), "20190506_12", null));
        event.properties = properties;
        return event;
    }

    @Test
    public void testPostEvent() throws Exception {
        String json = objectMapper.writeValueAsString(getTestEvent());
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/event").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("<identifier>e3c8df0d-02e9-446d-a59b-224a14b89f9a</identifier>")))
                .andExpect(content().string(containsString("<subject>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C77/current/G71</identifier><name>In-situ seafloor measurement/sampling</name><urn>SDN:C77::G71</urn></subject><tool><term>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653</identifier><name>Van Veen grab</name><urn>SDN:L22::TOOL0653</urn></term>"))).andExpect(content().string(containsString("</tool><toolCategory>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/L05/current/50</identifier><name>sediment grabs</name><urn>SDN:L05::50</urn></toolCategory><process>"))).andExpect(content().string(containsString("<identifier>http://ontologies.ef-ears.eu/ears2/1#pro_1</identifier><name>Sampling</name><urn>ears:pro::1</urn></process><action>"))).andExpect(content().string(containsString("<identifier>http://ontologies.ef-ears.eu/ears2/1#act_2</identifier><name>End</name><urn>ears:act::2</urn></action>")))
                .andExpect(content().string(containsString("<properties><key>"))).andExpect(content().string(containsString("<identifier>http://ontologies.orr.org/fish_count</identifier><name>fish_count</name></key><value>89</value>")))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        assertTrue(StringUtils.countOccurrencesOf(content, "<properties>") == 4);
    }
}
