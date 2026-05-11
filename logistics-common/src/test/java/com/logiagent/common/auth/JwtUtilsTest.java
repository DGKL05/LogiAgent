package com.logiagent.common.auth;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    private final JwtProperties properties = new JwtProperties("test-secret", 7200L, "logiagent");

    @Test
    void generateAndParseTokenShouldReturnUserClaims() {
        String token = JwtUtils.generateToken(1L, "admin", List.of("ADMIN"), properties);

        JwtUserContext context = JwtUtils.parseToken(token, properties);

        assertEquals(1L, context.getUserId());
        assertEquals("admin", context.getUsername());
        assertEquals(List.of("ADMIN"), context.getRoles());
        assertTrue(JwtUtils.validateToken(token, properties));
        assertFalse(JwtUtils.isExpired(token, properties));
    }

    @Test
    void parseTokenShouldRejectTamperedToken() {
        String token = JwtUtils.generateToken(1L, "admin", List.of("ADMIN"), properties);
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThrows(TokenInvalidException.class, () -> JwtUtils.parseToken(tampered, properties));
    }

    @Test
    void parseTokenShouldRejectExpiredToken() {
        JwtProperties expiredProperties = new JwtProperties("test-secret", -1L, "logiagent");
        String token = JwtUtils.generateToken(1L, "admin", List.of("ADMIN"), expiredProperties, Instant.now());

        assertThrows(TokenExpiredException.class, () -> JwtUtils.parseToken(token, properties));
    }
}
