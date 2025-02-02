package com.W3yneRagsac.SnapShop.service.classes;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtilService {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtilService.class);
    private final String SECRET_KEY;

    public JwtUtilService() {
        Dotenv dotenv = Dotenv.load();
        this.SECRET_KEY = dotenv.get("JWT_SECRET_KEY");

        if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
            logger.warn("⚠️ JWT_SECRET_KEY is not set or is empty!");
        } else {
            logger.info("✅ Loaded JWT_SECRET_KEY (first 10 chars): {}", SECRET_KEY.substring(0, 10));
        }
    }

    public String generateToken(String email, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles.stream().map(role -> "ROLE_" + role).collect(Collectors.toList())); // Prefix roles with ROLE_

        String token = Jwts.builder()
                .claims(claims) // Use setClaims instead of claims()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30 * 1000)) // 30 minutes
                .signWith(getSigningKey()) // Ensure the algorithm is consistent
                .compact();

        logger.info("JWT Token generated for user: {}", email);
        return token;
    }

    private SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.error("Error generating signing key: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT Secret Key", e);
        }
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        List<String> roles = claims.get("roles", List.class); // Ensure this matches the key used in generateToken
        logger.info("Extracted roles from token: {}", roles);
        return roles;
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token has expired: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error parsing JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT Token", e);
        }
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        if (expired) {
            logger.warn("JWT token is expired.");
        }
        return expired;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            boolean isValid = email.equals(userDetails.getUsername()) && !isTokenExpired(token);
            logger.info("Token validation for {}: {}", email, isValid ? "VALID" : "INVALID");
            return isValid;
        } catch (Exception e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public List<String> extractRolesList(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class); // Ensure this matches the key used in generateToken
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
