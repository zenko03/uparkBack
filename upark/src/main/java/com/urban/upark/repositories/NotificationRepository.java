package com.urban.upark.repositories;

import com.urban.upark.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    /**
     * Récupérer toutes les notifications d'un utilisateur (triées par date)
     */
    @Query("SELECT n FROM Notification n WHERE n.user.Id_Users = :userId ORDER BY n.sentAt DESC")
    List<Notification> findByUserIdOrderBySentAtDesc(@Param("userId") Integer userId);
    
    /**
     * Récupérer les notifications non lues d'un utilisateur
     */
    @Query("SELECT n FROM Notification n WHERE n.user.Id_Users = :userId AND n.read = false ORDER BY n.sentAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Integer userId);
    
    /**
     * Compter les notifications non lues d'un utilisateur
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.Id_Users = :userId AND n.read = false")
    Long countUnreadByUserId(@Param("userId") Integer userId);
    
    /**
     * Marquer toutes les notifications comme lues
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read = true, n.readAt = :readAt WHERE n.user.Id_Users = :userId AND n.read = false")
    int markAllAsRead(@Param("userId") Integer userId, @Param("readAt") LocalDateTime readAt);
    
    /**
     * Supprimer les notifications anciennes (> 90 jours)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.sentAt < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Récupérer les notifications par type
     */
    @Query("SELECT n FROM Notification n WHERE n.user.Id_Users = :userId AND n.type = :type ORDER BY n.sentAt DESC")
    List<Notification> findByUserIdAndType(@Param("userId") Integer userId, @Param("type") String type);
}
