package org.uvhnael.chatserver.Utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class JwtUtility {

    private SecretKey key = Jwts.SIG.HS256.key().build();

    public String generateToken(String id, String username, String email) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(id, username, email, claims);
    }

    private String createToken(String id, String username, String email, Map<String, Object> claims) {
        claims.put("id", id); // Lưu ID với khóa "id"
        claims.put("username", username);
        claims.put("email", email);

        return Jwts.builder()
                .header()
                .keyId(id)
                .and()
                .subject(username)
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        String username = null;
        try {
            username = Objects.requireNonNull(parseToken(token)).getPayload().getSubject();
        } catch (Exception ignored) {
        }
        return username;
    }

    public String extractId(String token) {
        String id = null;
        try {
            Claims claims = Objects.requireNonNull(parseToken(token)).getPayload();
            id = claims.get("id", String.class); // Lấy ID từ claims
            System.out.println("Extracted ID: " + id); // Ghi log để kiểm tra
        } catch (Exception e) {
            System.out.println("Error extracting ID: " + e.getMessage()); // Ghi log lỗi
        }
        return id;
    }

    public Boolean validateToken(String token) {
        return parseToken(token) != null;
    }

    private Jws<Claims> parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            System.out.println("Token parsing error: " + e.getMessage()); // Ghi log lỗi
            return null;
        }
    }
}
