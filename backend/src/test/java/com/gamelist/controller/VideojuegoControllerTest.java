package com.gamelist.controller;

import com.gamelist.model.Categoria;
import com.gamelist.model.EstadoJuego;
import com.gamelist.model.Plataforma;
import com.gamelist.model.Videojuego;
import com.gamelist.security.ApiKeyAuthFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de la capa web de {@link VideojuegoController}: se burla el repositorio y
 * se importa el filtro de API Key para validar también la autenticación de escritura.
 */
@WebMvcTest(VideojuegoController.class)
@Import(ApiKeyAuthFilter.class)
class VideojuegoControllerTest {

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String VALID_KEY = "test-key";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.gamelist.repository.VideojuegoRepository repo;

    // ── Lectura (pública) ──

    @Test
    void listar_devuelve200ConLosJuegos() throws Exception {
        when(repo.buscarConFiltros(any(), any(), any(), any()))
                .thenReturn(List.of(juego(1L, "Celeste", EstadoJuego.TERMINADO)));

        mockMvc.perform(get("/api/videojuegos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Celeste"));
    }

    @Test
    void obtenerInexistente_devuelve404() throws Exception {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/videojuegos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void estadisticas_cuentaPorEstadoYTotal() throws Exception {
        when(repo.findAll()).thenReturn(List.of(
                juego(1L, "A", EstadoJuego.PENDIENTE),
                juego(2L, "B", EstadoJuego.PENDIENTE),
                juego(3L, "C", EstadoJuego.TERMINADO)
        ));

        mockMvc.perform(get("/api/videojuegos/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PENDIENTE").value(2))
                .andExpect(jsonPath("$.TERMINADO").value(1))
                .andExpect(jsonPath("$.JUGANDO").value(0))
                .andExpect(jsonPath("$.TOTAL").value(3));
    }

    // ── Escritura (protegida por API Key) ──

    @Test
    void crearSinApiKey_devuelve401() throws Exception {
        mockMvc.perform(post("/api/videojuegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonJuego("Hades", "JUGANDO")))
                .andExpect(status().isUnauthorized());

        verify(repo, never()).save(any());
    }

    @Test
    void crearConApiKeyYDatosValidos_devuelve201() throws Exception {
        when(repo.save(any(Videojuego.class))).thenAnswer(inv -> {
            Videojuego v = inv.getArgument(0);
            v.setId(10L);
            return v;
        });

        mockMvc.perform(post("/api/videojuegos")
                        .header(API_KEY_HEADER, VALID_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonJuego("Hades", "JUGANDO")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Hades"));
    }

    @Test
    void crearConTituloVacio_devuelve400() throws Exception {
        mockMvc.perform(post("/api/videojuegos")
                        .header(API_KEY_HEADER, VALID_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonJuego("", "JUGANDO")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.titulo").exists());

        verify(repo, never()).save(any());
    }

    @Test
    void eliminarInexistenteConApiKey_devuelve404() throws Exception {
        when(repo.existsById(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/api/videojuegos/42").header(API_KEY_HEADER, VALID_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarExistenteConApiKey_devuelve204() throws Exception {
        when(repo.existsById(5L)).thenReturn(true);

        mockMvc.perform(delete("/api/videojuegos/5").header(API_KEY_HEADER, VALID_KEY))
                .andExpect(status().isNoContent());

        verify(repo).deleteById(5L);
    }

    // ── Helpers ──

    private static String jsonJuego(String titulo, String estado) {
        return """
                {"titulo":"%s","anio":2020,"estado":"%s"}
                """.formatted(titulo, estado);
    }

    private static Videojuego juego(Long id, String titulo, EstadoJuego estado) {
        Videojuego v = new Videojuego();
        v.setId(id);
        v.setTitulo(titulo);
        v.setAnio(2020);
        v.setEstado(estado);
        v.setCategoria(new Categoria());
        v.setPlataforma(new Plataforma());
        return v;
    }
}
