package com.urban.upark.services;

import com.urban.upark.models.DeviceToken;
import com.urban.upark.models.Notification;
import com.urban.upark.models.Users;
import com.urban.upark.repositories.DeviceTokenRepository;
import com.urban.upark.repositories.NotificationRepository;
import com.urban.upark.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final UsersRepository usersRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${supabase.url:https://fbpefbjoxzkxombdcqif.supabase.co}")
    private String supabaseUrl;
    
    @Value("${supabase.key:}")
    private String supabaseServiceKey;

    @Transactional
    public void sendbulknotifications(String title, String message, String type, Map<String, String> data) {
        if(usersRepository.findAllActiveUsers().isEmpty()){
            System.out.println("⚠️ Aucun utilisateur actif trouvé pour l'envoi de notifications.");
            return;
        }
        for(Users user : usersRepository.findAllActiveUsers()){
            sendPushNotification(user.getId_Users(), title, message, type, data);
        }
    }
    
    /**
     * Envoyer une notification push à un utilisateur
     */
    @Transactional
    public void sendPushNotification(Integer userId, String title, String message, String type, Map<String, String> data) {
        try {
            System.out.println("📤 Envoi notification à user " + userId + ": " + title);
            
            // 1. Sauvegarder la notification dans la base
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + userId));
            
            Notification notification = Notification.builder()
                    .title(title)
                    .message(message)
                    .type(type)
                    .data(data)
                    .user(user)
                    .build();
            
            notificationRepository.save(notification);
            System.out.println("✅ Notification sauvegardée en BDD");
            
            // 2. Récupérer les tokens FCM actifs de l'utilisateur
            List<DeviceToken> tokens = deviceTokenRepository.findActiveTokensByUserId(userId);
            
            if (tokens.isEmpty()) {
                System.out.println("⚠️ Aucun token FCM trouvé pour user " + userId);
                return;
            }
            
            // 3. Envoyer via Supabase Edge Function pour chaque token
            for (DeviceToken deviceToken : tokens) {
                try {
                    sendViaSupabaseEdgeFunction(deviceToken.getToken(), title, message, data);
                    System.out.println("✅ Notification envoyée au token: " + deviceToken.getToken().substring(0, 20) + "...");
                } catch (Exception e) {
                    System.err.println("❌ Erreur envoi au token " + deviceToken.getToken().substring(0, 20) + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur sendPushNotification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Appeler la Supabase Edge Function pour envoyer via FCM
     */
    private void sendViaSupabaseEdgeFunction(String fcmToken, String title, String body, Map<String, String> data) {
        String edgeFunctionUrl = supabaseUrl + "/functions/v1/send-push-notification";
        
        // Construire le payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("token", fcmToken);
        payload.put("title", title);
        payload.put("body", body);
        if (data != null) {
            payload.put("data", data);
        }
        
        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + supabaseServiceKey);
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        
        // Envoyer la requête
        restTemplate.postForEntity(edgeFunctionUrl, request, String.class);
    }
    
    /**
     * Enregistrer ou mettre à jour un token FCM
     */
    @Transactional
    public DeviceToken registerDeviceToken(Integer userId, String token, String platform) {
        // Vérifier si le token existe déjà
        return deviceTokenRepository.findByUserIdAndToken(userId, token)
                .map(existingToken -> {
                    existingToken.setIsActive(true);
                    existingToken.setUpdatedAt(LocalDateTime.now());
                    return deviceTokenRepository.save(existingToken);
                })
                .orElseGet(() -> {
                    Users user = usersRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
                    
                    DeviceToken newToken = DeviceToken.builder()
                            .token(token)
                            .platform(platform)
                            .user(user)
                            .isActive(true)
                            .build();
                    
                    return deviceTokenRepository.save(newToken);
                });
    }
    
    /**
     * Récupérer les notifications d'un utilisateur
     */
    public List<Notification> getUserNotifications(Integer userId, Integer limit) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderBySentAtDesc(userId);
        if (limit != null && limit > 0 && notifications.size() > limit) {
            return notifications.subList(0, limit);
        }
        return notifications;
    }
    
    /**
     * Marquer une notification comme lue
     */
    @Transactional
    public Notification markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        
        if (!notification.getRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            return notificationRepository.save(notification);
        }
        
        return notification;
    }
    
    /**
     * Marquer toutes les notifications comme lues
     */
    @Transactional
    public int markAllAsRead(Integer userId) {
        return notificationRepository.markAllAsRead(userId, LocalDateTime.now());
    }
    
    /**
     * Compter les notifications non lues
     */
    public Long getUnreadCount(Integer userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    /**
     * Supprimer une notification
     */
    @Transactional
    public void deleteNotification(Integer notificationId) {
        notificationRepository.deleteById(notificationId);
    }
    
    /**
     * Nettoyer les anciennes notifications (> 90 jours)
     */
    @Transactional
    public int cleanupOldNotifications() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
        return notificationRepository.deleteOldNotifications(cutoffDate);
    }
}
