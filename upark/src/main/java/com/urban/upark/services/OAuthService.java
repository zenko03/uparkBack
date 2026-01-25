package com.urban.upark.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.urban.upark.dto.auth.OAuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class OAuthService {

    @Value("${oauth.google.client-id}")
    private String googleClientId;

    @Value("${oauth.facebook.app-id}")
    private String facebookAppId;

    @Value("${oauth.facebook.app-secret}")
    private String facebookAppSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Vérifie et décode un token Google ID
     * @param idToken Token Google reçu du frontend
     * @return Informations utilisateur extraites du token
     */
    public OAuthUserInfo verifyGoogleToken(String idToken) {
        try {
            System.out.println("🔍 Vérification token Google...");
            System.out.println(" Client ID attendu: " + googleClientId);
            
            // Décoder le token AVANT vérification pour diagnostiquer
            GoogleIdToken unverifiedToken = GoogleIdToken.parse(GsonFactory.getDefaultInstance(), idToken);
            if (unverifiedToken != null) {
                GoogleIdToken.Payload unverifiedPayload = unverifiedToken.getPayload();
                System.out.println("📊 Token aud (audience): " + unverifiedPayload.getAudience());
                System.out.println("📊 Token azp (authorized party): " + unverifiedPayload.get("azp"));
                System.out.println("📊 Token iss (issuer): " + unverifiedPayload.getIssuer());
                
                long exp = unverifiedPayload.getExpirationTimeSeconds();
                long now = System.currentTimeMillis() / 1000;
                System.out.println("⏰ Expiration: " + exp + " | Now: " + now + " | Restant: " + (exp - now) + "s");
                
                if (exp < now) {
                    System.err.println("Erreur: TOKEN EXPIRÉ ! Demandez un nouveau token.");
                    throw new RuntimeException("Token expiré - veuillez vous reconnecter");
                }
            }
            
            // Créer le vérificateur Google avec le Web Client ID configuré
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
            .setAudience(Collections.singletonList(googleClientId))
            .build();

            // Vérifier le token avec validation de signature complète
            GoogleIdToken token = verifier.verify(idToken);
            
            if (token == null) {
                System.err.println("Erreur: verifier.verify() a retourné null");
                System.err.println("Erreur: Raisons possibles: signature invalide, problème réseau Google, certificats");
                throw new RuntimeException("Échec de vérification du token Google");
            }

            System.out.println(" Token vérifié avec succès !");
            
            // Extraire les informations utilisateur du token vérifié
            GoogleIdToken.Payload payload = token.getPayload();
            
            String userId = payload.getSubject();
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");
            String pictureUrl = (String) payload.get("picture");

            System.out.println(" Token vérifié - Utilisateur: " + email);
            
            return OAuthUserInfo.builder()
                    .oauthId(userId)
                    .email(email)
                    .emailVerified(emailVerified)
                    .name(familyName != null ? familyName : name)
                    .firstName(givenName != null ? givenName : "")
                    .profilePictureUrl(pictureUrl)
                    .provider("google")
                    .build();
        } catch (Exception e) {
            System.err.println("Erreur: Erreur vérification token Google: " + e.getMessage());
            throw new RuntimeException("Token Google invalide: " + e.getMessage(), e);
        }
    }

    /**
     * Vérifie un token Facebook access token
     * @param accessToken Token Facebook reçu du frontend
     * @return Informations utilisateur extraites de l'API Facebook
     */
    public OAuthUserInfo verifyFacebookToken(String accessToken) {
        try {
            // 1. Vérifier la validité du token auprès de Facebook
            String debugUrl = String.format(
                "https://graph.facebook.com/debug_token?input_token=%s&access_token=%s|%s",
                accessToken,
                facebookAppId,
                facebookAppSecret
            );

            ResponseEntity<Map> debugResponse = restTemplate.getForEntity(debugUrl, Map.class);
            Map<String, Object> data = (Map<String, Object>) debugResponse.getBody().get("data");
            
            if (data == null || !(Boolean) data.get("is_valid")) {
                throw new RuntimeException("Token Facebook invalide");
            }

            // 2. Récupérer les informations utilisateur
            String userInfoUrl = String.format(
                "https://graph.facebook.com/me?fields=id,name,email,first_name,last_name,picture&access_token=%s",
                accessToken
            );

            ResponseEntity<Map> userResponse = restTemplate.getForEntity(userInfoUrl, Map.class);
            Map<String, Object> user = userResponse.getBody();

            String userId = (String) user.get("id");
            String email = (String) user.get("email");
            String name = (String) user.get("last_name");
            String firstName = (String) user.get("first_name");
            
            // Photo de profil Facebook
            String pictureUrl = null;
            if (user.get("picture") != null) {
                Map<String, Object> picture = (Map<String, Object>) user.get("picture");
                Map<String, Object> pictureData = (Map<String, Object>) picture.get("data");
                pictureUrl = (String) pictureData.get("url");
            }

            return OAuthUserInfo.builder()
                    .oauthId(userId)
                    .email(email)
                    .emailVerified(true) // Facebook vérifie toujours l'email
                    .name(name != null ? name : "")
                    .firstName(firstName != null ? firstName : "")
                    .profilePictureUrl(pictureUrl)
                    .provider("facebook")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la vérification du token Facebook: " + e.getMessage(), e);
        }
    }
}
