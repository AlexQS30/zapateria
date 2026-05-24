package com.back.zapateria.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.back.zapateria.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private static final String SECRET = "YXBwLWJhc2U2NC1zZWNyZXQtZm9yLXphcGF0ZXJpYS0yMDI2LXNlcnZlcg==";

    @Test
    void generateToken_and_extractClaims_successfully() {
        JwtService service = new JwtService(SECRET, 3_600_000L);

        User user = new User();
        user.setId(10L);
        user.setEmail("user@demo.com");
        user.setRole("ADMIN");
        user.setFirstName("Ana");
        user.setLastName("Perez");

        String token = service.generateToken(user);
        Claims claims = service.extractClaims(token);

        assertEquals("user@demo.com", service.extractEmail(token));
        assertEquals(10L, service.extractUserId(token));
        assertEquals("ADMIN", service.extractRole(token));
        assertTrue(service.isTokenValid(token));
        assertEquals("Ana", claims.get("firstName", String.class));
        assertEquals("Perez", claims.get("lastName", String.class));
    }

    @Test
    void extractUserId_handlesStringUidClaim() {
        JwtService service = new JwtService(SECRET, 3_600_000L);
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", "42");
        claims.put("role", "USER");

        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("u@demo.com")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 60_000L))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertEquals(42L, service.extractUserId(token));
    }

    @Test
    void extractRole_returnsNull_whenRoleIsMissing() {
        JwtService service = new JwtService(SECRET, 3_600_000L);
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));

        String token = Jwts.builder()
                .setSubject("u@demo.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000L))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertNull(service.extractRole(token));
    }

    @Test
    void isTokenValid_returnsFalse_whenTokenExpired() {
        JwtService service = new JwtService(SECRET, 3_600_000L);
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));

        Date now = new Date();
        String expiredToken = Jwts.builder()
                .setSubject("u@demo.com")
                .setIssuedAt(new Date(now.getTime() - 10_000L))
                .setExpiration(new Date(now.getTime() - 1_000L))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        ExpiredJwtException exception = assertThrows(ExpiredJwtException.class, () -> service.isTokenValid(expiredToken));
        assertTrue(exception.getMessage().contains("JWT expired"));
    }
}
