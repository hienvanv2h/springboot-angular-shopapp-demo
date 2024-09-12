package com.example.shopapp.components;

import com.example.shopapp.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(User user) {
        // properties => claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("userId", user.getId());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getPhoneNumber())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000L))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
        return token;
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public  <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Check expiration
    public boolean isTokenExpired(String token) {
        var expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String phoneNumber = extractPhoneNumber(token);
        return phoneNumber.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Generate random 256 bits key
//    private String generateSecretKey() {
//        SecureRandom secureRandom = new SecureRandom();
//        byte[] keyBytes = new byte[32];
//        secureRandom.nextBytes(keyBytes);
//        String secretKey = Encoders.BASE64.encode(keyBytes);
//        return secretKey;
//    }
}
