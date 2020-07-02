package eu.eurofleets.ears3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


/*@Configuration
 @ComponentScan
 @EnableJpaRepositories
 @Import({RepositoryRestMvcConfiguration.class})
 @org.springframework.boot.autoconfigure.EnableAutoConfiguration
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })//scanBasePackages={"be.naturalsciences.bmdc.dits.webservices.iso"}
//@EnableJpaRepositories(basePackages = {"eu.eurofleets.ears3.service"})
@EnableAutoConfiguration
@EntityScan("eu.eurofleets.ears3.domain")
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }
}
