package com.project.auth.config;

import com.project.auth.security.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{

         return httpSecurity
                 .csrf(AbstractHttpConfigurer::disable)  // Disabled for stateless JWT auth
                 .authorizeHttpRequests(auth->auth
                         // Public authentication endpoints (anyone can access)
                         .requestMatchers("/auth/register", "/auth/login", "/auth/token", 
                                         "/auth/refresh", "/auth/logout", 
                                         "/auth/validate", "/auth/user-info",
                                         "/auth/forgot-password").permitAll()
                         .requestMatchers("/actuator/**").permitAll()
                         .requestMatchers("/auth/profile/**").permitAll()
                         .requestMatchers("/auth/change-password/**").permitAll()
                         .requestMatchers("/auth/admin/**").permitAll()
                         .anyRequest().authenticated()
                 )
                 .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(CustomUserDetailService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
