/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3.controller.html;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Nils Van den Steen
 */
@Controller
@RequestMapping(value = "")
public class HtmlManualController {

    @GetMapping(value = { "/manual" }, produces = { "text/html; charset=utf-8" })
    public String manual() {
        return "manual";
    }
}
