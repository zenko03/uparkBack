package com.urban.upark.configs;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // OPTIONS requests must be first for CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Image proxy endpoint (public)
                .requestMatchers("/api/images/**").permitAll()
                // Public endpoints (most specific first)
                .requestMatchers("/api/v1/reservations/test-public").permitAll()
                .requestMatchers("/api/v1/reservations/calculate-price").permitAll()
                .requestMatchers("/api/v1/reservations/check-availability").permitAll()
                // Authentication endpoints (public)
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/auth/oauth/**").permitAll()
                // Protected user-specific endpoints (MUST be before generic patterns)
                .requestMatchers("/api/v1/parkings/my-parkings").authenticated()
                .requestMatchers("/api/v1/parkings/user/**").authenticated()
                // Owner parking endpoints (create, update, delete require auth)
                .requestMatchers(HttpMethod.POST, "/api/v1/parkings").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/parkings/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/parkings/**").authenticated()
                // Public parking endpoints (recherche pour clients)
                .requestMatchers(HttpMethod.GET, "/api/v1/parkings").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/parkings/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/parkings/{id}/vehicles").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/parkings/search/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/parkings/{id}/availability").permitAll()
                // Public endpoints
                .requestMatchers("/api/v1/vehicles/**").permitAll()
                .requestMatchers("/api/v1/announcements/published").permitAll()
                // User notes: GET public (statistiques), POST/DELETE authentifie
                .requestMatchers(HttpMethod.GET, "/api/v1/user-notes/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/user-notes").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/user-notes/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/user-notes/**").authenticated()
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow all origins for development
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "Cache-Control",
            "X-App-Version"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache la configuration CORS pendant 1 heure
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
