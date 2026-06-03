package com.gamelist.security;

import com.gamelist.model.RolUsuario;
import com.gamelist.model.Usuario;
import com.gamelist.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminUserSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;
    private final String adminUsername;
    private final String adminPassword;

    public AdminUserSeeder(
            UsuarioRepository usuarioRepository,
            PasswordHasher passwordHasher,
            @Value("${app.admin.username}") String adminUsername,
            @Value("${app.admin.password}") String adminPassword
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        usuarioRepository.findByUsernameIgnoreCase(adminUsername)
                .ifPresentOrElse(this::ensureAdminRole, this::createDefaultAdmin);
    }

    private void createDefaultAdmin() {
        Usuario admin = new Usuario();
        admin.setUsername(adminUsername);
        admin.setDisplayName("Administrador");
        admin.setPasswordHash(passwordHasher.hash(adminPassword));
        admin.setRole(RolUsuario.ADMIN);
        usuarioRepository.save(admin);
    }

    private void ensureAdminRole(Usuario usuario) {
        if (usuario.getRole() != RolUsuario.ADMIN) {
            usuario.setRole(RolUsuario.ADMIN);
            usuarioRepository.save(usuario);
        }
    }
}
