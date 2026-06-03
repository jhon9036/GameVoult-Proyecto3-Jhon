package com.gamelist.controller;

import com.gamelist.model.PrioridadWishlist;
import com.gamelist.model.Wishlist;
import com.gamelist.repository.WishlistRepository;
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

    public WishlistController(WishlistRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Wishlist> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) PrioridadWishlist prioridad) {
        return repo.buscarConFiltros(titulo, prioridad);
    }

    @GetMapping("/{id}")
    public Wishlist obtener(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item de wishlist con id " + id + " no encontrado"));
    }

    @PostMapping
    public ResponseEntity<Wishlist> crear(@Valid @RequestBody Wishlist item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(item));
    }

    @PutMapping("/{id}")
    public Wishlist actualizar(@PathVariable Long id, @Valid @RequestBody Wishlist datos) {
        Wishlist existente = repo.findById(id)
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
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Item de wishlist con id " + id + " no encontrado");
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
