package com.billing.platform.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private String base64UrlEncode(String input) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    private String hmacSha256(String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
    }

    public String generateToken(String username, String role) {
        try {
            String header = base64UrlEncode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");

            long now = System.currentTimeMillis();
            long exp = now + expirationMs;
            String payloadJson = "{"
                    + "\"sub\":\"" + username + "\","
                    + "\"role\":\"" + role + "\","
                    + "\"iat\":" + (now / 1000) + ","
                    + "\"exp\":" + (exp / 1000)
                    + "}";
            String payload = base64UrlEncode(payloadJson);

            String signature = hmacSha256(header + "." + payload);
            return header + "." + payload + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public Map<String, String> extractClaims(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new RuntimeException("Invalid token format");

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8);

            Map<String, String> claims = new HashMap<>();
            payloadJson = payloadJson.replaceAll("[{}\"]", "");
            for (String pair : payloadJson.split(",")) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    claims.put(kv[0].trim(), kv[1].trim());
                }
            }
            return claims;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }

    public String extractUsername(String token) {
        return extractClaims(token).get("sub");
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role");
    }

    public boolean isTokenValid(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String expectedSig = hmacSha256(parts[0] + "." + parts[1]);
            if (!expectedSig.equals(parts[2])) return false;

            Map<String, String> claims = extractClaims(token);
            long exp = Long.parseLong(claims.get("exp"));
            return exp > (System.currentTimeMillis() / 1000);
        } catch (Exception e) {
            return false;
        }
    }
}