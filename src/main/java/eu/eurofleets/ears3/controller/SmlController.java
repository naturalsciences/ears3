/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller;

import be.naturalsciences.bmdc.cruise.model.ILinkedDataTerm;
import be.naturalsciences.bmdc.sensormlgenerator.SensorMLBuilder;
import be.naturalsciences.bmdc.sensormlgenerator.SensorMLPrinter;
import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.service.CruiseService;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.PlatformService;
import eu.eurofleets.ears3.service.ToolService;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.websocket.server.PathParam;
import javax.xml.bind.JAXBException;
import net.opengis.sensorml.PhysicalComponentType;
import net.opengis.sensorml.PhysicalSystemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thomas
 */
@RestController
public class SmlController {

    @Autowired
    private PlatformService platformService;
    @Autowired
    private EventService eventService;
    @Autowired
    private ToolService toolService;
    public static Logger log = Logger.getLogger(SmlController.class.getSimpleName());

    @RequestMapping(method = {RequestMethod.GET}, value = {"sml"}, params = {"platformUrn"}, produces = {"application/xml"})
    public String getPhysicalSystem(@RequestParam(required = true, value = "platformUrn") String platformCode) throws JAXBException {
        Platform p = this.platformService.findByIdentifier(platformCode);
        if (p != null) {
            for (Cruise cruise : p.getCruises()) {
                List<Event> events = this.eventService.findByCruise(cruise);
                cruise.setEvents(events); //ensure all events are available.
            }

            SensorMLBuilder builder = new SensorMLBuilder(p, "https://ears.bmdc.be/ears3");
            PhysicalSystemType physicalSystem = builder.getPhysicalSystem();
            SensorMLPrinter instance = new SensorMLPrinter(physicalSystem, physicalSystem.getClass());
            String result = instance.getResult();

            return result;
        }
        return "failed";
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"sml"}, params = {"deviceUrn"}, produces = {"application/xml"})
    public String getPhysicalComponent(@RequestParam(required = true, value = "deviceUrn") String deviceUrn) throws JAXBException {
        Tool tool = this.toolService.findByIdentifier(deviceUrn);
        if (tool != null) {
            List<Event> events = this.eventService.findByTool(tool);
            SensorMLBuilder builder = new SensorMLBuilder(null, "https://ears.bmdc.be/ears3");

            PhysicalComponentType physicalComponent = builder.getPhysicalComponent(tool, events);

            SensorMLPrinter instance = new SensorMLPrinter(physicalComponent, physicalComponent.getClass());
            String result = instance.getResult();

            return result;
        }
        return "failed";
    }

}
