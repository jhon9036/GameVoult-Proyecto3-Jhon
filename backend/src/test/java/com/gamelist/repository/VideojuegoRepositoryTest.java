package com.gamelist.repository;

import com.gamelist.model.Categoria;
import com.gamelist.model.EstadoJuego;
import com.gamelist.model.Plataforma;
import com.gamelist.model.RolUsuario;
import com.gamelist.model.Usuario;
import com.gamelist.model.Videojuego;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica la consulta {@link VideojuegoRepository#buscarConFiltros} sobre una base
 * de datos H2 real. Cubre el filtrado por usuario dueño además de los filtros
 * de título, estado, categoría y plataforma.
 */
@DataJpaTest
class VideojuegoRepositoryTest {

    private static final String OWNER = "tester";
    private static final String OTRO = "intruso";

    @Autowired
    private VideojuegoRepository repo;

    @Autowired
    private TestEntityManager em;

    private Categoria accion;
    private Plataforma pc;

    @BeforeEach
    void setUp() {
        Usuario owner = nuevoUsuario(OWNER);
        Usuario otro = nuevoUsuario(OTRO);

        accion = nuevaCategoria("Acción");
        Categoria rpg = nuevaCategoria("RPG");
        pc = nuevaPlataforma("PC");
        Plataforma switchPlat = nuevaPlataforma("Switch");

        persistir("The Witcher 3", EstadoJuego.TERMINADO, rpg, pc, owner);
        persistir("Hollow Knight", EstadoJuego.JUGANDO, accion, pc, owner);
        persistir("Zelda: Breath of the Wild", EstadoJuego.PENDIENTE, accion, switchPlat, owner);
        // Juego de otro usuario: nunca debe aparecer en las consultas de OWNER.
        persistir("Juego Ajeno", EstadoJuego.TERMINADO, accion, pc, otro);
        em.flush();
    }

    @Test
    void sinFiltros_devuelveSoloLosDelUsuario() {
        List<Videojuego> resultado = repo.buscarConFiltros(OWNER, null, null, null, null);
        assertThat(resultado).hasSize(3);
        assertThat(resultado).extracting(Videojuego::getTitulo).doesNotContain("Juego Ajeno");
    }

    @Test
    void otroUsuario_noVeLosJuegosDelOwner() {
        assertThat(repo.buscarConFiltros(OTRO, null, null, null, null))
                .extracting(Videojuego::getTitulo)
                .containsExactly("Juego Ajeno");
    }

    @Test
    void filtraPorTitulo_ignorandoMayusculas() {
        List<Videojuego> resultado = repo.buscarConFiltros(OWNER, "knight", null, null, null);
        assertThat(resultado)
                .extracting(Videojuego::getTitulo)
                .containsExactly("Hollow Knight");
    }

    @Test
    void filtraPorEstado() {
        List<Videojuego> resultado = repo.buscarConFiltros(OWNER, null, EstadoJuego.PENDIENTE, null, null);
        assertThat(resultado)
                .extracting(Videojuego::getTitulo)
                .containsExactly("Zelda: Breath of the Wild");
    }

    @Test
    void filtraPorCategoria() {
        List<Videojuego> resultado = repo.buscarConFiltros(OWNER, null, null, accion.getId(), null);
        assertThat(resultado)
                .extracting(Videojuego::getTitulo)
                .containsExactlyInAnyOrder("Hollow Knight", "Zelda: Breath of the Wild");
    }

    @Test
    void combinaTituloYPlataforma() {
        List<Videojuego> resultado = repo.buscarConFiltros(OWNER, "knight", null, null, pc.getId());
        assertThat(resultado)
                .extracting(Videojuego::getTitulo)
                .containsExactly("Hollow Knight");
    }

    @Test
    void filtroSinCoincidencias_devuelveVacio() {
        List<Videojuego> resultado = repo.buscarConFiltros(OWNER, "no-existe", null, null, null);
        assertThat(resultado).isEmpty();
    }

    // ── Helpers ──

    private Usuario nuevoUsuario(String username) {
        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPasswordHash("hash");
        u.setRole(RolUsuario.USER);
        return em.persist(u);
    }

    private Categoria nuevaCategoria(String nombre) {
        Categoria c = new Categoria();
        c.setNombre(nombre);
        return em.persist(c);
    }

    private Plataforma nuevaPlataforma(String nombre) {
        Plataforma p = new Plataforma();
        p.setNombre(nombre);
        return em.persist(p);
    }

    private void persistir(String titulo, EstadoJuego estado, Categoria categoria, Plataforma plataforma, Usuario usuario) {
        Videojuego v = new Videojuego();
        v.setTitulo(titulo);
        v.setAnio(2017);
        v.setEstado(estado);
        v.setCategoria(categoria);
        v.setPlataforma(plataforma);
        v.setUsuario(usuario);
        em.persist(v);
    }
}
