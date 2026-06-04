package com.gamelist.repository;

import com.gamelist.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByVideojuegoId(Long videojuegoId);

    // La reseña es propiedad transitiva del usuario dueño del videojuego asociado.
    Optional<Resena> findByIdAndVideojuegoUsuarioUsername(Long id, String username);
}
