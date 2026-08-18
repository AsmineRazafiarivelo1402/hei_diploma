package com.hei.course.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityBeansConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, "/ping", "/morning", "/courses/bonjour")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/notes/**", "/note-histories/**")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/notes", "/note-histories")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/notes/**", "/note-histories/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/notes/**", "/note-histories/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/releves/**")
                    .hasAnyRole("STUDENT", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/users/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/users")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/users/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/users/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/students/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/students")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/students/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/students/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/teachers/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/teachers")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/teachers/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/teachers/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/groups/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/groups")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/groups/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/groups/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/courses/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/courses")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/courses/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/courses/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/exams")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/promotions/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/promotions")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/promotions/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/promotions/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/semesters/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/semesters")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/semesters/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/semesters/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/affectations/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/affectations")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/affectations/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/affectations/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/group-exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/group-exams")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/group-exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/group-exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/course-specialities/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/course-specialities")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/course-specialities/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/course-specialities/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/teachings/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/teachings")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/teachings/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/teachings/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/diplomas")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .hasRole("ADMIN"))
        .httpBasic(withDefaults());

    return http.build();
  }
}
