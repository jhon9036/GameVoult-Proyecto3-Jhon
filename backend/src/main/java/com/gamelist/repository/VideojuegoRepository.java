package com.gamelist.repository;

import com.gamelist.model.EstadoJuego;
import com.gamelist.model.Videojuego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideojuegoRepository extends JpaRepository<Videojuego, Long> {

    // Todas las consultas filtran por el usuario dueño para que cada cuenta
    // solo vea su propia biblioteca.
    @Query("""
            SELECT v FROM Videojuego v
            WHERE v.usuario.username = :username
              AND (:titulo      IS NULL OR LOWER(v.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
              AND (:estado      IS NULL OR v.estado = :estado)
              AND (:categoriaId  IS NULL OR v.categoria.id = :categoriaId)
              AND (:plataformaId IS NULL OR v.plataforma.id = :plataformaId)
            """)
    List<Videojuego> buscarConFiltros(
            @Param("username")     String username,
            @Param("titulo")       String titulo,
            @Param("estado")       EstadoJuego estado,
            @Param("categoriaId")  Long categoriaId,
            @Param("plataformaId") Long plataformaId
    );

    List<Videojuego> findByUsuarioUsername(String username);

    Optional<Videojuego> findByIdAndUsuarioUsername(Long id, String username);

    boolean existsByIdAndUsuarioUsername(Long id, String username);
}
