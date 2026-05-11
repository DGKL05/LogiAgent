package com.logiagent.common.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JwtUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };
    private static final String HMAC_SHA256 = "HmacSHA256";

    private JwtUtils() {
    }

    public static String generateToken(Long userId, String username, List<String> roles, JwtProperties properties) {
        return generateToken(userId, username, roles, properties, Instant.now());
    }

    public static String generateToken(Long userId, String username, List<String> roles, JwtProperties properties, Instant now) {
        long expireSeconds = properties.getExpireSeconds() == null ? 7200L : properties.getExpireSeconds();
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new HashMap<>();
        payload.put("iss", properties.getIssuer());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plusSeconds(expireSeconds).getEpochSecond());
        payload.put("userId", userId);
        payload.put("username", username);
        payload.put("roles", roles);

        String headerPart = encodeJson(header);
        String payloadPart = encodeJson(payload);
        String signingInput = headerPart + "." + payloadPart;
        return signingInput + "." + sign(signingInput, properties.getSecret());
    }

    public static JwtUserContext parseToken(String token, JwtProperties properties) {
        String[] parts = splitToken(token);
        String signingInput = parts[0] + "." + parts[1];
        String expectedSignature = sign(signingInput, properties.getSecret());
        if (!constantTimeEquals(expectedSignature, parts[2])) {
            throw new TokenInvalidException("Token signature is invalid");
        }

        Map<String, Object> payload = decodeJson(parts[1]);
        String issuer = asString(payload.get("iss"));
        if (properties.getIssuer() != null && !properties.getIssuer().equals(issuer)) {
            throw new TokenInvalidException("Token issuer is invalid");
        }

        long expireAtEpoch = asLong(payload.get("exp"));
        if (Instant.now().getEpochSecond() >= expireAtEpoch) {
            throw new TokenExpiredException("Token has expired");
        }

        Long userId = asLong(payload.get("userId"));
        String username = asString(payload.get("username"));
        List<String> roles = asStringList(payload.get("roles"));
        LocalDateTime expireAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(expireAtEpoch), ZoneId.systemDefault());
        return new JwtUserContext(userId, username, roles, expireAt);
    }

    public static boolean validateToken(String token, JwtProperties properties) {
        try {
            parseToken(token, properties);
            return true;
        } catch (TokenAuthException ex) {
            return false;
        }
    }

    public static boolean isExpired(String token, JwtProperties properties) {
        try {
            parseToken(token, properties);
            return false;
        } catch (TokenExpiredException ex) {
            return true;
        }
    }

    public static LocalDateTime getExpireAt(String token, JwtProperties properties) {
        return parseToken(token, properties).getExpireAt();
    }

    private static String encodeJson(Map<String, Object> value) {
        try {
            return base64Url(OBJECT_MAPPER.writeValueAsBytes(value));
        } catch (JsonProcessingException ex) {
            throw new TokenInvalidException("Unable to build token", ex);
        }
    }

    private static Map<String, Object> decodeJson(String value) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(value);
            return OBJECT_MAPPER.readValue(bytes, MAP_TYPE);
        } catch (Exception ex) {
            throw new TokenInvalidException("Token payload is invalid", ex);
        }
    }

    private static String sign(String signingInput, String secret) {
        if (secret == null || secret.isBlank()) {
            throw new TokenInvalidException("JWT secret is blank");
        }
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return base64Url(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new TokenInvalidException("Unable to sign token", ex);
        }
    }

    private static String base64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String[] splitToken(String token) {
        if (token == null || token.isBlank()) {
            throw new TokenInvalidException("Token is blank");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new TokenInvalidException("Token format is invalid");
        }
        return parts;
    }

    private static boolean constantTimeEquals(String left, String right) {
        if (left == null || right == null || left.length() != right.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < left.length(); i++) {
            result |= left.charAt(i) ^ right.charAt(i);
        }
        return result == 0;
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    @SuppressWarnings("unchecked")
    private static List<String> asStringList(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }
}
