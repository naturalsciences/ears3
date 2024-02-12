package eu.eurofleets.ears3.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Property;
import eu.eurofleets.ears3.dto.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.eurofleets.ears3.service.EventRepository;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.LinkedDataTermService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.util.SocketUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.sound.midi.Soundbank;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = { "eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service" })
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) //reset the database to base state before each test method
public class EventExcelInputControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LinkedDataTermService linkedDataTermService;

    @Autowired
    private EventService eventService;

    private MockMvc mockMvc;

    /* public EventExcelInputControllerTest() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    
    } */

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    PersonDTO joan = new PersonDTO("Joan", "Backers", null, null, null, "joan.backers@naturalsciences.be");

    @Test
    public void validateHeaders() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // MediaType.APPLICATION_OCTET_STREAM_VALUE,
                new ClassPathResource("test.xlsx").getInputStream());
        assertTrue(this.mockMvc != null);

        this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/event")
                .file(mockMultipartFile)//)
                .header("person", joan.getFirstName()+'#'+joan.getLastName()))
                //.header("person", objectMapper.writeValueAsString(this.joan)))
                .andDo(print());
                //.andExpect(status().isOk());
    }

    //@Test
    void processSpreadsheetEvents() {
    }

    //@Test
    void saveSpreadsheetEvents() {
    }

    public static EventDTO getTestEvent() {
        PersonDTO joan = new PersonDTO("Joan", "Backers", null, null, null, "joan.backers@naturalsciences.be");
        EventDTO event = new EventDTO();
        event.setIdentifier(null); // this is a purely new event.
        event.setEventDefinitionId("e3c8df0d-02e9-446d-a59b-224a14b89f9a");
        event.setTimeStamp(OffsetDateTime.parse("2019-04-25T11:08:00Z"));
        event.setToolCategory(new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L05/current/50/", null,
                "sediment grabs"));
        event.setTool(new ToolDTO(
                new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/L22/current/TOOL0653/", null,
                        "Van Veen grab"),
                null));
        event.setProcess(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_1", null, "Sampling"));
        event.setAction(new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#act_2", null, "End"));
        event.setSubject(new LinkedDataTermDTO("http://vocab.nerc.ac.uk/collection/C77/current/G71/", null,
                "In-situ seafloor measurement/sampling"));
        event.setActor(joan);
        event.setPlatform("SDN:C17::11BE");
        List<PropertyDTO> properties = new ArrayList<>();
        properties.add(new PropertyDTO(
                new LinkedDataTermDTO("http://ontologies.orr.org/fish_count", null, "fish_count"), "89",
                null));
        properties.add(new PropertyDTO(
                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_21", null, "depth_m"),
                "3", "m"));
        properties.add(new PropertyDTO(
                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_4", null, "label"),
                "W04", null));
        properties.add(new PropertyDTO(
                new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pry_16", null, "sampleId"),
                "20190506_12", null));
        event.setProperties(properties);
        return event;
    }

    @Test
    public void genericTest(){


        /*LinkedDataTerm propertyLdTerm = getTestEvent();
        propertyLdTerm = linkedDataTermService.findOrCreate(propertyLdTerm); // replace it with a managed one, either

        propertyLdTerm.*/

        EventDTO event = getTestEvent();
        System.out.println(event);
        event.setProgram("2020-MF");
        Event savedKnownAction = eventService.save(event);

        LinkedDataTermDTO start = new LinkedDataTermDTO("http://ontologies.ef-ears.eu/ears2/1#pro_3_johnnyAction_Start", null,"Start");
        event.setAction(start);
        event.setIdentifier(UUID.randomUUID().toString());
        Event savedNewAction = eventService.save(event);


        System.out.println(savedKnownAction);
        System.out.println(savedNewAction);

        System.out.println("HIER");
    }
}