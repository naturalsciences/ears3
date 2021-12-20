/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.rest;

import be.naturalsciences.bmdc.sensormlgenerator.SensorMLBuilder;
import be.naturalsciences.bmdc.sensormlgenerator.SensorMLPrinter;
import eu.eurofleets.ears3.domain.Country;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.domain.LinkedDataTerm;
import eu.eurofleets.ears3.domain.Organisation;
import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.domain.Tool;
import eu.eurofleets.ears3.service.EventService;
import eu.eurofleets.ears3.service.PlatformService;
import eu.eurofleets.ears3.service.ToolService;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import net.opengis.sensorml.PhysicalComponentType;
import net.opengis.sensorml.PhysicalSystemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author thomas
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class SmlController {

    Country belgium = new Country(new LinkedDataTerm("http://vocab.nerc.ac.uk/collection/C32/current/BE", null, "Belgium"));
    LinkedDataTerm bmdcT = new LinkedDataTerm("https://edmo.seadatanet.org/report/3330", null, "Royal Belgian Institute of Natural Sciences, Operational Directorate Natural Environment, Belgian Marine Data Centre");
    Organisation bmdc = new Organisation(bmdcT, "02/555.555", "02/555.556", "info@bmdc.be", "http://odnature.naturalsciences.be/bmdc", "Vautierstraat 1", "Brussel", "1000", belgium);

    @Autowired
    private PlatformService platformService;
    @Autowired
    private EventService eventService;
    @Autowired
    private ToolService toolService;
    public static Logger log = Logger.getLogger(SmlController.class.getSimpleName());

    private String getUrl(HttpServletRequest request) {
        String scheme = request.getScheme().toLowerCase().replaceAll("\\/.*", "");
        String port = request.getServerPort() + "";
        port = port.equals("80") ? "" : ":" + port;
        String path = scheme + "://" + request.getServerName() + port + "/ears3";
        return path;
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"sml"}, params = {"platformUrn"}, produces = {"application/xml"})
    public String getPhysicalSystem(HttpServletRequest request, @RequestParam(required = true, value = "platformUrn") String platformUrn) throws JAXBException {
        Platform p = this.platformService.findByIdentifier(platformUrn);
        if (p != null) {

            SensorMLBuilder builder = new SensorMLBuilder(p, getUrl(request), bmdc);
            PhysicalSystemType physicalSystem = builder.getPhysicalSystem();
            SensorMLPrinter instance = new SensorMLPrinter(physicalSystem, physicalSystem.getClass());
            String result = instance.getResult();

            return result;
        }
        return "<WebErrorResponse><message>Platform with identifier " + platformUrn + " not found</message></WebErrorResponse>";
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"sml"}, params = {"deviceUrn", "platformUrn"}, produces = {"application/xml"})
    public String getPhysicalComponent(HttpServletRequest request, @RequestParam(required = true, value = "deviceUrn") String deviceUrn, @RequestParam(required = true, value = "platformUrn") String platformUrn) throws JAXBException {
        Tool tool = this.toolService.findByIdentifier(deviceUrn);
        if (tool != null) {
            List<Event> events = this.eventService.findByTool(tool);
            List<Event> newEvents = new ArrayList<>();
            for (Event event : events) {
                if (event.getPlatform().getTerm().getUrn().equals(platformUrn)) {
                    newEvents.add(event);
                }
            }
            SensorMLBuilder builder = new SensorMLBuilder(null, getUrl(request), bmdc);
            PhysicalComponentType physicalComponent = builder.getPhysicalComponent(tool, newEvents);
            SensorMLPrinter instance = new SensorMLPrinter(physicalComponent, physicalComponent.getClass());
            return instance.getResult();
        }
        return "<WebErrorResponse><message>Tool with identifier " + deviceUrn + " not found</message></WebErrorResponse>";
    }

}
