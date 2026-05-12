package com.logiagent.gateway.filter;

import com.logiagent.common.auth.AuthConstant;
import com.logiagent.common.auth.JwtProperties;
import com.logiagent.common.auth.JwtUtils;
import com.logiagent.gateway.config.AuthProperties;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuthGlobalFilterTest {

    private final JwtProperties jwtProperties = new JwtProperties("test-secret", 7200L, "logiagent");
    private final AuthGlobalFilter filter = new AuthGlobalFilter(jwtProperties, new AuthProperties());

    @Test
    void adminApiWithoutTokenShouldReturnUnauthorized() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/admin/orders").build()
        );

        filter.filter(exchange, emptyChain()).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void adminApiWithUserTokenShouldReturnForbidden() {
        String token = JwtUtils.generateToken(2L, "user", List.of("USER"), jwtProperties);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/admin/orders")
                        .header(AuthConstant.AUTHORIZATION, AuthConstant.BEARER + token)
                        .build()
        );

        filter.filter(exchange, emptyChain()).block();

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
    }

    @Test
    void adminApiWithAdminTokenShouldForwardUserHeaders() {
        String token = JwtUtils.generateToken(1L, "admin", List.of("ADMIN"), jwtProperties);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/admin/orders")
                        .header(AuthConstant.AUTHORIZATION, AuthConstant.BEARER + token)
                        .build()
        );
        AtomicReference<String> userId = new AtomicReference<>();
        AtomicReference<String> username = new AtomicReference<>();
        AtomicReference<String> roles = new AtomicReference<>();

        filter.filter(exchange, next -> {
            userId.set(next.getRequest().getHeaders().getFirst(AuthConstant.X_USER_ID));
            username.set(next.getRequest().getHeaders().getFirst(AuthConstant.X_USERNAME));
            roles.set(next.getRequest().getHeaders().getFirst(AuthConstant.X_USER_ROLES));
            return Mono.empty();
        }).block();

        assertNull(exchange.getResponse().getStatusCode());
        assertEquals("1", userId.get());
        assertEquals("admin", username.get());
        assertEquals("ADMIN", roles.get());
    }

    @Test
    void optionsRequestShouldBypassAuthFilter() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.method(HttpMethod.OPTIONS, "/api/admin/orders").build()
        );
        AtomicReference<Boolean> forwarded = new AtomicReference<>(false);

        filter.filter(exchange, next -> {
            forwarded.set(true);
            return Mono.empty();
        }).block();

        assertNull(exchange.getResponse().getStatusCode());
        assertEquals(true, forwarded.get());
    }

    private GatewayFilterChain emptyChain() {
        return exchange -> {
            assertNotNull(exchange);
            return Mono.empty();
        };
    }
}
