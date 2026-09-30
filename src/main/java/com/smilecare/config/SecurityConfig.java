package com.smilecare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

                .authorizeHttpRequests(
                        authorize -> authorize

                                .requestMatchers(
                                        "/login",
                                        "/css/**",
                                        "/js/**",
                                        "/images/**",
                                        "/error",
                                        "/access-denied"
                                )
                                .permitAll()

                                .anyRequest()
                                .authenticated()
                )


                // =================================================
                // LOGIN
                // =================================================

                .formLogin(
                        login -> login

                                .loginPage(
                                        "/login"
                                )

                                .loginProcessingUrl(
                                        "/login"
                                )

                                .defaultSuccessUrl(
                                        "/",
                                        true
                                )

                                .failureUrl(
                                        "/login?error"
                                )

                                .permitAll()
                )


                // =================================================
                // LOGOUT
                // =================================================

                .logout(
                        logout -> logout

                                .logoutUrl(
                                        "/logout"
                                )

                                .logoutSuccessUrl(
                                        "/login?logout"
                                )

                                .invalidateHttpSession(
                                        true
                                )

                                .clearAuthentication(
                                        true
                                )

                                .deleteCookies(
                                        "JSESSIONID"
                                )

                                .permitAll()
                )


                // =================================================
                // ACCESS DENIED
                // =================================================

                .exceptionHandling(
                        exception -> exception

                                .accessDeniedPage(
                                        "/access-denied"
                                )
                );


        return http.build();
    }
}