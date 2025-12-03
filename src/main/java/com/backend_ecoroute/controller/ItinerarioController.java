package com.backend_ecoroute.controller;

import com.backend_ecoroute.model.Itinerario;
import com.backend_ecoroute.repository.ItinerarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerarios")
@CrossOrigin(origins = "*")
public class ItinerarioController {

    private final ItinerarioRepository repository;

    public ItinerarioController(ItinerarioRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Itinerario> listarAgenda() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> agendarRota(@RequestBody Itinerario itinerario) {
        return ResponseEntity.ok(repository.save(itinerario));
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
