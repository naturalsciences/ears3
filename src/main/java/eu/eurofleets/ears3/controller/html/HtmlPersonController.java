/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.html;

import eu.eurofleets.ears3.domain.Person;
import eu.eurofleets.ears3.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Thomas Vandenberghe
 */
@Controller
@RequestMapping(value = "person")
public class HtmlPersonController {

    @Autowired
    private PersonService personService;
    
        @GetMapping(value = {"/persons"}, produces = {"text/html; charset=utf-8"})
    public String persons() {
        return "persons";
    }

    @GetMapping("/new")
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

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Person program = personService.findById(id);

        model.addAttribute("person", program);
        return "program-update";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid Person program,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            program.setId(id);
            return "person-update";
        }

        personService.save(program);
        return "redirect:/index";
    }

    @DeleteMapping("/delete/{id}")
    public String deletePerson(@PathVariable("id") long id, Model model) {
        Person person = personService.findById(id);
        personService.delete(person);
        return "index";
    }
}
