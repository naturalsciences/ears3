/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.Application;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Thomas Vandenberghe
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = {"eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service"})
@TestPropertySource(locations = "classpath:test.properties")
public class SyncSchedulerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    @Ignore
    public void testSyncDatabase() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/sync/database"))
                //.andDo(print())
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @Ignore
    public void testSyncSeaAreas() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/sync/seas"))
                //.andDo(print())
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @Ignore
    public void testSyncShips() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/sync/ships"))
                //.andDo(print())
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @Ignore
    public void testSyncHarbours() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/sync/harbours"))
                //.andDo(print())
                .andExpect(status().is(200))
                .andReturn();
    }

    @Test
    @Ignore
    public void testSyncAll() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/sync/all"))
                //.andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
