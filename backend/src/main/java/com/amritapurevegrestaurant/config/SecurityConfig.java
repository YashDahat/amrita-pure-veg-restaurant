package com.amritapurevegrestaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures HTTP security, permitting access to public APIs and securing others.
     *
     * @param http The HttpSecurity object to configure.
     * @return A SecurityFilterChain bean.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless REST API
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configure CORS
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/**").permitAll() // Permit all requests to /api/v1/**
                .anyRequest().authenticated() // All other requests require authentication (though none are configured yet)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // REST API is stateless
            );
        return http.build();
    }

    /**
     * Defines the CORS configuration for the application.
     * Allows requests from the local development server and Vercel deployments.
     *
     * @return A CorsConfigurationSource bean.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow specific origins. For production, replace "*" with your actual frontend domain(s).
        // For local development, http://localhost:3000 is common for React apps.
        // For Vercel deployment, it would be something like https://your-app-name.vercel.app
        // Using "*.vercel.app" allows all subdomains on Vercel, useful for preview deployments.
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "https://*.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Allow credentials (e.g., cookies, authorization headers)
        configuration.setMaxAge(3600L); // How long the pre-flight request can be cached

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // Apply this CORS configuration to all /api/** paths
        return source;
    }
}