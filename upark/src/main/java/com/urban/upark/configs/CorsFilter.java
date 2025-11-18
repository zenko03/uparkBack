package com.urban.upark.configs;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    // Liste des origines autorisées (ajoute ou retire selon tes besoins)
    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            // Développement React Native
            "http://localhost:8081",           // iOS & Android (Metro)
            "http://10.0.2.2:8081",          // Android Emulator → localhost PC
            "http://127.0.0.1:8081",
            "http://localhost:3000",          // si tu testes avec Expo Web ou autre

            // Appareils physiques sur le même réseau WiFi
            "http://192.168.1.",              // tous les 192.168.1.x (change si ton réseau est différent)
            "http://192.168.0.",
            "http://192.168.8.",

            // Production (à remplir quand tu auras ton app publiée ou un domaine)
            "https://upark.mg",
            "https://www.upark.mg",
            "capacitor://localhost",          // Capacitor iOS
            "ionic://localhost",              // Ionic
            "http://localhost"                // Expo Go parfois
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");

        boolean originAllowed = isOriginAllowed(origin);

        // Si l'origine est autorisée → on la renvoie exactement (nécessaire pour credentials)
        if (originAllowed) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Vary", "Origin"); // Bonne pratique
        }
        // Sinon on ne met rien → le navigateur bloquera (sécurité)

        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers",
                "Authorization, Content-Type, Accept, X-Requested-With, Cache-Control, X-App-Version");
        response.setHeader("Access-Control-Expose-Headers", "Authorization"); // si tu renvoies le token dans les headers
        response.setHeader("Access-Control-Max-Age", "3600");

        // Réponse immédiate aux requêtes preflight OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return; // on arrête ici, pas besoin d’aller plus loin
        }

        chain.doFilter(request, response);
    }

    private boolean isOriginAllowed(String origin) {
        if (origin == null) {
            return false;
        }

        // Autoriser les origines qui correspondent exactement ou par préfixe
        return ALLOWED_ORIGINS.stream()
                .anyMatch(allowed -> {
                    if (allowed.startsWith("://")) {
                        // cas "://localhost:8081"
                        return origin.endsWith(allowed.substring(1));
                    } else if (allowed.endsWith(".")) {
                        // cas "http://192.168.1."
                        return origin.startsWith(allowed);
                    } else {
                        return origin.equals(allowed);
                    }
                });
    }
}