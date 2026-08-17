package com.hei.course.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                auth
                    // Public
                    .requestMatchers(HttpMethod.GET, "/ping")
                    .permitAll()

                    // Users
                    .requestMatchers(HttpMethod.POST, "/users")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/users/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/users/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/users/**")
                    .hasRole("ADMIN")

                    // Students
                    .requestMatchers(HttpMethod.POST, "/students")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/students/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/students/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/students/**")
                    .hasRole("ADMIN")

                    // Teachers
                    .requestMatchers(HttpMethod.POST, "/teachers")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/teachers/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/teachers/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/teachers/**")
                    .hasRole("ADMIN")

                    // Groups
                    .requestMatchers(HttpMethod.POST, "/groups")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/groups/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/groups/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/groups/**")
                    .hasRole("ADMIN")

                    // Courses
                    .requestMatchers(HttpMethod.POST, "/courses")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/courses/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/courses/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/courses/**")
                    .hasRole("ADMIN")

                    // Exams
                    .requestMatchers(HttpMethod.POST, "/exams")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/exams/**")
                    .hasRole("ADMIN")

                    // Notes
                    .requestMatchers(HttpMethod.POST, "/notes")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/notes/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/notes/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/notes/**")
                    .hasAnyRole("TEACHER", "ADMIN")

                    // Note histories
                    .requestMatchers(HttpMethod.POST, "/note-histories")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/note-histories/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/note-histories/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/note-histories/**")
                    .hasAnyRole("TEACHER", "ADMIN")

                    // Diplomas
                    .requestMatchers(HttpMethod.GET, "/diplomas")
                    .hasRole("ADMIN")

                    // Transcripts
                    .requestMatchers(HttpMethod.GET, "/releves/{studentId}")
                    .hasAnyRole("TEACHER", "ADMIN", "STUDENT")
                    .requestMatchers(HttpMethod.POST, "/releves/{studentId}/email")
                    .hasAnyRole("TEACHER", "ADMIN", "STUDENT")
                    .requestMatchers(HttpMethod.GET, "/releves/me/**")
                    .hasRole("STUDENT")

                    // Promotions
                    .requestMatchers(HttpMethod.POST, "/promotions")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/promotions/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/promotions/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/promotions/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/promotions/view")
                    .authenticated()
                    .requestMatchers(HttpMethod.POST, "/semesters")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/semesters/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/semesters/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/semesters/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/affectations")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/affectations/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/affectations/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/affectations/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/group-exams")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/group-exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/group-exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/group-exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/course-specialities")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/course-specialities/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/course-specialities/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/course-specialities/**")
                    .hasRole("ADMIN")
                    // Everything else
                    .anyRequest()
                    .authenticated())
        .httpBasic(withDefaults());

    return http.build();
  }
}
