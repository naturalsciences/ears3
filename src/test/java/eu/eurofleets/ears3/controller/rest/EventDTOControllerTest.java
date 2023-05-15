/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import static eu.eurofleets.ears3.controller.rest.EventControllerTest.getIdentifiersFromJson;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
        public void testGetRecentOrModifiedEvents() throws Exception {
                // delete all previous events
                EventControllerTest.deleteAllEvents(this.mockMvc);

                // post a program
                ProgramDTO pr = ProgramControllerTest.getTestProgram1("2020-MF");
                ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

                // post an event
                EventDTO e = EventControllerTest.getTestEvent();
                // set the timestamp to null so that the server assigns it
                e.setTimeStamp(null);
                e.setProgram("2020-MF");
                EventControllerTest.postEvent(this.mockMvc, e, objectMapper);

                // note when and wait 2 seconds
                OffsetDateTime breakTime = Instant.now().atOffset(ZoneOffset.UTC);
                Thread.sleep(2000);

                // post the event again
                EventControllerTest.postEvent(this.mockMvc, e, objectMapper);

                // post the event again
                EventControllerTest.postEvent(this.mockMvc, e, objectMapper);

                // post the event again
                EventControllerTest.postEvent(this.mockMvc, e, objectMapper);

                MvcResult mvcResult = this.mockMvc
                                .perform(MockMvcRequestBuilders.get("/api/dto/events.json")
                                                .contentType(MediaType.APPLICATION_JSON))
                                // .andDo(print())
                                .andExpect(status().is(200))
                                .andReturn();

                List<String> tmp = new ArrayList<>(getIdentifiersFromJson(mvcResult));
                String identifier = tmp.get(0);

                EventControllerTest.assertSingleEventDTOTest("/api/dto/event.json?identifier=" + identifier,
                                this.mockMvc,
                                this.objectMapper);

                // we have 3 events after the break
                EventControllerTest.countEventDTOTest(
                                "/api/dto/events.json?startDate=" +
                                                breakTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                                3, this.mockMvc,
                                this.objectMapper);

                // we have 1 event before the break
                EventControllerTest.countEventDTOTest(
                                "/api/dto/events.json?endDate="
                                                + breakTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                                1, this.mockMvc,
                                this.objectMapper);

                // delete all events
                EventControllerTest.deleteAllEvents(this.mockMvc);
        }

}
