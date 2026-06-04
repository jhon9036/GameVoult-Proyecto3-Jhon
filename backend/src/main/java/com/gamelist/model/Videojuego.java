package com.gamelist.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "videojuego")
public class Videojuego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Column(nullable = false)
    private String titulo;

    @Min(value = 1970, message = "El año debe ser mayor a 1970")
    @Max(value = 2100, message = "El año no es válido")
    private Integer anio;

    @Column(length = 2000)
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String imagenUrl;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoJuego estado;

    // Dueño del videojuego: cada registro pertenece al usuario que lo creó.
    // Nullable a nivel de columna para no romper la migración de filas existentes;
    // el controlador siempre lo asigna al crear.
    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plataforma_id")
    private Plataforma plataforma;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "videojuego", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resena> resenas;
}
