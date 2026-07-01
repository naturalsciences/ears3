/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest(classes = { Application.class })
@WebAppConfiguration
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD) //reset the database to base state before each test method
public class SyncSchedulerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @Disabled 
    public void testSyncDatabase() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/sync/database").accept(MediaType.APPLICATION_XML))
                //.andDo(print())
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @Disabled 
    public void testSyncSeaAreas() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/sync/seas").accept(MediaType.APPLICATION_XML))
                //.andDo(print())
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @Disabled 
    public void testSyncShips() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/sync/ships").accept(MediaType.APPLICATION_XML))
                //.andDo(print())
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @Disabled 
    public void testSyncHarbours() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/sync/harbours").accept(MediaType.APPLICATION_XML))
                //.andDo(print())
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @Disabled 
    public void testSyncAll() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/sync/all").accept(MediaType.APPLICATION_XML))
                //.andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
