package com.gamelist.repository;

import com.gamelist.model.PrioridadWishlist;
import com.gamelist.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("""
            SELECT w FROM Wishlist w
            WHERE w.usuario.username = :username
              AND (:titulo    IS NULL OR LOWER(w.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
              AND (:prioridad IS NULL OR w.prioridad = :prioridad)
            """)
    List<Wishlist> buscarConFiltros(
            @Param("username")  String username,
            @Param("titulo")    String titulo,
            @Param("prioridad") PrioridadWishlist prioridad
    );

    Optional<Wishlist> findByIdAndUsuarioUsername(Long id, String username);
}
