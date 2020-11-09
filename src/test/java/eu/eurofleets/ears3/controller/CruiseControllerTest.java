/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

/**
 *
 * @author thomas
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class}, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = {"eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service"})
public class CruiseControllerTest {

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

    public static CruiseDTO getTestCruise1(String identifier) {
        CruiseDTO c = new CruiseDTO();
        c.arrivalHarbour = "SDN:C38::BSH4510";
        c.departureHarbour = "SDN:C38::BSH4510";
        c.identifier = identifier;
        c.name = identifier;
        c.startDate = OffsetDateTime.of(2007, 11, 8, 0, 0, 0, 0, ZoneOffset.UTC);
        c.endDate = OffsetDateTime.of(2007, 11, 15, 0, 0, 0, 0, ZoneOffset.UTC);
        c.objectives = "The objectives of the cruise are twofold: 1) to validate the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. 2) to do fisheries monitoring";
        c.collateCentre = "SDN:EDMO::1778";
        c.platform = "SDN:C17::11BE";
        List<String> p02 = Arrays.asList(new String[]{"SDN:P02::VDFC", "SDN:P02::LGCR", "SDN:P02::ZNTX"});
        List<PersonDTO> chiefScientists = Arrays.asList(new PersonDTO[]{new PersonDTO("Katrijn", "Baetens", "SDN:EDMO::3330", "02/2209091", "02/2208081", "kbaetens@naturalsciences.be"), new PersonDTO("Valérie", "Dulière", "SDN:EDMO::3330", "02/2209090", "02/2208080", "vduliere@naturalsciences.be")});

        List<String> seaAreas = Arrays.asList(new String[]{"SDN:C19::1_2"});
        c.P02 = p02;
        c.chiefScientists = chiefScientists;
        c.isCancelled = false;
        c.seaAreas = seaAreas;
        //c.programs = programs;
        return c;
    }

    public static ProgramDTO getTestProgram1(String identifier) {
        List<PersonDTO> principalInvestigators1 = Arrays.asList(new PersonDTO[]{new PersonDTO("Katrijn", "Baetens", "SDN:EDMO::3330", "02/2209091", "02/2208081", "kbaetens@naturalsciences.be"), new PersonDTO("Valérie", "Dulière", "SDN:EDMO::3330", "02/2209090", "02/2208080", "vduliere@naturalsciences.be")});
        return new ProgramDTO(identifier, principalInvestigators1, "validating the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. ", null, "RUBBER-DUCK", "No sampling");
    }

    public static ProgramDTO getTestProgram2(String identifier) {
        List<PersonDTO> principalInvestigators2 = Arrays.asList(new PersonDTO[]{new PersonDTO("Kris", "Hostens", "SDN:EDMO::ILVO", "057/2209091", "057/2208081", "khostens@ilvo.be")});
        return new ProgramDTO("KH1", principalInvestigators2, "Fisheries monitoring", null, "Fisheries monitoring", "Beam trawl 4 and 8m");
    }

    public static CruiseDTO getTestCruise2(String identifier) {
        CruiseDTO c = new CruiseDTO();
        c.arrivalHarbour = "SDN:C38::BSH4510";
        c.departureHarbour = "SDN:C38::BSH4510";
        c.identifier = identifier;
        c.name = identifier;
        c.startDate = OffsetDateTime.of(2008, 11, 8, 0, 0, 0, 0, ZoneOffset.UTC);
        c.endDate = OffsetDateTime.of(2008, 11, 15, 0, 0, 0, 0, ZoneOffset.UTC);
        c.objectives = "The objectives of the cruise are twofold: 1) to validate the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. 2) to do fisheries monitoring";
        c.collateCentre = "SDN:EDMO::1778";
        c.platform = "SDN:C17::11BE";
        List<String> p02 = Arrays.asList(new String[]{"SDN:P02::VDFC", "SDN:P02::LGCR", "SDN:P02::ZNTX"});
        List<PersonDTO> chiefScientists = Arrays.asList(new PersonDTO[]{new PersonDTO("Katrijn", "Baetens", "SDN:EDMO::3330", "02/2209091", "02/2208081", "kbaetens@naturalsciences.be"), new PersonDTO("Valérie", "Dulière", "SDN:EDMO::3330", "02/2209090", "02/2208080", "vduliere@naturalsciences.be")});
        List<String> seaAreas = Arrays.asList(new String[]{"SDN:C19::1_2"});
        c.P02 = p02;
        c.chiefScientists = chiefScientists;
        c.isCancelled = false;
        c.seaAreas = seaAreas;
        //c.programs = programs;
        return c;
    }
    private static UUID programUUID;
    private static String programId;

    public static MvcResult postProgram(MockMvc mockMvc, String programIdentifier, ObjectMapper objectMapper) throws Exception {
        ProgramDTO pr = CruiseControllerTest.getTestProgram1(programIdentifier);

        String json = objectMapper.writeValueAsString(pr);

        return mockMvc.perform(MockMvcRequestBuilders.post("/program").contentType(MediaType.APPLICATION_JSON).content(json))
                .andReturn();
    }

    public static String postCruise(MockMvc mockMvc, CruiseDTO cruise, ObjectMapper objectMapper) throws Exception {
        String json = objectMapper.writeValueAsString(cruise);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/cruise").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("</seaAreas><programs><identifier>KB_")))
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        Pattern p = Pattern.compile("<cruise><id>(.*?)<\\/id>");
        Matcher m = p.matcher(contentAsString);
        String cruiseId = null;
        if (m.find()) {
            cruiseId = m.group(1);
        }
        return cruiseId;
    }

    @Test
    public void testPostProgram() throws Exception {
        programUUID = UUID.randomUUID();
        ProgramDTO program = getTestProgram1("KB_" + programUUID);
        String json = objectMapper.writeValueAsString(program);

        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/program").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("<identifier>KB_" + programUUID + "</identifier>")))
                .andExpect(content().string(containsString("<description>" + program.description + "</description>")))
                .andReturn();

    }

    @Test
    public void testPostAndUpdateCruise() throws Exception {
        CruiseDTO cruise = getTestCruise1("BE11/2007_18-" + UUID.randomUUID());
        programUUID = UUID.randomUUID();
        ProgramDTO program = getTestProgram1("KB_" + programUUID);
        postProgram(this.mockMvc, program.identifier, objectMapper);
        cruise.programs = new ArrayList<>();
        cruise.programs.add(program.identifier);
        String cruiseId = postCruise(mockMvc, cruise, objectMapper);
        //modify it
        cruise.collateCentre = "SDN:EDMO::230";

        String json = objectMapper.writeValueAsString(cruise);
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/cruise").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("<identifier>https://edmo.seadatanet.org/report/230</identifier>"))).andReturn();

        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/cruise/" + cruiseId))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("<chiefScientists><firstName>Katrijn</firstName>")))
                .andExpect(content().string(containsString("</seaAreas><programs><identifier>KB_")))
                .andExpect(content().string(containsString("SDN:P02::VDFC")))
                .andExpect(content().string(containsString("SDN:C19::1_2")))
                .andExpect(content().string(containsString("<identifier>https://edmo.seadatanet.org/report/230</identifier>"))).andReturn();

        ///  mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.delete("/cruise?identifier=" + cruise.identifier))
        //          .andExpect(status().is(204)).andReturn();
    }

    @Test
    public void testPostAndDeleteCruise() throws Exception {
        CruiseDTO cruise = getTestCruise1("BE11/2007_18-" + UUID.randomUUID());
        cruise.programs = new ArrayList<>();

        programUUID = UUID.randomUUID();
        ProgramDTO program = getTestProgram1("KB_" + programUUID);
        postProgram(this.mockMvc, program.identifier, objectMapper);
        cruise.programs.add(program.identifier);
        String json = objectMapper.writeValueAsString(cruise);
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/cruise").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("<collateCentre><term>")))
                .andExpect(content().string(containsString("<identifier>https://edmo.seadatanet.org/report/1778</identifier><name>Japan Meteorological Agency</name><urn>SDN:EDMO::1778</urn></term><phoneNumber></phoneNumber><faxNumber></faxNumber><emailAddress></emailAddress><website>http://www.jma.go.jp/jma/indexe.html</website><deliveryPoint>1-3-4 Otemachi, Chiyoda-ku</deliveryPoint><city>Tokyo</city><postalCode>100-8122</postalCode><country><term>")))
                .andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C32/current/JP</identifier><name>Japan</name><urn>SDN:C32::JP</urn></term>")))
                .andExpect(content().string(containsString("</country>")))
                .andExpect(content().string(containsString("</collateCentre><departureHarbour><term>")))
                .andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C38/current/BSH4510</identifier><name>Zeebrugge</name><urn>SDN:C38::BSH4510</urn></term>"))).andExpect(content().string(containsString("</departureHarbour><arrivalHarbour><term>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C38/current/BSH4510</identifier><name>Zeebrugge</name><urn>SDN:C38::BSH4510</urn></term>"))).andExpect(content().string(containsString("</arrivalHarbour>")))
                //  .andExpect(content().string(containsString("<chiefScientists><firstName>Katrijn</firstName><lastName>Baetens</lastName><organisation><term>"))).andExpect(content().string(containsString("<identifier>https://edmo.seadatanet.org/report/3330</identifier><name>Royal Belgian Institute of Natural Sciences, Operational Directorate Natural Environment, Belgian Marine Data Centre</name><urn>SDN:EDMO::3330</urn></term><phoneNumber>+32 (0)2 773 2111</phoneNumber><faxNumber>+32 (0)2 770 6972</faxNumber><emailAddress>bmdc@naturalsciences.be</emailAddress><website>http://www.bmdc.be</website><deliveryPoint>Gulledelle 100</deliveryPoint><city>Brussels</city><postalCode>1200</postalCode><country><term>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C32/current/BE/</identifier><name>Belgium</name><urn>SDN:C32::BE</urn></term>"))).andExpect(content().string(containsString("</country>"))).andExpect(content().string(containsString("</organisation>"))).andExpect(content().string(containsString("</chiefScientists> ")))
                .andExpect(content().string(containsString("<seaAreas><term>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C19/current/1_2</identifier><name>North Sea</name><urn>SDN:C19::1_2</urn></term>"))).andExpect(content().string(containsString("</seaAreas>")))
                .andExpect(content().string(containsString("<platform><term>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C17/current/11BE</identifier><name>Belgica</name><urn>SDN:C17::11BE</urn></term>"))).andExpect(content().string(containsString("</platform><objectives>The objectives of the cruise are twofold: 1) to validate the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. 2) to do fisheries monitoring</objectives><isCancelled>false</isCancelled>"))).andReturn();

        //String content = mvcResult.getResponse().getContentAsString();
        //content = flattenString(content);
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.delete("/cruise?identifier=" + cruise.identifier))
                .andDo(print())
                .andExpect(status().is(204)).andReturn();

        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.delete("/program?identifier=" + program.identifier))
                .andDo(print())
                .andExpect(status().is(204)).andReturn();
    }

    @Test
    public void testGetCruiseCSR() throws Exception {
        String identifier = "BE11/2007_18-" + UUID.randomUUID();
        CruiseDTO cruise = getTestCruise1(identifier);
        cruise.programs = new ArrayList<>();

        programUUID = UUID.randomUUID();
        ProgramDTO program = getTestProgram1("KB_" + programUUID);
        postProgram(this.mockMvc, program.identifier, objectMapper);
        cruise.programs.add(program.identifier);

        postCruise(mockMvc, cruise, objectMapper);

        String licenseString = env.getProperty("ears.csr.license");
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/cruise/csr?identifier=" + identifier))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<gmi:MI_Metadata")))
                .andExpect(content().string(containsString("<gco:CharacterString>cruise-start</gco:CharacterString>")))
                .andExpect(content().string(containsString("<gco:CharacterString>cruise-end</gco:CharacterString>")))
                .andExpect(content().string(containsString("<gml:posList srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\" srsDimension=\"2\">")))
                .andExpect(content().string(containsString("COMMISSION REGULATION (EC) No 1205/2008")))
                .andExpect(content().string(containsString("<gmx:Anchor xlink:href=\"https://www.seadatanet.org/urnurl/SDN:L08::" + licenseString)))
                .andReturn();

        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.delete("/cruise?identifier=" + identifier))
                .andDo(print())
                .andExpect(status().is(204)).andReturn();

    }

    public static String flattenString(String input) {
        return input.replaceAll(T_TABS.pattern(), "").replaceAll(T_NEWLINES.pattern(), "").replaceAll(T_NEWLINES2.pattern(), "").replaceAll(T_EMPTYLINES.pattern(), "");

    }

    public static Pattern T_TABS = Pattern.compile("\t");
    public static Pattern T_SPACES = Pattern.compile(" +");
    public static Pattern T_NEWLINES = Pattern.compile("\r\n");
    public static Pattern T_NEWLINES2 = Pattern.compile("\n");
    public static Pattern T_EMPTYLINES = Pattern.compile("([\\n\\r]+\\s*)*$");
}
