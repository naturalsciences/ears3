/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.html;

import eu.eurofleets.ears3.domain.Program;
import eu.eurofleets.ears3.service.ProgramService;
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
@RequestMapping(value = "program")
public class HtmlProgramController {

    @Autowired
    private ProgramService programService;

    @GetMapping("/create")
    public String showSignUpForm(Program program) {
        return "program-create";
    }

    @PostMapping("/create-program")
    public String addProgram(@Valid Program program, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "program-new";
        }
        programService.save(program);
        model.addAttribute("programs", programService.findAll());
        return "redirect:/event";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Program program = programService.findById(id);

        model.addAttribute("program", program);
        return "program-update";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") long id, @Valid Program program,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            program.setId(id);
            return "program-update";
        }

        programService.save(program);
        model.addAttribute("programs", programService.findAll());
        return "redirect:/index";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, Model model) {
        Program program = programService.findById(id);
        programService.delete(program);
        model.addAttribute("programs", programService.findAll());
        return "index";
    }
}
