package com.urban.upark.repositories;

import com.urban.upark.models.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Integer> {
    
    /**
     * Récupérer tous les tokens actifs d'un utilisateur
     */
    @Query("SELECT dt FROM DeviceToken dt WHERE dt.user.Id_Users = :userId AND dt.isActive = true")
    List<DeviceToken> findActiveTokensByUserId(@Param("userId") Integer userId);
    
    /**
     * Récupérer un token spécifique d'un utilisateur
     */
    @Query("SELECT dt FROM DeviceToken dt WHERE dt.user.Id_Users = :userId AND dt.token = :token")
    Optional<DeviceToken> findByUserIdAndToken(@Param("userId") Integer userId, @Param("token") String token);
    
    /**
     * Désactiver tous les tokens d'un utilisateur sauf un
     */
    @Query("UPDATE DeviceToken dt SET dt.isActive = false WHERE dt.user.Id_Users = :userId AND dt.token != :token")
    void deactivateOtherTokens(@Param("userId") Integer userId, @Param("token") String token);
    
    /**
     * Récupérer tous les tokens actifs par plateforme
     */
    @Query("SELECT dt FROM DeviceToken dt WHERE dt.platform = :platform AND dt.isActive = true")
    List<DeviceToken> findActiveTokensByPlatform(@Param("platform") String platform);
    
    /**
     * Récupérer un token par sa valeur
     */
    @Query("SELECT dt FROM DeviceToken dt WHERE dt.token = :token")
    Optional<DeviceToken> findByToken(@Param("token") String token);
    
    /**
     * Désactiver un token pour TOUS les autres utilisateurs (sauf userId)
     * Utilisé lors de la connexion pour éviter les doubles notifications
     */
    @Modifying
    @Query("UPDATE DeviceToken dt SET dt.isActive = false WHERE dt.token = :token AND dt.user.Id_Users != :userId")
    void deactivateTokenForOtherUsers(@Param("token") String token, @Param("userId") Integer userId);
}
