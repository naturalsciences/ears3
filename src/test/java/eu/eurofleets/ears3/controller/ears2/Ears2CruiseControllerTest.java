/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.ears2;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.controller.rest.CruiseControllerTest;
import eu.eurofleets.ears3.dto.CruiseDTO;
import java.util.UUID;
import static org.hamcrest.core.StringContains.containsString;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = {"eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service"})
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD) //reset the database to base state before each test method
public class Ears2CruiseControllerTest {

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

    @Test
    public void testPostAndUpdateCruise() throws Exception {
        String identifier = "BE11/2007_18-" + UUID.randomUUID();
        CruiseDTO cruise = CruiseControllerTest.getTestCruise1(identifier);
        cruise.collateCentre = "SDN:EDMO::230";
        CruiseControllerTest.postCruise(this.mockMvc, cruise, objectMapper);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/ears2/cruises?platformCode=SDN:C17::11BE"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("<ewsl:chiefScientist>[{\"firstName\":\"Katrijn\",\"lastName\":\"Baetens\",\"organisationCode\":\"SDN:EDMO::3330\",\"organisationName\":\"Royal Belgian Institute of Natural Sciences, Operational Directorate Natural Environment, Belgian Marine Data Centre\",\"country\":\"Belgium\"},{\"firstName\":\"Valérie\",\"lastName\":\"Dulière\",\"organisationCode\":\"SDN:EDMO::3330\",\"organisationName\":\"Royal Belgian Institute of Natural Sciences, Operational Directorate Natural Environment, Belgian Marine Data Centre\",\"country\":\"Belgium\"}]</ewsl:chiefScientist>"))).andReturn();

        CruiseControllerTest.deleteCruise(identifier, this.mockMvc);
    }

}
