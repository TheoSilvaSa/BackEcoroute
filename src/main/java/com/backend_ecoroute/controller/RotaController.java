package com.backend_ecoroute.controller;

import com.backend_ecoroute.model.Rota;
import com.backend_ecoroute.repository.RotaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rotas")
@CrossOrigin(origins = "*")
public class RotaController {

    private final RotaRepository repository;

    public RotaController(RotaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Rota> listarTodas() {
        return repository.findAll();
    }

    @PostMapping
    public Rota salvarRota(@RequestBody Rota rota) {
        return repository.save(rota);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
