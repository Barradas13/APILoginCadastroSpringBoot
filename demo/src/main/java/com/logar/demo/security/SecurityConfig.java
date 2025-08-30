package com.logar.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // desabilita CSRF
            .authorizeHttpRequests()
            .requestMatchers("/api/influencers/login", "/api/influencers").permitAll() // login e cadastro públicos
            .anyRequest().authenticated() // todo o resto exige token
            .and()
            .httpBasic().disable() // ⚠️ desabilita autenticação Basic
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS); // sem sessão, usa JWT

        return http.build();
    }


    // Bean necessário se você precisar injetar AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}