/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.domain.Navigation;
import eu.eurofleets.ears3.dto.CruiseDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import eu.eurofleets.ears3.utilities.DatagramUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

/**
 *
 * @author Thomas Vandenberghe
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, properties = { "spring.main.allow-bean-definition-overriding=true" })
@WebAppConfiguration
@ComponentScan(basePackages = { "eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service" })
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD) //reset the database to base state before each test method
public class CruiseControllerTest {

        @Autowired
        private WebApplicationContext wac;

        private MockMvc mockMvc;

        @Before
        public void setup() throws Exception {
                this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
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
                List<String> p02 = Arrays.asList(new String[] { "SDN:P02::VDFC", "SDN:P02::LGCR", "SDN:P02::ZNTX" });
                List<PersonDTO> chiefScientists = Arrays.asList(new PersonDTO[] {
                                new PersonDTO("Katrijn", "Baetens", "SDN:EDMO::3330", "02/2209091", "02/2208081",
                                                "kbaetens@naturalsciences.be"),
                                new PersonDTO("Valérie", "Dulière", "SDN:EDMO::3330", "02/2209090", "02/2208080",
                                                "vduliere@naturalsciences.be") });

                List<String> seaAreas = Arrays.asList(new String[] { "SDN:C19::1_2" });
                c.P02 = p02;
                c.chiefScientists = chiefScientists;
                c.isCancelled = false;
                c.seaAreas = seaAreas;
                // c.programs = programs;
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
                List<String> p02 = Arrays.asList(new String[] { "SDN:P02::VDFC", "SDN:P02::LGCR", "SDN:P02::ZNTX" });
                List<PersonDTO> chiefScientists = Arrays.asList(new PersonDTO[] {
                                new PersonDTO("Katrijn", "Baetens", "SDN:EDMO::3330", "02/2209091", "02/2208081",
                                                "kbaetens@naturalsciences.be"),
                                new PersonDTO("Valérie", "Dulière", "SDN:EDMO::3330", "02/2209090", "02/2208080",
                                                "vduliere@naturalsciences.be") });
                List<String> seaAreas = Arrays.asList(new String[] { "SDN:C19::1_2" });
                c.P02 = p02;
                c.chiefScientists = chiefScientists;
                c.isCancelled = false;
                c.seaAreas = seaAreas;
                // c.programs = programs;
                return c;
        }

        private static UUID programUUID;

        public static String postCruise(MockMvc mockMvc, CruiseDTO cruise, ObjectMapper objectMapper) throws Exception {
                String json = objectMapper.writeValueAsString(cruise);

                String identifier = cruise.identifier;
                MvcResult mvcResult = mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/cruise")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                // .andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(content()
                                                .string(containsString("<identifier>" + identifier + "</identifier>")))
                                .andReturn();

                String contentAsString = mvcResult.getResponse().getContentAsString();
                Pattern p = Pattern.compile("<cruise><identifier>(.*?)<\\/identifier>");
                Matcher m = p.matcher(contentAsString);
                String cruiseId = null;
                if (m.find()) {
                        cruiseId = m.group(1);
                }
                return cruiseId;
        }

        public static void deleteCruise(String identifier, MockMvc mockMvc) throws Exception {
                mockMvc.perform(MockMvcRequestBuilders.delete("/api/cruise?identifier=" + identifier))
                                // .andDo(print())
                                .andExpect(status().is(204)).andReturn();
        }

        @Test
        public void testPostAndGetCurrent() throws Exception {
                String identifier = "BE11/2007_18-" + UUID.randomUUID();
                CruiseDTO cruise = getTestCruise1(identifier);
                String programIdentifier = "KB_" + UUID.randomUUID().toString();
                ProgramDTO program = ProgramControllerTest.getTestProgram1(programIdentifier);
                ProgramControllerTest.postProgram(this.mockMvc, program, objectMapper);
                cruise.programs = new ArrayList<>();
                cruise.programs.add(program.identifier);
                cruise.startDate = Instant.now().atOffset(ZoneOffset.UTC).minusDays(5);
                cruise.endDate = Instant.now().atOffset(ZoneOffset.UTC).plusDays(5);
                postCruise(mockMvc, cruise, objectMapper);

                MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/cruise/current"))
                                // .andDo(print())
                                .andExpect(status().is(200))
                                .andExpect(content()
                                                .string(containsString("<identifier>" + identifier + "</identifier>")))
                                .andExpect(content().string(containsString("</seaAreas><programs><identifier>KB_")))
                                .andExpect(content().string(containsString("SDN:P02::VDFC")))
                                .andExpect(content().string(containsString("SDN:C19::1_2"))).andReturn();

                mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/program/current"))
                                // .andDo(print())
                                .andExpect(status().is(200))
                                .andExpect(content().string(
                                                containsString("<identifier>" + programIdentifier + "</identifier>")))
                                .andReturn();
                String contentAsString = mvcResult.getResponse().getContentAsString();
                int programCount = StringUtils.countOccurrencesOf(contentAsString, "<program>");
                // assertTrue(programCount == 1);
                deleteCruise(identifier, mockMvc);
                ProgramControllerTest.deleteProgram(programIdentifier, mockMvc);
        }

        @Test
        public void testPostAndUpdateCruise() throws Exception {
                CruiseDTO cruise = getTestCruise1("BE11/2007_18-" + UUID.randomUUID());
                programUUID = UUID.randomUUID();
                ProgramDTO program = ProgramControllerTest.getTestProgram1("KB_" + programUUID);
                ProgramControllerTest.postProgram(this.mockMvc, program, objectMapper);
                cruise.programs = new ArrayList<>();
                cruise.programs.add(program.identifier);
                String cruiseId = postCruise(mockMvc, cruise, objectMapper);
                // modify it
                cruise.collateCentre = "SDN:EDMO::230";

                String json = objectMapper.writeValueAsString(cruise);
                this.mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/cruise")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                .andExpect(status().isCreated()).andReturn();
                // .andExpect(content().string(containsString("<identifier>https://edmo.seadatanet.org/report/230</identifier>"))).andReturn();

                this.mockMvc.perform(MockMvcRequestBuilders.get("/api/cruise?identifier=" + cruiseId))
                                // .andDo(print())
                                .andExpect(status().is(200))
                                .andExpect(content().string(
                                                containsString("<chiefScientists><firstName>Katrijn</firstName>")))
                                .andExpect(content().string(containsString("</seaAreas><programs><identifier>KB_")))
                                .andExpect(content().string(containsString("SDN:P02::VDFC")))
                                .andExpect(content().string(containsString("SDN:C19::1_2")))
                                .andExpect(content()
                                                .string(containsString(
                                                                "<identifier>https://edmo.seadatanet.org/report/230</identifier>")))
                                .andReturn();

                /// mvcResult =
                /// this.mockMvc.perform(MockMvcRequestBuilders.delete("/cruise?identifier=" +
                /// cruise.identifier))
                // .andExpect(status().is(204)).andReturn();
        }

        @Test
        public void testPostAndDeleteCruise() throws Exception {
                CruiseDTO cruise = getTestCruise1("BE11/2007_18-" + UUID.randomUUID());
                cruise.programs = new ArrayList<>();

                programUUID = UUID.randomUUID();
                ProgramDTO program = ProgramControllerTest.getTestProgram1("KB_" + programUUID);
                ProgramControllerTest.postProgram(this.mockMvc, program, objectMapper);
                cruise.programs.add(program.identifier);
                String json = objectMapper.writeValueAsString(cruise);
                this.mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/cruise")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(json))
                                // .andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(content().string(containsString("<collateCentre>")))
                                .andExpect(content().string(
                                                containsString("<collateCentre>SDN:EDMO::1778</collateCentre>")))
                                .andExpect(content().string(containsString("</collateCentre><departureHarbour>")))
                                .andExpect(content().string(containsString(
                                                "<departureHarbour>SDN:C38::BSH4510</departureHarbour>")))
                                .andExpect(content().string(containsString("</departureHarbour><arrivalHarbour")))
                                .andExpect(content().string(containsString("</arrivalHarbour>")))
                                // .andExpect(content().string(containsString("<chiefScientists><firstName>Katrijn</firstName><lastName>Baetens</lastName><organisation><term>"))).andExpect(content().string(containsString("<identifier>https://edmo.seadatanet.org/report/3330</identifier><name>Royal
                                // Belgian Institute of Natural Sciences, Operational Directorate Natural
                                // Environment, Belgian Marine Data
                                // Centre</name><urn>SDN:EDMO::3330</urn></term><phoneNumber>+32 (0)2 773
                                // 2111</phoneNumber><faxNumber>+32 (0)2 770
                                // 6972</faxNumber><emailAddress>bmdc@naturalsciences.be</emailAddress><website>http://www.bmdc.be</website><deliveryPoint>Gulledelle
                                // 100</deliveryPoint><city>Brussels</city><postalCode>1200</postalCode><country><term>"))).andExpect(content().string(containsString("<identifier>http://vocab.nerc.ac.uk/collection/C32/current/BE/</identifier><name>Belgium</name><urn>SDN:C32::BE</urn></term>"))).andExpect(content().string(containsString("</country>"))).andExpect(content().string(containsString("</organisation>"))).andExpect(content().string(containsString("</chiefScientists>
                                // ")))
                                .andExpect(content().string(containsString("<seaAreas>")))
                                .andExpect(content().string(containsString("<seaAreas>SDN:C19::1_2</seaAreas>")))
                                .andExpect(content().string(containsString("<platform>SDN:C17::11BE</platform>")))
                                .andExpect(content().string(containsString(
                                                "</platform><objectives>The objectives of the cruise are twofold: 1) to validate the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released. 2) to do fisheries monitoring</objectives><isCancelled>false</isCancelled>")))
                                .andReturn();

                // String content = mvcResult.getResponse().getContentAsString();
                // content = flattenString(content);
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/cruise?identifier=" + cruise.identifier))
                                // .andDo(print())
                                .andExpect(status().is(204)).andReturn();

                this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/program?identifier=" + program.identifier))
                                // .andDo(print())
                                .andExpect(status().is(204)).andReturn();
        }

        /**
         * *
         * Test if the test framework is capable of reading the test.properties
         * file.
         *
         * @throws Exception
         */
        @Test
        public void testProperties() throws Exception {
                String licenseString = env.getProperty("ears.csr.license");
                if (licenseString == null) {
                        fail();
                }
        }

        @Test
        // acquisition must be running for this to work
        public void testGetCruiseCSR() throws Exception {
                String identifier = "BE11/2007_18-" + UUID.randomUUID();
                CruiseDTO cruise = getTestCruise1(identifier);
                cruise.startDate = OffsetDateTime.parse("2019-09-16T08:15:00Z");
                cruise.endDate = OffsetDateTime.parse("2019-09-20T17:00:00Z");
                cruise.programs = new ArrayList<>();
                cruise.objectives = "The project is part of the continuous surveillance and evaluation of the quality of the marine environment in the region of the Belgian part of the North Sea 'BPNS' in the framework of the national obligations toward the Joint Assessment and Monitoring Programme (JAMP) of the OSPAR commission and the Water Framework Directive of the EC (2000/60/EC). OD Nature determines nutrients, salinity, suspended matter, dissolved oxygen, TOC and POC, chlorophyll a, phaeophytine, optical parameters and organic contaminants in the water column. Phytoplankton biomass and species composition as well as benthos species composition and biomass are also determined as part of the monitoring program. The other determinants (e.g. heavy metals and organic contaminants) in sediment and biota are determined in collaboration with ILVO-Fishery (ecological monitoring). Quality assurance and quality control during sampling and in the laboratory receive a high priority within the project.In this research project, novel and integrated passive sampler (PS)-based approaches (modelling and measurements) will be developed for both chemical exposure (monitoring) and biological effect assessment (passive dosing). Through the use of a broader array of PS techniques, applicable in a wide polarity range, the project will focus on the quantitation of an extended set of priority and emerging organic micropollutants and metals (targeted approach). Next to that, untargeted analysis with high-resolution mass spectrometry will be performed to develop qualitative screening approaches able to detect trace levels of a virtually unlimited number of known (suspect) and possibly unknown contaminants. For a selection of compounds, both the total concentration and labile fraction (i.e. bioavailable) will be determined. Additionally, to trace the Suspended Particulate Matter (SPM) towards its origin, carbon (12C/13C) and nitrogen (14N/15N) stable isotope ratios will be measured, since organic matter from marine and terrestrial origin has a different isotopic C and sometimes N signature. In some cases, the sources of the organic matter present in the marine environment might be identified. In addition, modelling techniques will be of great support.In the framework of the assessment of the effects of the construction and operation of offshore wind farms on small cetaceans, the RBINS uses Passive Acoustic Monitoring Devices: porpoise detectors (C-PoDs). A C-PoD consists of a hydrophone, a processor, batteries and a digital timing and logging system, and has an autonomy of up to four months (www.chelonia.co.uk). Data obtained provide an indication of the presence of harbor porpoises in the vicinity of the device, up to a distance of approximately 300m. Data obtained from one PoD can give an indication of presence/absence of porpoises, and can be compared to data obtained from PoDs moored at other locations – as such, the presence of porpoise in wind farm areas can be compared to the presence of porpoises in reference areas as well as compared throughout the year.Based on the results of standardised and ship-based seabird counts, the Research Institute for Nature and Forest (INBO) investigates the effects of offshore wind turbines on the presence of seabirds. Therefore, the INBO performs monthly surveys along fixed monitoring routes through the impact and control areas.The project ”MOMO” is part of the general and permanent duties of monitoring and evaluation of the effects of all human activities on the marine ecosystem to which Belgium is committed following the OSPAR-convention (1992). The general goals of the project are to reduce the dredging work on the BCS and in the coastal harbors and to obtain a detailed insight into the physical processes involved in the marine environment were the dredging works take place. This implies on the one hand policy supported research to decrease the sedimentation in the dredging areas and to evaluate alternative disposal strategies. On the other hand fundamental research on cohesive sediment dynamics is carried out in order to plan and estimate the effect of disposal of fine grained sediments on the marine ecosystem. The latter is carried out using numerical models and in situ measurements.Radiological monitoring on the Belgian part of the North Sea 'BPNS' in the frame of national and international obligations. Survey in the vicinity of the Franco-Belgian border; influence of aquatic releases from foreign nuclear sites on the marine environment; influence on the food chain. Radioactivity measurements on 25 fishes, 20 water samples (5 areas, 4x/y) and 20 sediment samples (5 areas, 4 x/y). Measurements: alpha spectrometry (fish), gamma spectrometry (fish, water and sediment), alpha- and beta-activity, K-40 (water). Program in the frame of the Belgian Federal Agency of Nuclear Control (FANC).";
                // objectives=string longer than 4000 chars.
                programUUID = UUID.randomUUID();
                ProgramDTO program = ProgramControllerTest.getTestProgram1("KB_" + programUUID);
                ProgramControllerTest.postProgram(this.mockMvc, program, objectMapper);
                cruise.programs.add(program.identifier);

                // cruise.startDate = OffsetDateTime.now().minusDays(5);
                // cruise.endDate = OffsetDateTime.now().plusDays(5);
                postCruise(mockMvc, cruise, objectMapper);

                String navServer = env.getProperty("ears.navigation.server");
                boolean isOffline = false;
                DatagramUtilities<Navigation> datagramUtilities = new DatagramUtilities<>(Navigation.class, navServer);
                try {
                        DatagramUtilities.tryConnect(datagramUtilities.getBaseUrl());
                } catch (IOException e) {
                        isOffline = true;
                }

                String licenseString = env.getProperty("ears.csr.license");
                this.mockMvc
                                .perform(MockMvcRequestBuilders.get("/api/cruise/csr?identifier=" + identifier))
                                // .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(content().string(not(containsString("4.0"))))
                                .andExpect(content().string(containsString("<gmi:MI_Metadata")))
                                .andExpect(content().string(containsString(
                                                "<gco:CharacterString>cruise-start</gco:CharacterString>")))
                                .andExpect(content().string(containsString(
                                                "<gco:CharacterString>cruise-end</gco:CharacterString>")))
                                .andExpect(content().string(containsString("COMMISSION REGULATION (EC) No 1205/2008")))
                                .andExpect(content().string(containsString(
                                                "<gmx:Anchor xlink:href=\"https://www.seadatanet.org/urnurl/SDN:L08::"
                                                                + licenseString)))
                                .andExpect(content()
                                                .string(containsString("disposal strategies.</gco:CharacterString>")))
                                .andReturn();
                if (!isOffline) {
                        this.mockMvc
                                        .perform(MockMvcRequestBuilders.get("/api/cruise/csr?identifier=" + identifier))
                                        // .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(content().string(containsString(
                                                        "<gml:posList srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\" srsDimension=\"2\">")))
                                        .andReturn();
                } else {
                        this.mockMvc
                                        .perform(MockMvcRequestBuilders.get("/api/cruise/csr?identifier=" + identifier))
                                        // .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(content().string(not(containsString(
                                                        "<gml:posList srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\" srsDimension=\"2\">"))))
                                        .andExpect(content().string(containsString(
                                                        "<!--No valid bounding box provided. Bounding box filled with defaults!: -180, -90, 180, 90-->")))
                                        .andReturn();
                }
                this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/cruise?identifier=" + identifier))
                                // .andDo(print())
                                .andExpect(status().is(204)).andReturn();

                File file = new File(System.getProperty("java.io.tmpdir") + "/test.xml");

                FileUtils.write(file, content().toString(), StandardCharsets.UTF_8.name());

        }

        public static String flattenString(String input) {
                return input.replaceAll(T_TABS.pattern(), "").replaceAll(T_NEWLINES.pattern(), "")
                                .replaceAll(T_NEWLINES2.pattern(), "").replaceAll(T_EMPTYLINES.pattern(), "");

        }

        public static Pattern T_TABS = Pattern.compile("\t");
        public static Pattern T_SPACES = Pattern.compile(" +");
        public static Pattern T_NEWLINES = Pattern.compile("\r\n");
        public static Pattern T_NEWLINES2 = Pattern.compile("\n");
        public static Pattern T_EMPTYLINES = Pattern.compile("([\\n\\r]+\\s*)*$");
}
