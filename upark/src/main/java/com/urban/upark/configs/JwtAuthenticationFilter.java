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
        // D'abord vérifier les endpoints TOUJOURS PROTÉGÉS
        if (requestURI.contains("/my-parkings") || 
            requestURI.contains("/user/")) {
            System.out.println(" Endpoint protégé - vérification JWT requise");
            // Continue avec la vérification JWT plus bas
        }
        // Ensuite vérifier les endpoints publics
        else if (requestURI.startsWith("/api/v1/auth/") ||
            requestURI.startsWith("/api/auth/") ||
            // Image proxy: toujours public
            requestURI.startsWith("/api/images/") ||
            // Vehicles: tous publics
            requestURI.startsWith("/api/vehicles") ||
            requestURI.startsWith("/api/v1/vehicles") ||
            // Parkings: seuls GET publics (POST/PUT/DELETE protégés)
            (request.getMethod().equals("GET") && 
             (requestURI.startsWith("/api/parkings") || requestURI.startsWith("/api/v1/parkings"))) ||
            // User notes: GET publics (statistiques et avis), POST/DELETE protégés
            (request.getMethod().equals("GET") && 
             (requestURI.startsWith("/api/user-notes") || requestURI.startsWith("/api/v1/user-notes"))) ||
            // Reservations publiques
            requestURI.startsWith("/api/reservations/test-public") ||
            requestURI.startsWith("/api/v1/reservations/test-public") ||
            requestURI.startsWith("/api/reservations/calculate-price") ||
            requestURI.startsWith("/api/v1/reservations/calculate-price") ||
            requestURI.startsWith("/api/reservations/check-availability") ||
            requestURI.startsWith("/api/v1/reservations/check-availability")) {
            System.out.println(" Endpoint public - pas de vérification JWT");
            filterChain.doFilter(request, response);
            return;
        }
        else {
            System.out.println(" Endpoint protégé - vérification JWT requise");
        }
        
        System.out.println(" Endpoint protégé - vérification JWT requise");
        
        final String authHeader=request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Erreur: Pas de header Authorization ou ne commence pas par Bearer");
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt=authHeader.substring(7);
        System.out.println(" Token JWT extrait: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");
        
        try {
            username = jwtService.extractUsername(jwt);
            System.out.println("👤 Username extrait du token: " + username);
        } catch (Exception e) {
            System.err.println("Erreur: Erreur extraction username du token: " + e.getMessage());
            e.printStackTrace();
            filterChain.doFilter(request, response);
            return;
        }
        
        if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails=this.userDetailsService.loadUserByUsername(username);
            System.out.println("👤 UserDetails chargé: " + userDetails.getUsername());
            
            if (jwtService.isTokenValid(jwt, userDetails)) {
                System.out.println(" Token JWT valide - authentification réussie");
                UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                System.err.println("Erreur: Token JWT invalide ou expiré");
            }
        }
        filterChain.doFilter(request, response);
    }

    

}