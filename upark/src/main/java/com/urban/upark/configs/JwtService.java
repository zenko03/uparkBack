package com.urban.upark.configs;

import java.security.Key;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET_KEY = "MCECbHH3hg6YJjOvZsZQe8M1Qxk2qNg8E4wsF9Z+GpdnrZPx";

    /**
     * Durée de validité du token en jours (configurable via application.properties)
     * Valeur par défaut : 7 jours
     */
    @Value("${jwt.token.validity.days:7}")
    private int tokenValidityDays;

    public JwtService() {
        super();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userdetails) {
        // Calcul de l'expiration : maintenant + X jours en millisecondes
        long expirationMillis = System.currentTimeMillis() + (tokenValidityDays * 24L * 60L * 60L * 1000L);
        
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userdetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] signingKey = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(signingKey);
    }

    /**
     * Retourne la durée de validité du token en jours
     */
    public int getTokenValidityDays() {
        return tokenValidityDays;
    }
}
