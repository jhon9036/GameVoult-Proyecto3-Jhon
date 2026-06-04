package com.gamelist.controller;

import com.gamelist.model.Categoria;
import com.gamelist.model.EstadoJuego;
import com.gamelist.model.Plataforma;
import com.gamelist.model.RolUsuario;
import com.gamelist.model.Usuario;
import com.gamelist.model.Videojuego;
import com.gamelist.security.ApiKeyAuthFilter;
import com.gamelist.security.AuthTokenService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests de la capa web de {@link VideojuegoController}: se burlan el repositorio,
 * el servicio de tokens y el repositorio de usuarios. Se importa el filtro de
 * autenticación para validar que los datos quedan acotados al usuario de la sesión.
 */
@WebMvcTest(VideojuegoController.class)
@Import({ApiKeyAuthFilter.class, VideojuegoControllerTest.MetricsTestConfig.class})
class VideojuegoControllerTest {

    private static final String VALID_TOKEN = "good-token";
    private static final String USERNAME = "tester";

    // El slice @WebMvcTest registra el ApiMetricsFilter, que necesita un MeterRegistry.
    @TestConfiguration
    static class MetricsTestConfig {
        @Bean
        MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private com.gamelist.repository.VideojuegoRepository repo;

    @MockBean
    private com.gamelist.repository.UsuarioRepository usuarioRepo;

    @MockBean
    private AuthTokenService authTokenService;

    @BeforeEach
    void setUp() {
        when(authTokenService.validate(VALID_TOKEN)).thenReturn(Optional.of(
                new AuthTokenService.TokenClaims(USERNAME, RolUsuario.USER, Instant.now().plusSeconds(3600))));
    }

    // ── Lectura (requiere sesión de usuario) ──

    @Test
    void listar_devuelve200ConLosJuegosDelUsuario() throws Exception {
        when(repo.buscarConFiltros(eq(USERNAME), any(), any(), any(), any()))
                .thenReturn(List.of(juego(1L, "Celeste", EstadoJuego.TERMINADO)));

        mockMvc.perform(autenticado(get("/api/videojuegos")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Celeste"));
    }

    @Test
    void listarSinSesion_devuelve401() throws Exception {
        mockMvc.perform(get("/api/videojuegos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerInexistente_devuelve404() throws Exception {
        when(repo.findByIdAndUsuarioUsername(99L, USERNAME)).thenReturn(Optional.empty());

        mockMvc.perform(autenticado(get("/api/videojuegos/99")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void estadisticas_cuentaPorEstadoYTotal() throws Exception {
        when(repo.findByUsuarioUsername(USERNAME)).thenReturn(List.of(
                juego(1L, "A", EstadoJuego.PENDIENTE),
                juego(2L, "B", EstadoJuego.PENDIENTE),
                juego(3L, "C", EstadoJuego.TERMINADO)
        ));

        mockMvc.perform(autenticado(get("/api/videojuegos/estadisticas")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.PENDIENTE").value(2))
                .andExpect(jsonPath("$.TERMINADO").value(1))
                .andExpect(jsonPath("$.JUGANDO").value(0))
                .andExpect(jsonPath("$.TOTAL").value(3));
    }

    // ── Escritura (requiere sesión de usuario) ──

    @Test
    void crearSinSesion_devuelve401() throws Exception {
        mockMvc.perform(post("/api/videojuegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonJuego("Hades", "JUGANDO")))
                .andExpect(status().isUnauthorized());

        verify(repo, never()).save(any());
    }

    @Test
    void crearConSesionYDatosValidos_devuelve201() throws Exception {
        when(usuarioRepo.findByUsernameIgnoreCase(USERNAME)).thenReturn(Optional.of(usuario()));
        when(repo.save(any(Videojuego.class))).thenAnswer(inv -> {
            Videojuego v = inv.getArgument(0);
            v.setId(10L);
            return v;
        });

        mockMvc.perform(autenticado(post("/api/videojuegos"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonJuego("Hades", "JUGANDO")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Hades"));
    }

    @Test
    void crearConTituloVacio_devuelve400() throws Exception {
        mockMvc.perform(autenticado(post("/api/videojuegos"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonJuego("", "JUGANDO")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.titulo").exists());

        verify(repo, never()).save(any());
    }

    @Test
    void eliminarInexistenteConSesion_devuelve404() throws Exception {
        when(repo.findByIdAndUsuarioUsername(42L, USERNAME)).thenReturn(Optional.empty());

        mockMvc.perform(autenticado(delete("/api/videojuegos/42")))
                .andExpect(status().isNotFound());

        verify(repo, never()).delete(any());
    }

    @Test
    void eliminarExistenteConSesion_devuelve204() throws Exception {
        Videojuego existente = juego(5L, "Celeste", EstadoJuego.TERMINADO);
        when(repo.findByIdAndUsuarioUsername(5L, USERNAME)).thenReturn(Optional.of(existente));

        mockMvc.perform(autenticado(delete("/api/videojuegos/5")))
                .andExpect(status().isNoContent());

        verify(repo).delete(existente);
    }

    // ── Helpers ──

    private static MockHttpServletRequestBuilder autenticado(MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + VALID_TOKEN);
    }

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

    private static Usuario usuario() {
        Usuario u = new Usuario();
        u.setId(1L);
        u.setUsername(USERNAME);
        u.setPasswordHash("hash");
        u.setRole(RolUsuario.USER);
        return u;
    }
}
