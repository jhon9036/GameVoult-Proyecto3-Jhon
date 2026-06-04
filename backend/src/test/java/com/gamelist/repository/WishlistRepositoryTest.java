package com.gamelist.repository;

import com.gamelist.model.PrioridadWishlist;
import com.gamelist.model.RolUsuario;
import com.gamelist.model.Usuario;
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
 * filtrado por usuario dueño y por título/prioridad.
 */
@DataJpaTest
class WishlistRepositoryTest {

    private static final String OWNER = "tester";
    private static final String OTRO = "intruso";

    @Autowired
    private WishlistRepository repo;

    @Autowired
    private TestEntityManager em;

    @BeforeEach
    void setUp() {
        Usuario owner = nuevoUsuario(OWNER);
        Usuario otro = nuevoUsuario(OTRO);

        persistir("Silksong", PrioridadWishlist.ALTA, owner);
        persistir("Metroid Prime 4", PrioridadWishlist.MEDIA, owner);
        persistir("Elden Ring DLC", PrioridadWishlist.ALTA, owner);
        persistir("Deseo Ajeno", PrioridadWishlist.ALTA, otro);
        em.flush();
    }

    @Test
    void sinFiltros_devuelveSoloLosDelUsuario() {
        assertThat(repo.buscarConFiltros(OWNER, null, null)).hasSize(3);
        assertThat(repo.buscarConFiltros(OWNER, null, null))
                .extracting(Wishlist::getTitulo)
                .doesNotContain("Deseo Ajeno");
    }

    @Test
    void otroUsuario_noVeLosItemsDelOwner() {
        assertThat(repo.buscarConFiltros(OTRO, null, null))
                .extracting(Wishlist::getTitulo)
                .containsExactly("Deseo Ajeno");
    }

    @Test
    void filtraPorPrioridad() {
        List<Wishlist> resultado = repo.buscarConFiltros(OWNER, null, PrioridadWishlist.ALTA);
        assertThat(resultado)
                .extracting(Wishlist::getTitulo)
                .containsExactlyInAnyOrder("Silksong", "Elden Ring DLC");
    }

    @Test
    void filtraPorTitulo_ignorandoMayusculas() {
        List<Wishlist> resultado = repo.buscarConFiltros(OWNER, "silk", null);
        assertThat(resultado)
                .extracting(Wishlist::getTitulo)
                .containsExactly("Silksong");
    }

    @Test
    void combinaTituloYPrioridad() {
        assertThat(repo.buscarConFiltros(OWNER, "metroid", PrioridadWishlist.ALTA)).isEmpty();
        assertThat(repo.buscarConFiltros(OWNER, "metroid", PrioridadWishlist.MEDIA)).hasSize(1);
    }

    private Usuario nuevoUsuario(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPasswordHash("hash");
        u.setRole(RolUsuario.USER);
        return em.persist(u);
    }

    private void persistir(String titulo, PrioridadWishlist prioridad, Usuario usuario) {
        Wishlist w = new Wishlist();
        w.setTitulo(titulo);
        w.setPrioridad(prioridad);
        w.setUsuario(usuario);
        em.persist(w);
    }
}
