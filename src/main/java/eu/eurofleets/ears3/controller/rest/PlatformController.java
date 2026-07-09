package eu.eurofleets.ears3.controller.rest;

import eu.eurofleets.ears3.domain.Platform;
import eu.eurofleets.ears3.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

@RestController
@RequestMapping(value = "/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlatformController {

    @Autowired
    private PlatformService platformService;
    
    @Autowired
    private Environment env;

    @GetMapping(value = {"platform/current"}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Platform getCurrentPlatform() {
        String identifier = env.getProperty("ears.platform");
        Platform p = platformService.findByIdentifier(identifier);
        return p;

    }
}
