/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.controller.rest.ProgramController;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 *
 * @author thomas
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = {"eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service"})
@TestPropertySource(locations="classpath:test.properties")
public class ProgramControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        /*   MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/sync/all")) //first we need all the harbours, countries etc in the system
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();*/
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Environment env;

    public static ProgramDTO getTestProgram1(String identifier) {
        List<PersonDTO> principalInvestigators1 = Arrays.asList(new PersonDTO[]{new PersonDTO("Katrijn", "Baetens", "SDN:EDMO::3330", "02/2209091", "02/2208081", "kbaetens@naturalsciences.be"), new PersonDTO("Valérie", "Dulière", "SDN:EDMO::3330", "02/2209090", "02/2208080", "vduliere@naturalsciences.be")});
        return new ProgramDTO(identifier, principalInvestigators1, "validating the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. ", null, "RUBBER-DUCK", "No sampling");
    }

    public static ProgramDTO getTestProgram2(String identifier) {
        List<PersonDTO> principalInvestigators2 = Arrays.asList(new PersonDTO[]{new PersonDTO("Kris", "Hostens", "SDN:EDMO::ILVO", "057/2209091", "057/2208081", "khostens@ilvo.be")});
        return new ProgramDTO("KH1", principalInvestigators2, "Fisheries monitoring", null, "Fisheries monitoring", "Beam trawl 4 and 8m");
    }

    public static MvcResult postProgram(MockMvc mockMvc, ProgramDTO pr, ObjectMapper objectMapper) throws Exception {
        String json = objectMapper.writeValueAsString(pr);
        return mockMvc.perform(MockMvcRequestBuilders.post("/api/program").contentType(MediaType.APPLICATION_JSON).content(json))
                .andReturn();
    }

    public static void deleteProgram(String identifier, MockMvc mockMvc) throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete("/api/program?identifier=" + identifier))
                .andDo(print())
                .andExpect(status().is(204)).andReturn();
    }

    /**
     * Test of getPrograms method, of class ProgramController.
     */
    @Test
    public void testGetPrograms() throws Exception {
        System.out.println("getPrograms");
        ProgramDTO pr = getTestProgram1("MF-2020");
        postProgram(this.mockMvc, pr, objectMapper);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/programs"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("<identifier>MF-2020</identifier>"))).andReturn();

    }
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * Test of getProgramsByDate method, of class ProgramController.
     */
    @Test
    @Ignore
    public void testGetProgramsByDate() throws Exception {
        requestMappingHandlerMapping.getHandlerMethods().keySet();

        System.out.println("getProgramsByDate");
        CruiseDTO testCruise = CruiseControllerTest.getTestCruise1("SEASHELL-20");
        OffsetDateTime start = Instant.now().minus(20, ChronoUnit.DAYS).atOffset(ZoneOffset.UTC);
        OffsetDateTime end = Instant.now().atOffset(ZoneOffset.UTC);
        testCruise.startDate = start;
        testCruise.endDate = end;
        testCruise.programs = new ArrayList<>();
        testCruise.programs.add("HRZ-1898");
        ProgramDTO pr = getTestProgram1("HRZ-1898");
        postProgram(this.mockMvc, pr, objectMapper);
        CruiseControllerTest.postCruise(this.mockMvc, testCruise, this.objectMapper);

        testCruise = CruiseControllerTest.getTestCruise1("SEASHELL-20");
        start = OffsetDateTime.parse("2019-04-25T11:08:00Z");
        end = OffsetDateTime.parse("2019-04-29T11:08:00Z");
        testCruise.startDate = start;
        testCruise.endDate = end;
        testCruise.programs = new ArrayList<>();
        testCruise.programs.add("ILVO-FISHY");
        pr = getTestProgram1("ILVO-FISHY");
        postProgram(this.mockMvc, pr, objectMapper);
        CruiseControllerTest.postCruise(this.mockMvc, testCruise, this.objectMapper);
//mvcResult.getResponse().getContentAsString()
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/programs?cruiseIdentifier=SEASHELL-20"))
                .andDo(print())
                .andExpect(status().is(200)).andReturn();
        mvcResult.getResponse().getContentAsString();
//                .andExpect(content().string(containsString("<identifier>HRZ-1898</identifier>")))
//                .andExpect(content().string(containsString("<identifier>ILVO-FISHY</identifier>"))).andReturn();
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/programs?startDate=" + start.format(DateTimeFormatter.ISO_DATE_TIME)).contentType(MediaType.APPLICATION_JSON)) // + "&endDate=" + end.format(DateTimeFormatter.ISO_DATE_TIME)
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("<identifier>HRZ-1898</identifier>")))
                .andExpect(content().string(not(containsString("<identifier>ILVO-FISHY</identifier>")))).andReturn();
        CruiseControllerTest.deleteCruise(testCruise.identifier, mockMvc);
        deleteProgram("ILVO-FISHY", this.mockMvc);
        deleteProgram("HRZ-1898", this.mockMvc);

    }

    /**
     * Test of getProgramById method, of class ProgramController.
     */
    @Test
    public void testGetProgramById() throws Exception {
        System.out.println("getProgramById");
        ProgramDTO pr = getTestProgram1("MF-2020");
        postProgram(this.mockMvc, pr, objectMapper);
        ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/program?identifier=MF-2020"))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("<identifier>MF-2020</identifier>"))).andReturn();
    }

    /**
     * Test of getProgramByidentifier method, of class ProgramController.
     */
    @Test
    @Ignore
    public void testGetProgramByidentifier() {
        System.out.println("getProgramByidentifier");
        String identifier = "";
        ProgramController instance = new ProgramController();
        Program expResult = null;
        Program result = instance.getProgramByidentifier(identifier);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    private static UUID programUUID;
    private static String programId;

    @Test
    public void testPostProgram() throws Exception {
        programUUID = UUID.randomUUID();
        ProgramDTO program = getTestProgram1("KB_" + programUUID);
        String json = objectMapper.writeValueAsString(program);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/program").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("<identifier>KB_" + programUUID + "</identifier>")))
                .andExpect(content().string(containsString("<description>" + program.description + "</description>")))
                .andReturn();

    }

    /**
     * Test of removeProgramByIdentifier method, of class ProgramController.
     */
    @Test
    @Ignore
    public void testRemoveProgramByIdentifier() {
        System.out.println("removeProgramByIdentifier");
        String identifier = "";
        ProgramController instance = new ProgramController();
        String expResult = "";
        String result = instance.removeProgramByIdentifier(identifier);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeProgramById method, of class ProgramController.
     */
    @Test
    @Ignore
    public void testRemoveProgramById() {
        System.out.println("removeProgramById");
        String id = "";
        ProgramController instance = new ProgramController();
        String expResult = "";
        String result = instance.removeProgramById(id);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

}
