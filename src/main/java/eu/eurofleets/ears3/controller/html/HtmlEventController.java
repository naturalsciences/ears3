package eu.eurofleets.ears3.controller.html;

import be.naturalsciences.bmdc.cruise.model.IProperty;
import eu.eurofleets.ears3.domain.Event;
import eu.eurofleets.ears3.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller()
@RequestMapping(value = "")
public class HtmlEventController {

    @Autowired
    private EventService eventService;

    @RequestMapping(method = {RequestMethod.GET}, value = {"/event/new", "/index", "", "/", "../"}, produces = {"text/html; charset=utf-8"})
    public String event() {
        return "event-new";
    }

    @GetMapping(value = {"/events"}, produces = {"text/html; charset=utf-8"})
    public String events() {
        return "events";
    }

    @GetMapping(value = {"event/{id}/edit"}, produces = {"text/html; charset=utf-8"})
    public String eventEdit(@PathVariable("id") String id, Model model) {
        Event event = eventService.findByIdentifier(id);
        model.addAttribute("event", event);
        model.addAttribute("properties", event.getProperties()); 
        return "event-edit";
    }

    @RequestMapping(method = {RequestMethod.GET}, value = {"/settings"}, produces = {"text/html; charset=utf-8"})
    public String settings() {
        return "settings";
    }
}
