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
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    // Atributo de request donde se publica el username autenticado para los controladores.
    public static final String USER_ATTRIBUTE = "authUsername";

    private static final String HEADER_NAME = "X-API-Key";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final Set<String> PROTECTED_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    // Recursos cuyos datos pertenecen a un usuario concreto: tanto lecturas como
    // escrituras exigen una sesión de usuario válida (token Bearer), no basta la API Key.
    private static final List<String> OWNED_PREFIXES = List.of(
            "/api/videojuegos", "/api/wishlist", "/api/resenas");

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
                // El preflight CORS no lleva credenciales: nunca debe bloquearse.
                || "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String apiKey = request.getHeader(HEADER_NAME);
        String authorization = request.getHeader("Authorization");

        // Resuelve la identidad del token (si lo hay) y la expone a los controladores.
        Optional<AuthTokenService.TokenClaims> claims = bearerClaims(authorization);
        claims.ifPresent(c -> request.setAttribute(USER_ATTRIBUTE, c.username()));

        String path = request.getRequestURI();
        boolean ownedResource = OWNED_PREFIXES.stream().anyMatch(path::startsWith);

        if (ownedResource) {
            // Datos por-usuario: se requiere sesión de usuario válida para leer o escribir.
            if (claims.isPresent()) {
                filterChain.doFilter(request, response);
                return;
            }
            unauthorized(response);
            return;
        }

        // Recursos compartidos (categorías, plataformas): las lecturas son públicas y
        // solo las escrituras exigen API Key o sesión.
        boolean isWrite = PROTECTED_METHODS.contains(request.getMethod());
        if (!isWrite || isValidApiKey(apiKey) || claims.isPresent()) {
            filterChain.doFilter(request, response);
            return;
        }
        unauthorized(response);
    }

    private Optional<AuthTokenService.TokenClaims> bearerClaims(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }
        return authTokenService.validate(authorization.substring(BEARER_PREFIX.length()));
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
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
}
