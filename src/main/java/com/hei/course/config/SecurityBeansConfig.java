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

                                        .requestMatchers(HttpMethod.GET, "/diplomas")
                                        .hasRole("ADMIN")


                                        .requestMatchers(HttpMethod.POST, "/notes")
                                        .hasAnyRole("TEACHER", "ADMIN")
                                        .requestMatchers(HttpMethod.PUT, "/notes/**")
                                        .hasAnyRole("TEACHER", "ADMIN")


                                        .requestMatchers(HttpMethod.GET, "/notes/**")
                                        .hasAnyRole("TEACHER", "ADMIN")

                                        .requestMatchers(HttpMethod.GET, "/releves/{studentId}")
                                        .hasAnyRole("TEACHER", "ADMIN", "STUDENT")


                                        .requestMatchers(HttpMethod.GET, "/releves/me/**")
                                        .hasRole("STUDENT")

                                        .anyRequest()
                                        .authenticated())
                .httpBasic(withDefaults());
        return http.build();
    }
}

