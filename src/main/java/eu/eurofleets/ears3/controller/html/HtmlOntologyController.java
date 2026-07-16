package eu.eurofleets.ears3.controller.html;

import eu.eurofleets.ears3.controller.ontology.OntologyEditingGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller()
@RequestMapping(value = "")
public class HtmlOntologyController {

    @Autowired
    private OntologyEditingGuard editingGuard;

    @GetMapping(value = {"/ontology"}, produces = {"text/html; charset=utf-8"})
    public String ontology(Model model) {
        model.addAttribute("editingEnabled", editingGuard.isEditingEnabled());
        model.addAttribute("importEnabled", editingGuard.isImportEnabled());
        return "ontology";
    }
}
