package com.gamelist.security;

import com.gamelist.model.RolUsuario;
import com.gamelist.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class AuthTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String secret;
    private final long ttlMinutes;
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private final Base64.Decoder decoder = Base64.getUrlDecoder();

    public AuthTokenService(
            @Value("${app.auth.token-secret}") String secret,
            @Value("${app.auth.token-ttl-minutes}") long ttlMinutes
    ) {
        this.secret = secret;
        this.ttlMinutes = ttlMinutes;
    }

    public IssuedToken issue(Usuario usuario) {
        Instant expiresAt = Instant.now().plusSeconds(ttlMinutes * 60);
        String payload = usuario.getUsername() + "|" + usuario.getRole().name() + "|" + expiresAt.getEpochSecond();
        String encodedPayload = encoder.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = encoder.encodeToString(sign(encodedPayload));
        return new IssuedToken(encodedPayload + "." + signature, expiresAt);
    }

    public Optional<TokenClaims> validate(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            return Optional.empty();
        }

        byte[] expectedSignature = sign(parts[0]);
        byte[] actualSignature;
        try {
            actualSignature = decoder.decode(parts[1]);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
        if (!MessageDigest.isEqual(expectedSignature, actualSignature)) {
            return Optional.empty();
        }

        String payload;
        try {
            payload = new String(decoder.decode(parts[0]), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }

        String[] payloadParts = payload.split("\\|");
        if (payloadParts.length != 3) {
            return Optional.empty();
        }

        try {
            Instant expiresAt = Instant.ofEpochSecond(Long.parseLong(payloadParts[2]));
            if (expiresAt.isBefore(Instant.now())) {
                return Optional.empty();
            }
            return Optional.of(new TokenClaims(payloadParts[0], RolUsuario.valueOf(payloadParts[1]), expiresAt));
        } catch (RuntimeException ex) {
            return Optional.empty();
        }
    }

    private byte[] sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo firmar el token", ex);
        }
    }

    public record IssuedToken(String token, Instant expiresAt) {}

    public record TokenClaims(String username, RolUsuario role, Instant expiresAt) {}
}
