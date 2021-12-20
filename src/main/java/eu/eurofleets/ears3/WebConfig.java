/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.eurofleets.ears3;

import eu.eurofleets.ears3.service.SimpleJpaRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                "/js/**",
                "/css/**",
                "/images/**",
                "/json/**")
                .addResourceLocations(
                        "classpath:/static/js/",
                        "classpath:/static/css/",
                        "classpath:/static/images/",
                        "classpath:/static/json/");
    }

}
