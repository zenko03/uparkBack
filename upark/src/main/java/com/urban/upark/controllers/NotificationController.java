package com.urban.upark.controllers;

import com.urban.upark.models.DeviceToken;
import com.urban.upark.models.Notification;
import com.urban.upark.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    
    @PostMapping("/test-push")
    public ResponseEntity<Map<String, String>> testPushNotification(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = (Integer) request.get("userId");
            String title = (String) request.getOrDefault("title", "🔔 Test Notification");
            String message = (String) request.getOrDefault("message", "Ceci est une notification de test!");
            
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "userId requis"));
            }
            
            System.out.println("📤 TEST: Envoi notification à user " + userId);
            
            Map<String, String> data = new HashMap<>();
            data.put("type", "system");
            data.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            notificationService.sendPushNotification(userId, title, message, "system", data);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Notification envoyée à l'utilisateur " + userId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Erreur test push: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Récupérer les notifications d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(
            @PathVariable Integer userId,
            @RequestParam(required = false) Integer limit) {
        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId, limit);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            System.err.println("❌ Erreur récupération notifications: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Compter les notifications non lues
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Integer userId) {
        try {
            Long count = notificationService.getUnreadCount(userId);
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Marquer une notification comme lue
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Integer notificationId) {
        try {
            Notification notification = notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Marquer toutes les notifications comme lues
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(@PathVariable Integer userId) {
        try {
            int updated = notificationService.markAllAsRead(userId);
            Map<String, Integer> response = new HashMap<>();
            response.put("updated", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Supprimer une notification
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Integer notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
