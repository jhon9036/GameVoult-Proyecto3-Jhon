package com.gamelist.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-API-Key";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final Set<String> PROTECTED_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final String expectedApiKey;
    private final AuthTokenService authTokenService;

    public ApiKeyAuthFilter(
            @Value("${app.api-key}") String expectedApiKey,
            AuthTokenService authTokenService
    ) {
        this.expectedApiKey = expectedApiKey;
        this.authTokenService = authTokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/")
                || path.startsWith("/api/auth/")
                || !PROTECTED_METHODS.contains(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String apiKey = request.getHeader(HEADER_NAME);
        String authorization = request.getHeader("Authorization");

        if (isValidApiKey(apiKey) || isValidBearerToken(authorization)) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
                {"status":401,"error":"Sesion de usuario requerida","headers":["Authorization: Bearer <token>","X-API-Key"]}
                """);
    }

    private boolean isValidApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(
                apiKey.getBytes(StandardCharsets.UTF_8),
                expectedApiKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    private boolean isValidBearerToken(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            return false;
        }
        String token = authorization.substring(BEARER_PREFIX.length());
        return authTokenService.validate(token).isPresent();
    }
}
