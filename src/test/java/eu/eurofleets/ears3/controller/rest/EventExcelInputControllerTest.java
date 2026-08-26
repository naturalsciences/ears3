package eu.eurofleets.ears3.controller.rest;

//import com.fasterxml.jackson.databind.JsonNode;

import eu.eurofleets.ears3.dto.ActorProgramTZDTO;
import eu.eurofleets.ears3.service.ToolService;
import eu.eurofleets.ears3.utilities.Constants;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;
import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.dto.PersonDTO;
import eu.eurofleets.ears3.dto.ProgramDTO;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.LinkedDataTermService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static eu.eurofleets.ears3.controller.rest.EventControllerTest.assertEventCount;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.param;

@SpringBootTest(classes = {Application.class})
@WebAppConfiguration
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
//reset the database to base state before each test method
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

    @BeforeEach
    public void setup() throws Exception {
        ToolService.clearCache();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    PersonDTO joan = new PersonDTO("Joan", "Backers", null, null, null, "joan.backers@naturalsciences.be");
    PersonDTO notJoan = new PersonDTO("NotJoan", "NotJoan", null, null, null, "Notjoan.backers@naturalsciences.be");

    private MockMultipartFile createMockFile(String filename) throws IOException {
        return new MockMultipartFile(
                "file",
                filename,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // MediaType.APPLICATION_OCTET_STREAM_VALUE,
                new ClassPathResource(filename).getInputStream());
    }

    private ResultActions postExcel(MockMultipartFile mockMultipartFile, PersonDTO actor, String program, String timezone, int expectedStatus) throws Exception {
        ActorProgramTZDTO apd = new ActorProgramTZDTO(actor, program, timezone);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "apd",                                   // must match your @RequestPart("apd") name
                "",                                       // no filename needed for a data part
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(apd));

        return this.mockMvc.perform(MockMvcRequestBuilders.multipart("/api/event/import")
                        .file(mockMultipartFile)
                        .file(jsonPart)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(expectedStatus));
    }

    @Test
    public void validateOkSubmitter() throws Exception {
        MockMultipartFile mockFile = createMockFile("test-5problems.xlsx");
        postExcel(mockFile, joan, null, "UTC", 409);//we used the 5problems excel as inputfile so it is expected to get a 409
    }

    @Test
    public void validateErrorFile() throws Exception {
        MockMultipartFile mockFile = createMockFile("test-5problems.xlsx");
        postExcel(mockFile, joan, null, "UTC",409)
                .andExpect(content().string(containsString("\"row\":1")))
                .andExpect(content().string(containsString("\"row\":2")))
                .andExpect(content().string(containsString("\"row\":3")))
                .andExpect(content().string(containsString("\"row\":8")))
                .andExpect(content().string(containsString("\"row\":9")))
                .andExpect(content().string(containsString("\"row\":10")))
                .andExpect(content().string(containsString("\"row\":11")))
                .andExpect(content().string(containsString("\"row\":13")))
                .andExpect(content().string(containsString("\"row\":16")));
    }

    @Test
    public void validateErrorHourFile() throws Exception {
        MockMultipartFile mockFile = createMockFile("test-hrproblems.xlsx");
        postExcel(mockFile, joan, null, "UTC", 409)
                .andExpect(content().string(containsString("\"row\":1")))
                .andExpect(content().string(containsString("\"row\":2")))
                .andExpect(content().string(containsString("\"row\":3")))
                .andExpect(content().string(containsString("\"row\":4")))
                .andExpect(content().string(containsString("\"row\":5")))
                .andExpect(content().string(containsString("\"row\":6")))
                .andExpect(content().string(containsString("\"row\":7")))
                .andExpect(content().string(containsString("\"row\":8")))
                .andExpect(content().string(containsString("\"row\":9")));
    }

    public static ProgramDTO getTestProgram(String identifier) {
        List<PersonDTO> principalInvestigators1 = Arrays.asList(new PersonDTO[]{
                new PersonDTO("Seppe", "Machiels", "SDN:EDMO::3330", null, null,
                        "seppe.machiels@mil.be")});
        return new ProgramDTO(identifier, principalInvestigators1,
                "General activities of the Belgica, such as navigation, personnel operations,...",
                null, "Belgica operations", null);
    }

    @Test
    public void validateOkFile() throws Exception {
        ProgramDTO pr = getTestProgram("11BU_operations");
        ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

        MockMultipartFile mockFile = createMockFile("test-noproblems.xlsx");
        postExcel(mockFile, joan, null, "UTC", 201);

        assertEventCount("/api/events", 50, this.mockMvc, this.objectMapper); //default size of 50.
        assertEventCount("/api/events?size=25", 25, this.mockMvc, this.objectMapper);
        assertEventCount("/api/events?size=50&page=0", 50, this.mockMvc, this.objectMapper);
        assertEventCount("/api/events?size=145", 145, this.mockMvc, this.objectMapper);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/api/events?size=145")
                        .accept(Constants.APPLICATION_XML_UTF8))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("<name>All persons</name>")))
                .andExpect(content().string(containsString("<name>Belgica</name>")))
                .andExpect(content().string(containsString("<name>Unknown sparker</name>")))
                .andExpect(content().string(containsString("<name>Crew</name>")))
                .andExpect(content().string(containsString("<name>Command</name>")))
                .andExpect(content().string(containsString("<name>Scientists</name>")))
                .andExpect(content().string(containsString("<name>Exercise</name>")))
                .andExpect(content().string(containsString("<name>Meeting</name>")))
                .andExpect(content().string(containsString("<name>Recreation</name>")))
                .andExpect(content().string(containsString("<name>Testing</name>")))
                .andExpect(content().string(not(containsString("<name>Sheltering</name>"))))
                .andExpect(content().string(containsString("<identifier>11BU_operations</identifier>")))
                .andExpect(content().string(containsString("<name>Belgica operations</name>")))
                .andExpect(content().string(containsString("<timeStamp>2023-10-14T15:30:00Z</timeStamp>")))
                .andExpect(content().string(containsString("<timeStamp>2023-10-14T16:32:00Z</timeStamp>")))
                .andReturn();


        postExcel(mockFile, joan, null, "UTC", 409); //attempting double entry!
    }

    @Test
    public void validatePredefinedProgram() throws Exception {
        ProgramDTO pr = getTestProgram("11BU_operations");
        ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);

        ProgramDTO pr2 = getTestProgram("test_program");
        ProgramControllerTest.postProgram(this.mockMvc, pr2, objectMapper);

        MockMultipartFile mockFile = createMockFile("test-noproblems.xlsx");

        postExcel(mockFile, joan, "11BU_operations", "UTC", 201);
        //these are all created with an overridden program, named test_program. There cannot be a unique event clash as the programmes are different
        postExcel(mockFile, joan, "test_program", "UTC", 201);

    }

    @Test
    public void validateOkFileExtraColumn() throws Exception {
        ProgramDTO pr = getTestProgram("11BU_operations");
        ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);
        MockMultipartFile mockFile = createMockFile("test-noproblemsExtraColAndSwitchedCol.xlsx");

        postExcel(mockFile, joan, null, "UTC", 201);
    }

    @Test
    public void validateOkFileMissingHourColumn() throws Exception {
        ProgramDTO pr = getTestProgram("11BU_operations");
        ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);
        Logger.getLogger("EventExcelInputConstrollerTest").log(Level.INFO, "Posted program");
        MockMultipartFile mockFile = createMockFile("test-missingHourCol.xlsx");
        Logger.getLogger("EventExcelInputConstrollerTest").log(Level.INFO, "Starting to post events from file");

        postExcel(mockFile, joan, null, "UTC", 409);
        //Since we are missing a required header we expect a failure to create the Excel Event
    }

    @Test
    public void validateVarious() throws Exception {
        ProgramDTO pr = getTestProgram("11BU_operations");
        ProgramControllerTest.postProgram(this.mockMvc, pr, objectMapper);
        MockMultipartFile mockFile = createMockFile("test-various.xlsx");
        postExcel(mockFile, joan, null, "UTC", 409); //various mistakes
    }

    /*Refactor Idea:  In order to replace the static definitions of these properties and DEFS and CATMAP, create a function based on this concept that fills in the
     * maps from the custom_ldts.json file.
     * for the propertiesMap, we'll have to change it to getting the empty poperty from the map, fill the current value, push that value to another (props) map
     * */
        /* @Test
        public void justatest() throws Exception {
        
                Map<String, LinkedDataTermDTO> DEFS = new HashMap<>();
                Map<String, LinkedDataTermDTO> CATMAP = new HashMap<>();
                Map<String, PropertyDTO> propertiesMap = new HashMap<>();
        
                JsonNode rootNode;
                ObjectMapper objectMapper;
                objectMapper = new ObjectMapper();
                File jsonFile = new ClassPathResource("custom_ldts.json").getFile();
                rootNode = objectMapper.readTree(jsonFile);
        
                JsonNode defs = rootNode.get("defs");
                ArrayList<LinkedHashMap<String,String>> defList = objectMapper.convertValue(defs, ArrayList.class);
        
                JsonNode catmap = rootNode.get("catmap");
                ArrayList<LinkedHashMap<String,String>> cmList = objectMapper.convertValue(catmap, ArrayList.class);
        
                JsonNode properties = rootNode.get("properties");
                ArrayList<LinkedHashMap<String,String>> propList = objectMapper.convertValue(properties, ArrayList.class);
        
                for( LinkedHashMap<String,String> item : defList ){
                        System.out.println( "het item: " + item + "\n");
                        LinkedDataTermDTO ldtDTO = new LinkedDataTermDTO(item.get("identifier"), item.get("transitiveLdIdentifier"), item.get("name"));
                        String key = StringUtils.capitalize(StringUtils.lowerCase( item.get("name") ) );
                        DEFS.put(key, ldtDTO);
                }
        
                for( LinkedHashMap<String,String> item : cmList ){
                        System.out.println( "het item: " + item + "\n");
                        String key = StringUtils.capitalize(StringUtils.lowerCase( item.get("name") ) );
                        String prop = StringUtils.capitalize(StringUtils.lowerCase( item.get("prop") ) );
                        LinkedDataTermDTO ldtDTO = DEFS.get(prop);
                        CATMAP.put(key, ldtDTO);
                }
        
                //Value moet nog wel ingevuld worden in EventExcelService dan
                for( LinkedHashMap<String, String> item : propList ){
                        System.out.println("De property: " + item + "\n");
                        LinkedDataTermDTO ldtDTO = new LinkedDataTermDTO(item.get("identifier"), item.get("transitiveLdIdentifier"), item.get("name"));
                        PropertyDTO pDTO = new PropertyDTO(ldtDTO, item.get("value"), item.get("uom"));
                        String key = StringUtils.capitalize(StringUtils.lowerCase(item.get("name")));
                        propertiesMap.put(key, pDTO);
                }
        
                int a=5;
                //String prettyPrintEmployee = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
                //System.out.println(prettyPrintEmployee+"\n");
        
        } */

}