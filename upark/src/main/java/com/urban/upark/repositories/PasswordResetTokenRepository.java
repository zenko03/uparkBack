package com.urban.upark.repositories;

import com.urban.upark.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    /**
     * Trouve le dernier token valide pour un email donné
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.email = :email AND t.isUsed = false AND t.expirationDate > :now ORDER BY t.createdAt DESC")
    Optional<PasswordResetToken> findValidTokenByEmail(@Param("email") String email, @Param("now") LocalDateTime now);

    /**
     * Trouve un token par email et code
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.email = :email AND t.code = :code AND t.isUsed = false")
    Optional<PasswordResetToken> findByEmailAndCode(@Param("email") String email, @Param("code") String code);

    /**
     * Invalide tous les tokens précédents pour un email
     */
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.isUsed = true WHERE t.email = :email AND t.isUsed = false")
    void invalidateAllTokensForEmail(@Param("email") String email);

    /**
     * Supprime les tokens expirés (pour le nettoyage)
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expirationDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
