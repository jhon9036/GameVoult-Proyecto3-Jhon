package com.gamelist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "usuario_app")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 3, max = 30, message = "El usuario debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9_.-]+$", message = "El usuario solo permite letras, numeros, punto, guion y guion bajo")
    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
    @Column(length = 80)
    private String displayName;

    @JsonIgnore
    @Column(nullable = false, length = 260)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (role == null) {
            role = RolUsuario.USER;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
