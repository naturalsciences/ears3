/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.html;

import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.service.PersonService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Thomas Vandenberghe
 */
@Controller
@RequestMapping(value = "")
public class HtmlPersonController {

    @Autowired
    private PersonService personService;
    
        @GetMapping(value = {"/persons"}, produces = {"text/html; charset=utf-8"})
    public String persons() {
        return "persons";
    }

    @GetMapping("person/new")
    public String showSignUpForm(Person program) {
        return "person-new";
    }

    @PostMapping("/create-person")
    public String addPerson(@Valid Person person, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "person-create";
        }
        personService.save(person);
    //    model.addAttribute("person", personService.findAll());
        return "redirect:/event";
    }

    @GetMapping("person/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Person program = personService.findById(id);

        model.addAttribute("person", program);
        return "program-update";
    }

    @PostMapping("person/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid Person program,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            program.setId(id);
            return "person-update";
        }

        personService.save(program);
     //   model.addAttribute("persons", personService.findAll());
        return "redirect:/index";
    }

    @GetMapping("/delete/{id}")
    public String deletePerson(@PathVariable("id") long id, Model model) {
        Person person = personService.findById(id);
        personService.delete(person);
    //    model.addAttribute("programs", personService.findAll());
        return "index";
    }
}
