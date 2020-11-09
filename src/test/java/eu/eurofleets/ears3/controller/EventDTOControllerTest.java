/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.EventDTOList;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
public class EventDTOControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Test of getRecentOrModifiedEvents method, of class EventDTOController.
     */
    @Test
    @Ignore
    public void testGetRecentOrModifiedEvents() throws Exception {
        System.out.println("getRecentOrModifiedEvents");
        EventDTO e = EventControllerTest.getTestEvent();
        e.program = "2020-MF";
        MvcResult postEvent = EventControllerTest.postEvent(this.mockMvc, e, objectMapper);
        OffsetDateTime after = Instant.now().atOffset(ZoneOffset.UTC);
        Thread.sleep(2000);
        CruiseControllerTest.postProgram(this.mockMvc, "2020-MF", objectMapper);
        MvcResult postEvent2 = EventControllerTest.postEvent(this.mockMvc, e, objectMapper);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/dto/events.json").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();

        String identifier = EventControllerTest.getIdentifier(postEvent);
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/dto/event.json?identifier=" + identifier).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(200))
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/dto/events.json?after=" + after.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        contentAsString = mvcResult.getResponse().getContentAsString();
     //   assertTrue(StringUtils.countOccurrencesOf(contentAsString, "event") == 1);

        EventControllerTest.deleteEvent(this.mockMvc, postEvent);
        EventControllerTest.deleteEvent(this.mockMvc, postEvent2);
    }

}
