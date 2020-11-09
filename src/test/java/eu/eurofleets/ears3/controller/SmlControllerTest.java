/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.domain.Country;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Deployment;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.Harbour;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.domain.Project;
import eu.eurofleets.ears3.domain.Property;
import eu.eurofleets.ears3.domain.SamplingEvent;
import eu.eurofleets.ears3.domain.SeaArea;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.dto.EventDTO;
import eu.eurofleets.ears3.dto.LinkedDataTermDTO;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.PropertyDTO;
import eu.eurofleets.ears3.dto.ToolDTO;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.hamcrest.core.StringContains.containsString;
import org.junit.Before;
import org.junit.Test;
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
public class SmlControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Autowired
    private ObjectMapper objectMapper;

    public Cruise generateANiceTestCruise() {
        Country belgium = new Country(new LinkedDataTerm("C32:BE", null, "Belgium"));
        LinkedDataTerm odnaturet = new LinkedDataTerm("SDN:EDMO::3330", null, "Royal Belgian Institute of Natural Sciences, Operational Directorate Natural Environment");
        Organisation odnature = new Organisation(odnaturet, "02/555.555", "02/555.556", "info@odnature.be", "http://odnature.naturalsciences.be", "Vautierstraat 1", "Brussel", "1000", belgium);
        Platform p = new Platform(new LinkedDataTerm("SDN:C17::11BE", null, "Belgica"), new LinkedDataTerm("SDN:L06::31", null, "research vessel"), odnature);
        Cruise c = new Cruise();

        c.setPlatform(p);

        Harbour h = new Harbour(new LinkedDataTerm("SDN:C38::BSH4510", null, "Zeebrugge"), belgium);

        c.setArrivalHarbour(h);
        c.setDepartureHarbour(h);
        c.setIdentifier("BE11/2007_18");
        c.setName("BE11/2007_18");
        c.setStartDate(OffsetDateTime.of(2019, 5, 6, 0, 0, 0, 0, ZoneOffset.UTC));
        c.setEndDate(OffsetDateTime.of(2019, 5, 7, 0, 0, 0, 0, ZoneOffset.UTC));
        c.setObjectives("The objectives of the cruise are to validate the modeling efforts of the last 2 years using the COHERENS model. Rubber ducks will be released.");

        List<SeaArea> seaAreas = Arrays.asList(new SeaArea[]{new SeaArea(new LinkedDataTerm("SDN:C19::1_2", null, "North Sea"))});

        List<Cruise> cruises = Arrays.asList(new Cruise[]{c});
        c.setSeaAreas(seaAreas);

        LinkedDataTerm sumot = new LinkedDataTerm("SDN:EDMO::3330", null, "Royal Belgian Institute of Natural Sciences, Operational Directorate Natural Environment, SUMO");
        Organisation sumo = new Organisation(sumot, "02/555.551", "02/555.552", "info@sumo.be", "http://odnature.naturalsciences.be", "Vautierstraat 1", "Brussel", "1000", belgium);

        List<Person> chiefScientists = Arrays.asList(new Person[]{new Person("Michael", "Fettweis", sumo, null, null, null)});
        c.setChiefScientists(chiefScientists);

        LinkedDataTerm bmdct = new LinkedDataTerm("SDN:EDMO::1778", null, "Belgian Marine Data Centre");
        Organisation bmdc = new Organisation(bmdct, "02/555666", "02/555662", "info@bmdc.be", "www.bmdc.be", "Vautierstraat 1", "Brussel", "1000", belgium);
        c.setCollateCentre(bmdc);

        Project prj = new Project(new LinkedDataTerm("SDN:EDMERP::11436", null, "An ecosystem approach in sustainable fisheries management through local ecological knowledge {acronym=&quot;LECOFISH&quot; organisation=&quot;Ghent University, Maritime Institute / Dpt. International Public Law&quot; country=&quot;Belgium&quot;}"));
        List<Project> projects = Arrays.asList(new Project[]{prj});
        Program pr = new Program("MOMO", cruises, chiefScientists, "The MOMO programme is active since 2012 and investigates...", projects);

        List<Program> programmes = Arrays.asList(new Program[]{pr});
        c.setPrograms(programmes);

        Person joan = new Person("Joan", "Backers", null, null, null, "jb@rbins.be");

        List<Event> events = new ArrayList<>();
        Event event = new SamplingEvent();
        event.setIdentifier("e3c8df0d-02e9-446d-a59b-224a14b89f9a");
        event.setTimeStamp(OffsetDateTime.parse("2019-05-06T16:44:18Z"));
        event.setToolCategory(new LinkedDataTerm("http://vocab.nerc.ac.uk/collection/L05/current/50/", null, "sediment grabs"));
        event.setTool(new Tool(new LinkedDataTerm("http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653/", null, "Van Veen grab"), null));
        event.setProcess(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#pro_1", null, "Sampling"));
        event.setAction(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#act_2", null, "End"));
        event.setSubject(new LinkedDataTerm("http://vocab.nerc.ac.uk/collection/C77/current/G71/", null, "In-situ seafloor measurement/sampling"));
        event.setActor(joan);
        event.setProgram(pr);
        List<Property> properties = new ArrayList<>();
        properties.add(new Property(new LinkedDataTerm("http://ontologies.orr.org/fish_count", null, "fish_count"), "89", null));
        properties.add(new Property(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1/11BE#pry_21", null, "depth_m"), "3", "m"));
        properties.add(new Property(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#pry_4", null, "label"), "W04", null));
        properties.add(new Property(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#pry_16", null, "sampleId"), "20190506_12", null));
        event.setProperties(properties);
        events.add(event);

        Event event2 = new Deployment();
        event2.setIdentifier("e3c8df0d-02e9-446d-a59b-224a14b87890");
        event2.setTimeStamp(OffsetDateTime.parse("2019-05-07T11:10:18Z"));
        event2.setToolCategory(new LinkedDataTerm("http://vocab.nerc.ac.uk/collection/L05/current/50/", null, "Benthic lander"));
        event2.setTool(new Tool(new LinkedDataTerm("http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653/", null, "MOMO frame"), null));
        event2.setProcess(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#pro_1", null, "Deployment"));
        event2.setAction(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#act_2", null, "Start"));
        event2.setSubject(new LinkedDataTerm("http://vocab.nerc.ac.uk/collection/C77/current/G71/", null, "Sediment dynamics"));
        event2.setActor(joan);
        event2.setProgram(pr);
        event2.setPlatform(p);
        List<Property> properties2 = new ArrayList<>();
        properties2.add(new Property(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1/11BE#pry_21", null, "depth_m"), "19", "m"));
        properties2.add(new Property(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#pry_4", null, "label"), "W08", null));
        properties2.add(new Property(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#pry_16", null, "deployment"), "20190506_12", null));
        event2.setProperties(properties);
        events.add(event2);

        Event event3 = new Deployment();
        event3.setIdentifier("a1c8df0d-02e9-446d-a59b-224345b87890");
        event3.setTimeStamp(OffsetDateTime.parse("2019-05-07T12:50:18Z"));
        event3.setToolCategory(new LinkedDataTerm("http://vocab.nerc.ac.uk/collection/L05/current/50/", null, "Benthic lander"));
        event3.setTool(new Tool(new LinkedDataTerm("http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653/", null, "MOMO frame"), null));
        event3.setProcess(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#pro_1", null, "Deployment"));
        event3.setAction(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#act_2", null, "Start"));
        event3.setSubject(new LinkedDataTerm("http://vocab.nerc.ac.uk/collection/C77/current/G71/", null, "Sediment dynamics"));
        event3.setActor(joan);
        event3.setPlatform(p);
        event3.setProgram(pr);
        List<Property> properties3 = new ArrayList<>();
        properties3.add(new Property(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1/11BE#pry_21", null, "depth_m"), "5", "m"));
        properties3.add(new Property(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#pry_4", null, "label"), "W03", null));
        properties3.add(new Property(new LinkedDataTerm("http://ontologies.ef-ears.eu/ears2/1#pry_16", null, "deployment"), "20190506_17", null));
        event3.setProperties(properties);
        events.add(event3);

        c.setEvents(events);
        return c;
    }

    @Test
    @Ignore
    public void testGetBasicSml() throws Exception {
        // String json = objectMapper.writeValueAsString(generateANiceTestCruise());
        MvcResult readSmlBefore = this.mockMvc.perform(MockMvcRequestBuilders.get("/sml?platformUrn=SDN:C17::11BE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<PhysicalSystem xmlns:swe=\"http://www.opengis.net/swe/2.0\" xmlns=\"sml\" xmlns:gco=\"http://www.isotc211.org/2005/gco\" xmlns:sml=\"http://www.opengis.net/sensorml/2.0\" xmlns:gts=\"http://www.isotc211.org/2005/gts\" xmlns:gml=\"http://www.opengis.net/gml/3.2\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:gmd=\"http://www.isotc211.org/2005/gmd\">")))
                .andReturn();

    }

    @Test
    public void testGetPhysicalSystem() throws Exception {
        String json = objectMapper.writeValueAsString(CruiseControllerTest.getTestCruise2("BE11/2008_17-" + UUID.randomUUID()));
        MvcResult createCruise = this.mockMvc.perform(MockMvcRequestBuilders.post("/cruise").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn();

        EventDTO e = EventControllerTest.getTestEvent();
        e.program = "2020-MF";
        CruiseControllerTest.postProgram(this.mockMvc, "2020-MF", objectMapper);
        json = objectMapper.writeValueAsString(e);

        MvcResult createEvent = this.mockMvc.perform(MockMvcRequestBuilders.post("/event").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        e = EventControllerTest.getTestEvent2();
        e.program = "2020-MF";
        json = objectMapper.writeValueAsString(e);

        MvcResult createEvent2 = this.mockMvc.perform(MockMvcRequestBuilders.post("/event").contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        MvcResult readSmlAfter = this.mockMvc.perform(MockMvcRequestBuilders.get("/sml?platformUrn=SDN:C17::11BE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<sml:component name=\"Van Veen grab\" xlink:href=\"https://ears.bmdc.be/ears3/sml?deviceUrn=SDN:L22::TOOL0653\"/>")))
                .andReturn();

        String smlAfter = readSmlAfter.getResponse().getContentAsString();
        int a = 5;
        //  assertTrue(content.contains("cruises"));
    }

    @Test
    @Ignore
    public void testGetPhysicalComponent() throws Exception {

        MvcResult readSmlAfter = this.mockMvc.perform(MockMvcRequestBuilders.get("/sml?deviceUrn=SDN:L22::TOOL0653"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Van Veen grab Sampling End at 2019-05-06")))
                // .andExpect(content().string(containsString("<sml:value>bdf1b8a9-631d-406d-93aa-64829502d544</sml:value>")))
                .andExpect(content().string(containsString("<gml:identifier>http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653</gml:identifier>")))
                .andExpect(content().string(containsString("<sml:Term definition=\"http://vocab.nerc.ac.uk/collection/W06/current/CLSS0002\" id=\"sediment grabs\">")))
                .andExpect(content().string(containsString("<gco:CharacterString>Joan Backers</gco:CharacterString>")))
                .andExpect(content().string(containsString("<swe:Count definition=\"http://ontologies.orr.org/fish_count\">")))
                .andExpect(content().string(containsString("<swe:label>fish_count</swe:label>")))
                .andExpect(content().string(containsString("<swe:value>89</swe:value>")))
                .andExpect(content().string(containsString("<swe:Quantity definition=\"http://ontologies.ef-ears.eu/ears2/1/11BE#pry_21\">")))
                .andExpect(content().string(containsString("<swe:Text definition=\"http://ontologies.ef-ears.eu/ears2/1#pry_4\">")))
                .andReturn();

        String smlAfter = readSmlAfter.getResponse().getContentAsString();
    }

}
