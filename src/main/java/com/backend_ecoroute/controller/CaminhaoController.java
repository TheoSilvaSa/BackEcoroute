package com.backend_ecoroute.controller;

import com.backend_ecoroute.model.Caminhao;
import com.backend_ecoroute.repository.CaminhaoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Caminhao criar(@RequestBody Caminhao caminhao) {
        return repository.save(caminhao);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Caminhao> atualizar(@PathVariable Long id, @RequestBody Caminhao dadosAtualizados) {
        return repository.findById(id)
                .map(caminhao -> {
                    caminhao.setPlaca(dadosAtualizados.getPlaca());
                    caminhao.setMotorista(dadosAtualizados.getMotorista());
                    caminhao.setCapacidadeCarga(dadosAtualizados.getCapacidadeCarga());
                    caminhao.setResiduosSuportados(dadosAtualizados.getResiduosSuportados());
                    return ResponseEntity.ok(repository.save(caminhao));
                })
                .orElse(ResponseEntity.notFound().build());
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
