package com.urban.upark.services;

import com.urban.upark.models.PasswordResetToken;
import com.urban.upark.models.Users;
import com.urban.upark.repositories.PasswordResetTokenRepository;
import com.urban.upark.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service de gestion de la réinitialisation de mot de passe
 */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UsersRepository usersRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final int CODE_LENGTH = 6;
    private static final int TOKEN_VALIDITY_MINUTES = 15;

    /**
     * Génère et envoie un code de réinitialisation
     * @param email Email de l'utilisateur
     * @return true si l'email a été envoyé, false si l'email n'existe pas
     */
    @Transactional
    public boolean requestPasswordReset(String email) {
        // Vérifier si l'email existe
        Optional<Users> userOpt = usersRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            System.out.println("⚠️ Tentative de reset pour email inexistant: " + email);
            // Pour des raisons de sécurité, on retourne true même si l'email n'existe pas
            // Cela évite de révéler si un email est enregistré ou non
            return true;
        }

        Users user = userOpt.get();

        // Vérifier si l'utilisateur OAuth (pas de mot de passe)
        if (user.getOauthProvider() != null && user.getPassword() == null) {
            System.out.println("⚠️ Utilisateur OAuth, pas de mot de passe à réinitialiser: " + email);
            return true; // Même réponse pour éviter la fuite d'information
        }

        // Invalider les anciens tokens
        tokenRepository.invalidateAllTokensForEmail(email);

        // Générer un nouveau code
        String code = generateCode();

        // Créer le token
        PasswordResetToken token = PasswordResetToken.builder()
                .email(email)
                .code(code)
                .expirationDate(LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES))
                .isUsed(false)
                .createdAt(LocalDateTime.now())
                .build();

        tokenRepository.save(token);

        // Envoyer l'email
        try {
            emailService.sendPasswordResetCode(email, code);
            System.out.println("✅ Code de réinitialisation envoyé à: " + email);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email");
        }
    }

    /**
     * Vérifie si le code est valide
     * @param email Email de l'utilisateur
     * @param code Code à vérifier
     * @return true si le code est valide
     */
    public boolean verifyCode(String email, String code) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByEmailAndCode(email, code);

        if (tokenOpt.isEmpty()) {
            System.out.println("⚠️ Code non trouvé pour: " + email);
            return false;
        }

        PasswordResetToken token = tokenOpt.get();

        if (!token.isValid()) {
            System.out.println("⚠️ Code expiré ou déjà utilisé pour: " + email);
            return false;
        }

        System.out.println("✅ Code vérifié avec succès pour: " + email);
        return true;
    }

    /**
     * Réinitialise le mot de passe
     * @param email Email de l'utilisateur
     * @param code Code de vérification
     * @param newPassword Nouveau mot de passe
     * @return true si le mot de passe a été changé
     */
    @Transactional
    public boolean resetPassword(String email, String code, String newPassword) {
        // Vérifier le code
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByEmailAndCode(email, code);

        if (tokenOpt.isEmpty()) {
            System.out.println("❌ Token non trouvé pour reset: " + email);
            return false;
        }

        PasswordResetToken token = tokenOpt.get();

        if (!token.isValid()) {
            System.out.println("❌ Token invalide pour reset: " + email);
            return false;
        }

        // Trouver l'utilisateur
        Optional<Users> userOpt = usersRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            System.out.println("❌ Utilisateur non trouvé pour reset: " + email);
            return false;
        }

        Users user = userOpt.get();

        // Validation du mot de passe (minimum 6 caractères)
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
        }

        // Changer le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepository.save(user);

        // Marquer le token comme utilisé
        token.setUsed(true);
        tokenRepository.save(token);

        // Invalider tous les autres tokens pour cet email
        tokenRepository.invalidateAllTokensForEmail(email);

        System.out.println("✅ Mot de passe réinitialisé pour: " + email);
        return true;
    }

    /**
     * Génère un code numérique aléatoire
     */
    private String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        
        return code.toString();
    }

    /**
     * Nettoie les tokens expirés (à appeler périodiquement)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        System.out.println("🧹 Tokens expirés supprimés");
    }
}
