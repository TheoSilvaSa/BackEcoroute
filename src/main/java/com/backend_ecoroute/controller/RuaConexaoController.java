package com.backend_ecoroute.controller;

import com.backend_ecoroute.dto.RuaConexaoDTO;
import com.backend_ecoroute.model.RuaConexao;
import com.backend_ecoroute.repository.RuaConexaoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ruas")
@CrossOrigin(origins = "*")
public class RuaConexaoController {

    private final RuaConexaoRepository repository;

    public RuaConexaoController(RuaConexaoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<RuaConexaoDTO> listarTodas() {
        return repository.findAllAsDto();
    }

    @PostMapping
    public RuaConexao criar(@RequestBody RuaConexao rua) {
        return repository.save(rua);
    }

    // Permite atualizar a distância (peso da aresta) [cite: 52]
    @PutMapping("/{id}")
    public ResponseEntity<RuaConexao> atualizar(@PathVariable Long id, @RequestBody RuaConexao dados) {
        return repository.findById(id)
                .map(rua -> {
                    rua.setDistanciaKm(dados.getDistanciaKm());
                    return ResponseEntity.ok(repository.save(rua));
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
