package com.predictwin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:changeit}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpirationMs;
    
    // Access token expiry override (ms) - if 0 uses jwtExpirationMs
    @Value("${jwt.access.expiration:0}")
    private long jwtAccessExpirationMs;

    public String generateToken(String username) {
        Date now = new Date();
        long exp = (jwtAccessExpirationMs > 0) ? jwtAccessExpirationMs : jwtExpirationMs;
        Date expiry = new Date(now.getTime() + exp);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
