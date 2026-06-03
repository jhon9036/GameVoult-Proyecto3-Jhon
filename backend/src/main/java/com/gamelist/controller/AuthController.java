package com.gamelist.controller;

import com.gamelist.model.RolUsuario;
import com.gamelist.model.Usuario;
import com.gamelist.repository.UsuarioRepository;
import com.gamelist.security.AuthTokenService;
import com.gamelist.security.PasswordHasher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;
    private final AuthTokenService authTokenService;

    public AuthController(
            UsuarioRepository usuarioRepository,
            PasswordHasher passwordHasher,
            AuthTokenService authTokenService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.authTokenService = authTokenService;
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@RequestBody LoginRequest request) {
        if (request == null || isBlank(request.username()) || isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario y contrasena son obligatorios");
        }

        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(request.username().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena invalidos"));

        if (!passwordHasher.matches(request.password(), usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena invalidos");
        }

        return responseFor(usuario);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse register(@RequestBody RegisterRequest request) {
        validarRegistro(request);

        String username = request.username().trim();
        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setDisplayName(normalizarNombre(request.displayName(), username));
        usuario.setPasswordHash(passwordHasher.hash(request.password()));
        usuario.setRole(RolUsuario.USER);

        return responseFor(usuarioRepository.save(usuario));
    }

    private LoginResponse responseFor(Usuario usuario) {
        AuthTokenService.IssuedToken issuedToken = authTokenService.issue(usuario);
        return new LoginResponse(
                issuedToken.token(),
                usuario.getUsername(),
                usuario.getDisplayName(),
                usuario.getRole(),
                issuedToken.expiresAt()
        );
    }

    private void validarRegistro(RegisterRequest request) {
        if (request == null
                || isBlank(request.username())
                || isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario y contrasena son obligatorios");
        }
        String username = request.username().trim();
        if (!username.matches("^[A-Za-z0-9_.-]{3,30}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario debe tener 3 a 30 caracteres validos");
        }
        if (request.password().length() < 6 || request.password().length() > 80) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contrasena debe tener entre 6 y 80 caracteres");
        }
    }

    private String normalizarNombre(String displayName, String username) {
        if (displayName == null || displayName.isBlank()) {
            return username;
        }
        return displayName.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    record LoginRequest(String username, String password) {}

    record RegisterRequest(String username, String password, String displayName) {}

    record LoginResponse(
            String token,
            String username,
            String displayName,
            RolUsuario role,
            Instant expiresAt
    ) {}
}
