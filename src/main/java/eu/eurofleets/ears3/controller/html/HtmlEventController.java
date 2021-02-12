package eu.eurofleets.ears3.controller.html;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller()
@RequestMapping(value = "html")
public class HtmlEventController {

    @RequestMapping(method = {RequestMethod.GET}, value = {"/event", "/index", "", "/", "../"}, produces = {"text/html; charset=utf-8"})
    public String event() {
        return "event";
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"/events"}, produces = {"text/html; charset=utf-8"})
    public String events() {
        return "events";
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"/settings"}, produces = {"text/html; charset=utf-8"})
    public String settings() {
        return "settings";
    }
}
