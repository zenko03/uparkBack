package com.urban.upark.configs;

import com.urban.upark.models.*;
import com.urban.upark.repositories.UsersRepository;
import com.urban.upark.dto.auth.RegisterRequest;
import com.urban.upark.dto.auth.AuthenticationRequest;
import com.urban.upark.dto.auth.AuthenticationResponse;
import com.urban.upark.dto.auth.OAuthUserInfo;
import com.urban.upark.services.OAuthService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OAuthService oAuthService;

    // @Bean
    public AuthenticationResponse register(RegisterRequest request) {
        var user= Users.builder()
                .name(request.getName())
                .first_name(request.getFirst_name())
                .userName(request.getUser_name())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone_number(request.getPhone_number())
                .role(Role.valueOf(request.getRole()))
                .build();
        repository.save(user);
        var jwtToken =jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId_Users())
                .userName(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    // @Bean
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUser_name(),
                        request.getPassword()
                )
        );
        var user=repository.findByUserName(request.getUser_name())
                .orElseThrow();
        var jwtToken =jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId_Users())
                .userName(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    /**
     * Authentification OAuth2 avec Google
     * @param idToken Token ID Google reçu du frontend
     * @return JWT et informations utilisateur
     */
    public AuthenticationResponse loginWithGoogle(String idToken) {
        // 1. Vérifier le token auprès de Google
        OAuthUserInfo oauthInfo = oAuthService.verifyGoogleToken(idToken);
        
        // 2. Chercher ou créer l'utilisateur
        Users user = findOrCreateOAuthUser(oauthInfo);
        
        // 3. Générer JWT
        String jwtToken = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId_Users())
                .userName(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    /**
     * Authentification OAuth2 avec Facebook
     * @param accessToken Access token Facebook reçu du frontend
     * @return JWT et informations utilisateur
     */
    public AuthenticationResponse loginWithFacebook(String accessToken) {
        // 1. Vérifier le token auprès de Facebook
        OAuthUserInfo oauthInfo = oAuthService.verifyFacebookToken(accessToken);
        
        // 2. Chercher ou créer l'utilisateur
        Users user = findOrCreateOAuthUser(oauthInfo);
        
        // 3. Générer JWT
        String jwtToken = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getId_Users())
                .userName(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    /**
     * Trouve un utilisateur OAuth ou le crée s'il n'existe pas
     * @param oauthInfo Informations extraites du provider OAuth
     * @return Utilisateur trouvé ou créé
     */
    private Users findOrCreateOAuthUser(OAuthUserInfo oauthInfo) {
        // 1. Chercher par oauth_provider + oauth_id
        Optional<Users> existingOAuthUser = repository.findByOauthProviderAndOauthId(
            oauthInfo.getProvider(), 
            oauthInfo.getOauthId()
        );
        
        if (existingOAuthUser.isPresent()) {
            // Utilisateur OAuth existant trouvé
            Users user = existingOAuthUser.get();
            
            // Mettre à jour la photo de profil si elle a changé
            if (oauthInfo.getProfilePictureUrl() != null) {
                user.setProfilePictureUrl(oauthInfo.getProfilePictureUrl());
                repository.save(user);
            }
            
            return user;
        }
        
        // 2. Chercher par email (cas où user existe avec email classique)
        Optional<Users> existingEmailUser = repository.findByEmail(oauthInfo.getEmail());
        
        if (existingEmailUser.isPresent()) {
            // Email existe déjà avec un compte classique
            // On lie le compte OAuth au compte existant
            Users user = existingEmailUser.get();
            user.setOauthProvider(oauthInfo.getProvider());
            user.setOauthId(oauthInfo.getOauthId());
            user.setEmailVerified(true);
            
            if (oauthInfo.getProfilePictureUrl() != null) {
                user.setProfilePictureUrl(oauthInfo.getProfilePictureUrl());
            }
            
            return repository.save(user);
        }
        
        // 3. Créer un nouvel utilisateur OAuth
        String username = generateUniqueUsername(oauthInfo.getEmail());
        
        Users newUser = Users.builder()
                .name(oauthInfo.getName() != null ? oauthInfo.getName() : "")
                .first_name(oauthInfo.getFirstName() != null ? oauthInfo.getFirstName() : "")
                .userName(username)
                .email(oauthInfo.getEmail())
                .password(null) // Pas de mot de passe pour OAuth
                .phone_number("") // À compléter plus tard
                .role(Role.USER) // Rôle par défaut
                .oauthProvider(oauthInfo.getProvider())
                .oauthId(oauthInfo.getOauthId())
                .profilePictureUrl(oauthInfo.getProfilePictureUrl())
                .emailVerified(oauthInfo.getEmailVerified())
                .build();
        
        return repository.save(newUser);
    }

    /**
     * Génère un nom d'utilisateur unique à partir de l'email
     * @param email Email de l'utilisateur
     * @return Nom d'utilisateur unique
     */
    private String generateUniqueUsername(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;
        
        // Vérifier si le username existe déjà
        while (repository.findByUserName(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }
        
        return username;
    }

}
