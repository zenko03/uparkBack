package com.urban.upark.configs;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mongodb.lang.NonNull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        // Liste des endpoints publics à ignorer par le filtre JWT
        String requestURI = request.getRequestURI();
        
        // Log pour déboguer
        System.out.println("🔍 JWT Filter - URI: " + requestURI);
        
        // Vérifier si c'est un endpoint public (ORDRE IMPORTANT: plus spécifique d'abord)
        if (requestURI.startsWith("/api/v1/auth/") ||
            requestURI.startsWith("/api/auth/") ||
            requestURI.startsWith("/api/parkings") ||  // Tous les endpoints parkings sont publics
            requestURI.startsWith("/api/vehicles") ||
            requestURI.startsWith("/api/reservations/test-public") ||
            requestURI.startsWith("/api/reservations/calculate-price") ||
            requestURI.startsWith("/api/reservations/check-availability")) {
            System.out.println("✅ Endpoint public - pas de vérification JWT");
            filterChain.doFilter(request, response);
            return;
        }
        
        System.out.println("🔐 Endpoint protégé - vérification JWT requise");
        
        final String authHeader=request.getHeader("Authorization");
        final String jwt;
        final String username;
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt=authHeader.substring(7);
        username= jwtService.extractUsername(jwt);
        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails=this.userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    

}