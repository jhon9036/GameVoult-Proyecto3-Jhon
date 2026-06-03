package com.gamelist.repository;

import com.gamelist.model.PrioridadWishlist;
import com.gamelist.model.Wishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica {@link WishlistRepository#buscarConFiltros} sobre H2, cubriendo el
 * refactor que reemplazó el filtrado en memoria del WishlistController.
 */
@DataJpaTest
class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository repo;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        persistir("Silksong", PrioridadWishlist.ALTA);
        persistir("Metroid Prime 4", PrioridadWishlist.MEDIA);
        persistir("Elden Ring DLC", PrioridadWishlist.ALTA);
        em.flush();
    }

    @Test
    void sinFiltros_devuelveTodos() {
        assertThat(repo.buscarConFiltros(null, null)).hasSize(3);
    }

    @Test
    void filtraPorPrioridad() {
        List<Wishlist> resultado = repo.buscarConFiltros(null, PrioridadWishlist.ALTA);
        assertThat(resultado)
                .extracting(Wishlist::getTitulo)
                .containsExactlyInAnyOrder("Silksong", "Elden Ring DLC");
    }

    @Test
    void filtraPorTitulo_ignorandoMayusculas() {
        List<Wishlist> resultado = repo.buscarConFiltros("silk", null);
        assertThat(resultado)
                .extracting(Wishlist::getTitulo)
                .containsExactly("Silksong");
    }

    @Test
    void combinaTituloYPrioridad() {
        assertThat(repo.buscarConFiltros("metroid", PrioridadWishlist.ALTA)).isEmpty();
        assertThat(repo.buscarConFiltros("metroid", PrioridadWishlist.MEDIA)).hasSize(1);
    }

    private void persistir(String titulo, PrioridadWishlist prioridad) {
        Wishlist w = new Wishlist();
        w.setTitulo(titulo);
        w.setPrioridad(prioridad);
        em.persist(w);
    }
}
