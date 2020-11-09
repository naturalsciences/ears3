/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3;

import eu.eurofleets.ears3.ontology.storage.StorageProperties;
import eu.eurofleets.ears3.ontology.storage.StorageService;
import eu.eurofleets.ears3.service.CruiseRepository;
import eu.eurofleets.ears3.service.SimpleJpaRepositoryImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author thomas
 */
@Configuration
@EnableWebMvc
@EnableJpaRepositories(
        repositoryBaseClass = SimpleJpaRepositoryImpl.class,
        basePackages = "eu.eurofleets.ears3.service"
)
public class WebConfig implements WebMvcConfigurer {

    /**
     * Setup a simple strategy: use all the defaults and return XML by default
     * when not sure.
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(true).
                favorParameter(false).
                //    parameterName("mediaType").
                ignoreAcceptHeader(false).
                defaultContentType(MediaType.APPLICATION_XML);
        //   mediaType("xml", MediaType.APPLICATION_XML).
        //   mediaType("json", MediaType.APPLICATION_JSON);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/html/js/**",
                "/html/css/**",
                "/html/json/**")
                .addResourceLocations(
                        "classpath:/static/js/",
                        "classpath:/static/css/",
                        "classpath:/static/json/");
    }

}
