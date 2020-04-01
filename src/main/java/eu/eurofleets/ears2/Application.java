 package eu.eurofleets.ears2;
 
 import org.springframework.boot.SpringApplication;
 import org.springframework.context.annotation.ComponentScan;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.context.annotation.Import;
 import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
 import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
 
 @Configuration
 @ComponentScan
 @EnableJpaRepositories
 @Import({RepositoryRestMvcConfiguration.class})
 @org.springframework.boot.autoconfigure.EnableAutoConfiguration
 public class Application
 {
   public static void main(String[] args)
   {
     SpringApplication.run(Application.class, args);
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/Application.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */