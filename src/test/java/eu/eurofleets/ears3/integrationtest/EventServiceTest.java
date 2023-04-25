/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.integrationtest;

import eu.eurofleets.ears3.controller.rest.EventControllerTest;
import eu.eurofleets.ears3.service.EventRepository;
import eu.eurofleets.ears3.service.EventService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Thomas Vandenberghe
 */
@RunWith(MockitoJUnitRunner.class)
@ComponentScan(basePackages = {"eu.eurofleets.ears3.domain", " eu.eurofleets.ears3.service"})
@TestPropertySource(locations = "classpath:test.properties")
public class EventServiceTest {

    @Mock
    private Environment env;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    EventService eventService = new EventService(eventRepository, env);

    @Before
    public void createMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test1() {
        eventService.save(EventControllerTest.getTestEvent2());
    }
    // write test cases here
}
