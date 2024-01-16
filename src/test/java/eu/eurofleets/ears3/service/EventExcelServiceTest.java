package eu.eurofleets.ears3.service;

import eu.eurofleets.ears3.Application;
import eu.eurofleets.ears3.dto.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class }, properties = "spring.main.allow-bean-definition-overriding=true")
@WebAppConfiguration
@ComponentScan(basePackages = { "eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service" })
@TestPropertySource(locations = "classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) //reset the database to base state before each test method
class EventExcelServiceTest {

  @MockBean
  EventExcelService excelService;

  @Autowired
  MockMvc mockMvc;

  //@Test
  void convert() {
  }

  //@Test
  void validateAllTabs() {
  }

  @Test
  void validateHeaders() throws Exception {
    MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "multipartFile",
            "test.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            new ClassPathResource("test.xlsx").getInputStream());

    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/event")
            .file(mockMultipartFile)
            .header("person", new PersonDTO()));
            //.andExpect(status().isOk());
  }


  //@Test
  void processSpreadsheetEvents() {
  }

  //@Test
  void saveSpreadsheetEvents() {
  }
}