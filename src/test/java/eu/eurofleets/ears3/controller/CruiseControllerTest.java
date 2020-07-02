/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
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

    public static CruiseDTO getTestCruise1() {
        CruiseDTO c = new CruiseDTO();
        c.arrivalHarbour = "SDN:C38::BSH4510";
        c.departureHarbour = "SDN:C38::BSH4510";
        c.identifier = "BE11/2007_18";
        c.name = "BE11/2007_18";
        c.startDate = OffsetDateTime.of(2007, 11, 8, 0, 0, 0, 0, ZoneOffset.UTC);
        c.endDate = OffsetDateTime.of(2007, 11, 15, 0, 0, 0, 0, ZoneOffset.UTC);
        c.objectives = "The objectives of the cruise are twofold: 1) to validate the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. 2) to do fisheries monitoring";
        c.collateCentre = "SDN:EDMO::1778";
        c.platform = "SDN:C17::11BE";
        List<String> p02 = Arrays.asList(new String[]{"SDN:P02::VDFC", "SDN:P02::LGCR", "SDN:P02::ZNTX"});
        List<PersonDTO> chiefScientists = Arrays.asList(new PersonDTO[]{new PersonDTO("Katrijn", "Baetens", "SDN:EDMO::3330", "02/2209091", "02/2208081", "kbaetens@naturalsciences.be"), new PersonDTO("Valérie", "Dulière", "SDN:EDMO::3330", "02/2209090", "02/2208080", "vduliere@naturalsciences.be")});
        List<PersonDTO> principalInvestigators1 = Arrays.asList(new PersonDTO[]{new PersonDTO("Katrijn", "Baetens", "SDN:EDMO::3330", "02/2209091", "02/2208081", "kbaetens@naturalsciences.be"), new PersonDTO("Valérie", "Dulière", "SDN:EDMO::3330", "02/2209090", "02/2208080", "vduliere@naturalsciences.be")});
        List<PersonDTO> principalInvestigators2 = Arrays.asList(new PersonDTO[]{new PersonDTO("Kris", "Hostens", "SDN:EDMO::ILVO", "057/2209091", "057/2208081", "khostens@ilvo.be")});

        List<ProgramDTO> programs = Arrays.asList(new ProgramDTO[]{new ProgramDTO("KB-12", principalInvestigators1, "validating the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. ", null), new ProgramDTO("KH1", principalInvestigators2, "Fisheries monitoring", null)});

        List<String> seaAreas = Arrays.asList(new String[]{"SDN:C19::1_2"});
        c.P02 = p02;
        c.chiefScientists = chiefScientists;
        c.isCancelled = false;
        c.seaAreas = seaAreas;
        //c.programs = programs;
        return c;
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
        List<PersonDTO> principalInvestigators1 = Arrays.asList(new PersonDTO[]{new PersonDTO("Katrijn", "Baetens", "SDN:EDMO::3330", "02/2209091", "02/2208081", "kbaetens@naturalsciences.be"), new PersonDTO("Valérie", "Dulière", "SDN:EDMO::3330", "02/2209090", "02/2208080", "vduliere@naturalsciences.be")});
        List<PersonDTO> principalInvestigators2 = Arrays.asList(new PersonDTO[]{new PersonDTO("Kris", "Hostens", "SDN:EDMO::ILVO", "057/2209091", "057/2208081", "khostens@ilvo.be")});

        List<ProgramDTO> programs = Arrays.asList(new ProgramDTO[]{new ProgramDTO("KB-12", principalInvestigators1, "validating the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. ", null), new ProgramDTO("KH1", principalInvestigators2, "Fisheries monitoring", null)});

        List<String> seaAreas = Arrays.asList(new String[]{"SDN:C19::1_2"});
        c.P02 = p02;
        c.chiefScientists = chiefScientists;
        c.isCancelled = false;
        c.seaAreas = seaAreas;
        //c.programs = programs;
        return c;
    }

    @Test
    public void testPostCruise() throws Exception {
        CruiseDTO cruise = getTestCruise1();
        String json = objectMapper.writeValueAsString(cruise);
        MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/cruise").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("<collateCentre><term>")))
                .andExpect(content().string(containsString("<identifier>https://edmo.seadatanet.org/report/1778</identifier><name>Japan Meteorological Agency</name><urn>SDN:EDMO::1778</urn></term><phoneNumber></phoneNumber><faxNumber></faxNumber><emailAddress></emailAddress><website>http://www.jma.go.jp/jma/indexe.html</website><deliveryPoint>1-3-4 Otemachi, Chiyoda-ku</deliveryPoint><city>Tokyo</city><postalCode>100-8122</postalCode><country><term>")))
                .andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C32/current/JP/</identifier><name>Japan</name><urn>SDN:C32::JP</urn></term>")))
                .andExpect(content().string(containsString("</country>")))
                .andExpect(content().string(containsString("</collateCentre><departureHarbour><term>")))
                .andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C38/current/BSH4510/</identifier><name>Zeebrugge</name><urn>SDN:C38::BSH4510</urn></term>"))).andExpect(content().string(containsString("</departureHarbour><arrivalHarbour><term>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C38/current/BSH4510/</identifier><name>Zeebrugge</name><urn>SDN:C38::BSH4510</urn></term>"))).andExpect(content().string(containsString("</arrivalHarbour>")))
                //  .andExpect(content().string(containsString("<chiefScientists><firstName>Katrijn</firstName><lastName>Baetens</lastName><organisation><term>"))).andExpect(content().string(containsString("<identifier>https://edmo.seadatanet.org/report/3330</identifier><name>Royal Belgian Institute of Natural Sciences, Operational Directorate Natural Environment, Belgian Marine Data Centre</name><urn>SDN:EDMO::3330</urn></term><phoneNumber>+32 (0)2 773 2111</phoneNumber><faxNumber>+32 (0)2 770 6972</faxNumber><emailAddress>bmdc@naturalsciences.be</emailAddress><website>http://www.bmdc.be</website><deliveryPoint>Gulledelle 100</deliveryPoint><city>Brussels</city><postalCode>1200</postalCode><country><term>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C32/current/BE/</identifier><name>Belgium</name><urn>SDN:C32::BE</urn></term>"))).andExpect(content().string(containsString("</country>"))).andExpect(content().string(containsString("</organisation>"))).andExpect(content().string(containsString("</chiefScientists> ")))
                .andExpect(content().string(containsString("<seaAreas><term>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C19/current/1_2/</identifier><name>North Sea</name><urn>SDN:C19::1_2</urn></term>"))).andExpect(content().string(containsString("</seaAreas>")))
                .andExpect(content().string(containsString("<platform><term>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C17/current/11BE/</identifier><name>Belgica</name><urn>SDN:C17::11BE</urn></term>"))).andExpect(content().string(containsString("</platform><objectives>The objectives of the cruise are twofold: 1) to validate the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. 2) to do fisheries monitoring</objectives><isCancelled>false</isCancelled><name>BE11/2007_18</name></cruise>"))).andReturn();

        //String content = mvcResult.getResponse().getContentAsString();
        //content = flattenString(content);
        mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.delete("/cruise?identifier=" + cruise.identifier))
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
