package eu.eurofleets.ears3.controller.html;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller()
@RequestMapping(value = "")
public class HtmlImportExcelController {
    @Autowired
    private Environment env;

    @RequestMapping(method = {RequestMethod.GET}, value = {"/excel/import"}, produces = {"text/html; charset=utf-8"})
    public String event( Model model) {
        String maxFileSize = env.getProperty("spring.servlet.multipart.max-file-size");
        File shipTemplate = null;
        /*try {
            shipTemplate = new ClassPathResource("static/ship_log_template.xlsx").getFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
        model.addAttribute("maxFileSize", maxFileSize);
        //model.addAttribute("shipTemplateURI", shipTemplate.toURI());
        return "excel-upload";
    }

}
