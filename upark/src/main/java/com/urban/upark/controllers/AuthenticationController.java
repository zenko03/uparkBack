package com.urban.upark.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.urban.upark.configs.AuthenticationService;
import com.urban.upark.dto.auth.AuthenticationRequest;
import com.urban.upark.dto.auth.AuthenticationResponse;
import com.urban.upark.dto.auth.RegisterRequest;
import com.urban.upark.dto.auth.OAuthLoginRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
        @RequestBody RegisterRequest request ){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> login(
        @RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/hash-password")
    public ResponseEntity<String> hashPassword(@RequestBody String password) {
        String hashedPassword = passwordEncoder.encode(password);
        return ResponseEntity.ok(hashedPassword);
    }

    /**
     * Authentification OAuth2 avec Google
     * @param request Contient le token Google
     * @return JWT et informations utilisateur
     */
    @PostMapping("/oauth/google")
    public ResponseEntity<AuthenticationResponse> loginWithGoogle(
        @RequestBody OAuthLoginRequest request) {
        try {
            System.out.println("🔵 Tentative de connexion Google OAuth");
            AuthenticationResponse response = service.loginWithGoogle(request.getToken());
            System.out.println("✅ Connexion Google réussie pour: " + response.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Erreur connexion Google: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Authentification OAuth2 avec Facebook
     * @param request Contient le token Facebook
     * @return JWT et informations utilisateur
     */
    @PostMapping("/oauth/facebook")
    public ResponseEntity<AuthenticationResponse> loginWithFacebook(
        @RequestBody OAuthLoginRequest request) {
        try {
            System.out.println("🔵 Tentative de connexion Facebook OAuth");
            AuthenticationResponse response = service.loginWithFacebook(request.getToken());
            System.out.println("✅ Connexion Facebook réussie pour: " + response.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Erreur connexion Facebook: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}
