package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlatformController {

    @Autowired
    private PlatformService platformService;
    
    @Autowired
    private Environment env;

    @RequestMapping(method = {RequestMethod.GET}, value = {"platform/current"}, produces = {"application/xml; charset=utf-8", "application/json"})
    public Platform getCurrentPlatform() {
        String identifier = env.getProperty("ears.platform");
        Platform p = platformService.findByIdentifier(identifier);
        return p;

    }
}
