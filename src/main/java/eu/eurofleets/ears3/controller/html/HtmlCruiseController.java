/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.html;

import eu.eurofleets.ears3.domain.Cruise;
import eu.eurofleets.ears3.service.CruiseService;
import java.util.ArrayList;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Thomas Vandenberghe
 */
@Controller
@RequestMapping(value = "cruises")
public class HtmlCruiseController {

    @Autowired
    private CruiseService cruiseService;
    
    @Autowired
    private Environment env;

    @RequestMapping(method = RequestMethod.GET)
    public String showTableWithValues(Model model) {
        //list with Persons
        Set<Cruise> cruises = cruiseService.findAllByPlatformIdentifier(env.getProperty("ears.platform"));

        model.addAttribute("cruises", cruises);

        return "cruises";
    }

}
