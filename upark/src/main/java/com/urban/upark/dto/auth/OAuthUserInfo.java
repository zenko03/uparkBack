package com.urban.upark.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuthUserInfo {
    private String oauthId;          // ID unique du provider
    private String email;
    private Boolean emailVerified;
    private String name;             // Nom de famille
    private String firstName;        // Prénom
    private String profilePictureUrl;
    private String provider;         // "google" ou "facebook"
}
