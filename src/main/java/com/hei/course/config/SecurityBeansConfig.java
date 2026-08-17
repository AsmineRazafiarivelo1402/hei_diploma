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
                auth.requestMatchers(HttpMethod.GET, "/ping")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/users")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/users/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/users/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/users/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/students")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/students/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/students/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/students/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/teachers")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/teachers/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/teachers/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/teachers/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/groups")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/groups/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/groups/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/groups/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/exams")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/exams/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/courses")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.GET, "/courses/**")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.PUT, "/courses/**")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.DELETE, "/courses/**")
                    .hasAnyRole("ADMIN", "TEACHER")
                    .requestMatchers(HttpMethod.POST, "/notes")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/notes/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/notes/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/notes/**")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.POST, "/note-histories")
                    .hasAnyRole("TEACHER", "ADMIN")
                    .requestMatchers(HttpMethod.GET, "/note-histories/**")
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
                    .requestMatchers(HttpMethod.GET, "/promotions/view")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .httpBasic(withDefaults());

    return http.build();
  }
}
