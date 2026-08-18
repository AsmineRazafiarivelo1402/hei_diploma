package com.hei.course.config;

import static org.springframework.security.config.Customizer.withDefaults;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityBeansConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, authException) -> {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response
          .getWriter()
          .write(
              "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Authentication"
                  + " required\"}");
    };
  }

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, AuthenticationEntryPoint authenticationEntryPoint) throws Exception {

    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, "/ping", "/morning", "/courses/bonjour")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/users")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/users/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/users/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/students")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/students/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/students/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/teachers")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/teachers/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/teachers/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/groups")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/groups/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/groups/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/courses")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/courses/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/courses/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/exams")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/notes")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/notes/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/notes/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/note-histories")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/note-histories/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/note-histories/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/diplomas")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/releves/{studentId}")
                    .hasAnyRole("TEACHER", "ADMIN", "STUDENT")
                    .requestMatchers(HttpMethod.POST, "/releves/{studentId}/email")
                    .hasAnyRole("TEACHER", "ADMIN", "STUDENT")
                    .requestMatchers(HttpMethod.GET, "/releves/me/**")
                    .hasRole("STUDENT")
                    .requestMatchers(HttpMethod.POST, "/promotions")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/promotions/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/promotions/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/semesters")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/semesters/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/semesters/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/affectations")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/affectations/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/affectations/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/group-exams")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/group-exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/group-exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/course-specialities")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/course-specialities/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/course-specialities/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated())
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
        .httpBasic(withDefaults());

    return http.build();
  }
}
