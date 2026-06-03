package com.gamelist.repository;

import com.gamelist.model.Categoria;
import com.gamelist.model.EstadoJuego;
import com.gamelist.model.Plataforma;
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
 * de datos H2 real. Es la red de seguridad del refactor que movió el filtrado de
 * memoria (findAll().stream()) a una única consulta JPQL.
 */
@DataJpaTest
class VideojuegoRepositoryTest {

    @Autowired
    private VideojuegoRepository repo;

    @Autowired
    private TestEntityManager em;

    private Categoria accion;
    private Plataforma pc;

    @BeforeEach
    void setUp() {
        accion = nuevaCategoria("Acción");
        Categoria rpg = nuevaCategoria("RPG");
        pc = nuevaPlataforma("PC");
        Plataforma switchPlat = nuevaPlataforma("Switch");

        persistir("The Witcher 3", EstadoJuego.TERMINADO, rpg, pc);
        persistir("Hollow Knight", EstadoJuego.JUGANDO, accion, pc);
        persistir("Zelda: Breath of the Wild", EstadoJuego.PENDIENTE, accion, switchPlat);
        em.flush();
    }

    @Test
    void sinFiltros_devuelveTodos() {
        List<Videojuego> resultado = repo.buscarConFiltros(null, null, null, null);
        assertThat(resultado).hasSize(3);
    }

    @Test
    void filtraPorTitulo_ignorandoMayusculas() {
        List<Videojuego> resultado = repo.buscarConFiltros("knight", null, null, null);
        assertThat(resultado)
                .extracting(Videojuego::getTitulo)
                .containsExactly("Hollow Knight");
    }

    @Test
    void filtraPorEstado() {
        List<Videojuego> resultado = repo.buscarConFiltros(null, EstadoJuego.PENDIENTE, null, null);
        assertThat(resultado)
                .extracting(Videojuego::getTitulo)
                .containsExactly("Zelda: Breath of the Wild");
    }

    @Test
    void filtraPorCategoria() {
        List<Videojuego> resultado = repo.buscarConFiltros(null, null, accion.getId(), null);
        assertThat(resultado)
                .extracting(Videojuego::getTitulo)
                .containsExactlyInAnyOrder("Hollow Knight", "Zelda: Breath of the Wild");
    }

    @Test
    void combinaTituloYPlataforma() {
        List<Videojuego> resultado = repo.buscarConFiltros("knight", null, null, pc.getId());
        assertThat(resultado)
                .extracting(Videojuego::getTitulo)
                .containsExactly("Hollow Knight");
    }

    @Test
    void filtroSinCoincidencias_devuelveVacio() {
        List<Videojuego> resultado = repo.buscarConFiltros("no-existe", null, null, null);
        assertThat(resultado).isEmpty();
    }

    // ── Helpers ──

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

    private void persistir(String titulo, EstadoJuego estado, Categoria categoria, Plataforma plataforma) {
        Videojuego v = new Videojuego();
        v.setTitulo(titulo);
        v.setAnio(2017);
        v.setEstado(estado);
        v.setCategoria(categoria);
        v.setPlataforma(plataforma);
        em.persist(v);
    }
}
