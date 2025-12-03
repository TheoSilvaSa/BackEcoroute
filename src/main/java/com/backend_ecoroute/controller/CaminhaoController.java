package com.backend_ecoroute.controller;

import com.backend_ecoroute.model.Caminhao;
import com.backend_ecoroute.repository.CaminhaoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/caminhoes")
@CrossOrigin(origins = "*")
public class CaminhaoController {

    private final CaminhaoRepository repository;

    public CaminhaoController(CaminhaoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Caminhao> listarTodos() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Caminhao caminhao) {
        if (repository.existsByPlaca(caminhao.getPlaca())) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Já existe um caminhão cadastrado com a placa " + caminhao.getPlaca()));
        }

        return ResponseEntity.ok(repository.save(caminhao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Caminhao dadosAtualizados) {
        return repository.findById(id)
                .map(caminhao -> {
                    if (!caminhao.getPlaca().equals(dadosAtualizados.getPlaca()) &&
                            repository.existsByPlaca(dadosAtualizados.getPlaca())) {
                        throw new IllegalArgumentException("A placa " + dadosAtualizados.getPlaca() + " já está em uso.");
                    }

                    caminhao.setPlaca(dadosAtualizados.getPlaca());
                    caminhao.setMotorista(dadosAtualizados.getMotorista());
                    caminhao.setCapacidadeCarga(dadosAtualizados.getCapacidadeCarga());
                    caminhao.setResiduosSuportados(dadosAtualizados.getResiduosSuportados());
                    return ResponseEntity.ok(repository.save(caminhao));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
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