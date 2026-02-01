package com.urban.upark.controllers;

import com.urban.upark.models.DeviceToken;
import com.urban.upark.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/device-tokens")
@RequiredArgsConstructor
public class DeviceTokenController {
    
    private final NotificationService notificationService;
    
    /**
     * Enregistrer un token FCM
     */
    @PostMapping
    public ResponseEntity<DeviceToken> registerToken(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            String token = (String) request.get("token");
            String platform = (String) request.get("platform");
            
            if (userId == null || token == null || platform == null) {
                return ResponseEntity.badRequest().build();
            }
            
            DeviceToken deviceToken = notificationService.registerDeviceToken(userId, token, platform);
            System.out.println(" Token FCM enregistré pour user " + userId);
            
            return ResponseEntity.ok(deviceToken);
        } catch (Exception e) {
            System.err.println("Erreur: Erreur enregistrement token: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Désactiver un token FCM (lors du logout)
     */
    @DeleteMapping("/deactivate")
    public ResponseEntity<Map<String, String>> deactivateToken(@RequestBody Map<String, Object> request) {
        try {
            String token = (String) request.get("token");
            Integer userId = request.get("userId") != null ? (Integer) request.get("userId") : null;
            
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Token requis"));
            }
            
            notificationService.deactivateDeviceToken(token, userId);
            System.out.println(" Token FCM désactivé" + (userId != null ? " pour user " + userId : ""));
            
            return ResponseEntity.ok(Map.of("status", "success", "message", "Token désactivé"));
        } catch (Exception e) {
            System.err.println("Erreur: Erreur désactivation token: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
