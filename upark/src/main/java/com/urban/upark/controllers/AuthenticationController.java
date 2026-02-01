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
import com.urban.upark.dto.auth.ForgotPasswordRequest;
import com.urban.upark.dto.auth.VerifyResetCodeRequest;
import com.urban.upark.dto.auth.ResetPasswordRequest;
import com.urban.upark.services.PasswordResetService;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;

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

    //google
    @PostMapping("/oauth/google")
    public ResponseEntity<AuthenticationResponse> loginWithGoogle(
        @RequestBody OAuthLoginRequest request) {
        try {
            System.out.println(" Tentative de connexion Google OAuth");
            AuthenticationResponse response = service.loginWithGoogle(request.getToken());
            System.out.println(" Connexion Google réussie pour: " + response.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erreur: Erreur connexion Google: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

   //fb
    @PostMapping("/oauth/facebook")
    public ResponseEntity<AuthenticationResponse> loginWithFacebook(
        @RequestBody OAuthLoginRequest request) {
        try {
            System.out.println(" Tentative de connexion Facebook OAuth");
            AuthenticationResponse response = service.loginWithFacebook(request.getToken());
            System.out.println(" Connexion Facebook réussie pour: " + response.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Erreur: Erreur connexion Facebook: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

   
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(
        @RequestBody ForgotPasswordRequest request) {
        try {
            System.out.println(" Demande de reset password pour: " + request.getEmail());
            boolean sent = passwordResetService.requestPasswordReset(request.getEmail());
            
            // Toujours retourner succès pour ne pas révéler si l'email existe
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Si cette adresse email est associée à un compte, vous recevrez un code de vérification."
            ));
        } catch (Exception e) {
            System.err.println(" Erreur forgot-password: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Une erreur est survenue lors de l'envoi de l'email."
            ));
        }
    }

   
    @PostMapping("/verify-reset-code")
    public ResponseEntity<Map<String, Object>> verifyResetCode(
        @RequestBody VerifyResetCodeRequest request) {
        try {
            System.out.println(" Vérification du code pour: " + request.getEmail());
            boolean valid = passwordResetService.verifyCode(request.getEmail(), request.getCode());
            
            if (valid) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Code vérifié avec succès."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Code invalide ou expiré."
                ));
            }
        } catch (Exception e) {
            System.err.println(" Erreur verify-reset-code: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Une erreur est survenue lors de la vérification."
            ));
        }
    }

    
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
        @RequestBody ResetPasswordRequest request) {
        try {
            System.out.println(" Réinitialisation du mot de passe pour: " + request.getEmail());
            boolean success = passwordResetService.resetPassword(
                request.getEmail(), 
                request.getCode(), 
                request.getNewPassword()
            );
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Mot de passe réinitialisé avec succès."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Code invalide ou expiré."
                ));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println(" Erreur reset-password: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Une erreur est survenue lors de la réinitialisation."
            ));
        }
    }
}
