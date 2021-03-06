/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.ears2;

import eu.eurofleets.ears3.controller.rest.*;
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
import org.springframework.test.context.TestPropertySource;
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
@TestPropertySource("/test.properties")
@Ignore
public class Ears2EventControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private Environment env;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * *
     * Test if the test framework is capable of reading the test.properties
     * file.
     *
     * @throws Exception
     */
    @Test
    public void getEventByDates() throws Exception {
        EventDTO e = EventControllerTest.getTestEvent();
        e.program = "2020-MF";

        ProgramDTO pr = ProgramControllerTest.getTestProgram1(e.program);
        ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);
        MvcResult postEvent = postEvent(this.mockMvc, e, this.objectMapper);
        String content = postEvent.getResponse().getContentAsString();
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/ears2/events?startDate=2019-04-24T11:00:00Z&endDate=2019-04-26T12:00:00Z")).andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("event eventId")))
                .andExpect(content().string(containsString("<ewsl:program>2020-MF</ewsl:program>"))).andReturn();
        content = mvcResult.getResponse().getContentAsString();
        int nbEvent = StringUtils.countOccurrencesOf(content, "event eventId");
        assertTrue(nbEvent == 1);
        deleteEvent(mockMvc, postEvent);
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

}
