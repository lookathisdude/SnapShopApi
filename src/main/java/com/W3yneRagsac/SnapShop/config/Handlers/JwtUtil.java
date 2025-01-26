package com.W3yneRagsac.SnapShop.config.Handlers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    // secret key
    private static final String SECRET_KEY = "c25b521086d8abac781d99d066b93994b7ba804a581807716d6910a02a78fb9391207e95447108bb9de40b3cecfed5b587d54c537060415b6b28b041731c7edeee5b48166469bbd7acad50cfd55ff8196e036fcbee3f2ddb44e41e77bb3ea911ff533aa00fa1377b01de76eb56b4bce93c9b575c25a45ec079742333fdacf92a6a1fdb23d4c0e9e7eb002524111bb64178abfd7ee5f2e4cd705644f2904aa97f6632ec4c7d055044290824d358652b5fbd022793a0d7c5ee4ab33ac136e063ec607a0b0d4a31b4986345327e82823af2961616622b95e086266a7d3bda7d4607423fed7fa12f76eda8c0ca6e9dc82e56b6876a2b094b83f540ff7e97bf98cc38";
    private static final long TOKEN_VALIDITY = 3600; // 1 hour in seconds
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(String username) {
        // Create claims with username and role
        return Jwts.builder()
                .subject(username)  // Set the subject directly (username)
                .claim("role", role)  // Set custom claim for role
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

    }


    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                // build the claims, subject, issued at, expiration, sign in and then compact it
                .claims(claims)
                .subject(username)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(TOKEN_VALIDITY)))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }
    // Extracting functions
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims
    private Claims extractAllClaims(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }
    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
    // Validate token
    public boolean validatedToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Extract username
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }
}