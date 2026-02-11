package com.urban.upark.configs;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
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
                .requestMatchers("/api/vehicles/**").permitAll()
                .requestMatchers("/api/v1/announcements/published").permitAll()
                // User notes: GET public (statistiques), POST/DELETE authentifie
                .requestMatchers(HttpMethod.GET, "/api/v1/user-notes/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/user-notes").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/v1/user-notes/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/user-notes/**").authenticated()
                // Dashboard endpoints (admin only - should add role check later)
                .requestMatchers("/api/dashboard/**").permitAll()
                // Disputes endpoints (protected - users can only access their own)
                .requestMatchers("/api/v1/disputes/**").authenticated()
                .requestMatchers("/api/v1/dispute-proofs/**").authenticated()
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
        // Allowed origins (explicit list for credentials support)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8081",           // Dev mobile web
            "http://localhost:19006",          // Expo web dev
            "http://10.0.2.2:8081",            // Android emulator
            "https://upark-ivjilfyve-zeniths-projects-bf3d7e5d.vercel.app",  // Vercel prod
            "https://upark-web-git-v2-zeniths-projects-bf3d7e5d.vercel.app", // Vercel branch alias
            "https://*.vercel.app"             // All Vercel preview deployments
        ));
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
        configuration.setMaxAge(3600L); // Cache CORS config for 1 hour
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
