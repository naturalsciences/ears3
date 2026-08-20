package eu.eurofleets.ears3;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(
            SecurityProperties props,
            PasswordEncoder encoder) {

        List<UserDetails> users = props.getUsers()
                .stream()
                .map(u ->
                        User.builder()
                                .username(u.getUsername())
                                .password(encoder.encode(u.getPassword()))
                                .roles(u.getRoles().toArray(new String[0]))
                                .build())
                .toList();

        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/event/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/event/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/event/**")
                        .hasAnyRole("ADMIN", "ADMIN_EVENT")

                        .requestMatchers("/ontology")
                        .hasAnyRole("ADMIN", "ADMIN_TREE")
                        .requestMatchers("/api/stage-from-file")
                        .hasAnyRole("ADMIN", "ADMIN_TREE")
                        .requestMatchers("/api/stage-from-db")
                        .hasAnyRole("ADMIN")
                        .requestMatchers("/api/ingest")
                        .hasAnyRole("ADMIN")

                        // Broad catch-alls LAST, and no longer swallowing all of /api/**.
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()

                        .anyRequest().permitAll()
                )

                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/ontology", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}