 package eu.eurofleets.ears2;
 
 import org.springframework.boot.builder.SpringApplicationBuilder;
 import org.springframework.boot.context.web.SpringBootServletInitializer;
 
 public class WebInitializer extends SpringBootServletInitializer
 {
   protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
   {
     return application.sources(new Class[] { Application.class });
   }
 }


/* Location:              /home/thomas/Documents/Project-Eurofleets2/meetings/2016-11-03-04-workshop/VM/shared/ef_workshop/ears2.war!/WEB-INF/classes/eu/eurofleets/ears2/WebInitializer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */