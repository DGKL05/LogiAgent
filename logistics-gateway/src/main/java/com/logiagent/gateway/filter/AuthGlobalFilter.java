package com.logiagent.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logiagent.common.auth.AuthConstant;
import com.logiagent.common.auth.JwtProperties;
import com.logiagent.common.auth.JwtUserContext;
import com.logiagent.common.auth.JwtUtils;
import com.logiagent.common.auth.RoleEnum;
import com.logiagent.common.auth.TokenAuthException;
import com.logiagent.common.result.Result;
import com.logiagent.gateway.config.AuthProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final JwtProperties jwtProperties;
    private final AuthProperties authProperties;

    public AuthGlobalFilter(JwtProperties jwtProperties, AuthProperties authProperties) {
        this.jwtProperties = jwtProperties;
        this.authProperties = authProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        String authorization = exchange.getRequest().getHeaders().getFirst(AuthConstant.AUTHORIZATION);
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(AuthConstant.BEARER)) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "未登录或 Token 无效");
        }

        JwtUserContext context;
        try {
            context = JwtUtils.parseToken(authorization.substring(AuthConstant.BEARER.length()), jwtProperties);
        } catch (TokenAuthException ex) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "未登录或 Token 无效");
        }

        if (isAdminOnly(path) && !context.getRoles().contains(RoleEnum.ADMIN.name())) {
            return writeError(exchange, HttpStatus.FORBIDDEN, "无权限访问该资源");
        }

        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(AuthConstant.X_USER_ID);
                    headers.remove(AuthConstant.X_USERNAME);
                    headers.remove(AuthConstant.X_USER_ROLES);
                    headers.add(AuthConstant.X_USER_ID, String.valueOf(context.getUserId()));
                    headers.add(AuthConstant.X_USERNAME, context.getUsername());
                    headers.add(AuthConstant.X_USER_ROLES, String.join(",", context.getRoles()));
                })
                .build();
        return chain.filter(exchange.mutate().request(request).build());
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isWhitelisted(String path) {
        return matchesAny(path, authProperties.getWhitelist());
    }

    private boolean isAdminOnly(String path) {
        return matchesAny(path, authProperties.getAdminOnly());
    }

    private boolean matchesAny(String path, List<String> patterns) {
        if (patterns == null) {
            return false;
        }
        return patterns.stream().anyMatch(pattern -> matches(path, pattern));
    }

    private boolean matches(String path, String pattern) {
        if (!StringUtils.hasText(pattern)) {
            return false;
        }
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.equals(prefix) || path.startsWith(prefix + "/");
        }
        return path.equals(pattern);
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        try {
            byte[] bytes = OBJECT_MAPPER.writeValueAsString(Result.failure(status.value(), message))
                    .getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception ex) {
            return exchange.getResponse().setComplete();
        }
    }
}
