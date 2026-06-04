package com.gamelist.controller;

import com.gamelist.model.PrioridadWishlist;
import com.gamelist.model.Usuario;
import com.gamelist.model.Wishlist;
import com.gamelist.repository.UsuarioRepository;
import com.gamelist.repository.WishlistRepository;
import com.gamelist.security.ApiKeyAuthFilter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistRepository repo;
    private final UsuarioRepository usuarioRepo;

    public WishlistController(WishlistRepository repo, UsuarioRepository usuarioRepo) {
        this.repo = repo;
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping
    public List<Wishlist> listar(
            @RequestAttribute(ApiKeyAuthFilter.USER_ATTRIBUTE) String username,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) PrioridadWishlist prioridad) {
        return repo.buscarConFiltros(username, normalizarTituloFiltro(titulo), prioridad);
    }

    @GetMapping("/{id}")
    public Wishlist obtener(
            @RequestAttribute(ApiKeyAuthFilter.USER_ATTRIBUTE) String username,
            @PathVariable Long id) {
        return repo.findByIdAndUsuarioUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item de wishlist con id " + id + " no encontrado"));
    }

    @PostMapping
    public ResponseEntity<Wishlist> crear(
            @RequestAttribute(ApiKeyAuthFilter.USER_ATTRIBUTE) String username,
            @Valid @RequestBody Wishlist item) {
        item.setId(null);
        item.setUsuario(usuarioActual(username));
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(item));
    }

    @PutMapping("/{id}")
    public Wishlist actualizar(
            @RequestAttribute(ApiKeyAuthFilter.USER_ATTRIBUTE) String username,
            @PathVariable Long id,
            @Valid @RequestBody Wishlist datos) {
        Wishlist existente = repo.findByIdAndUsuarioUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item de wishlist con id " + id + " no encontrado"));

        existente.setTitulo(datos.getTitulo());
        existente.setPrioridad(datos.getPrioridad());
        existente.setNotas(datos.getNotas());
        existente.setPlataforma(datos.getPlataforma());
        existente.setCategoria(datos.getCategoria());
        return repo.save(existente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @RequestAttribute(ApiKeyAuthFilter.USER_ATTRIBUTE) String username,
            @PathVariable Long id) {
        Wishlist existente = repo.findByIdAndUsuarioUsername(id, username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item de wishlist con id " + id + " no encontrado"));
        repo.delete(existente);
        return ResponseEntity.noContent().build();
    }

    private Usuario usuarioActual(String username) {
        return usuarioRepo.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Sesion de usuario no valida"));
    }

    private String normalizarTituloFiltro(String titulo) {
        return titulo == null ? "" : titulo.trim();
    }
}
