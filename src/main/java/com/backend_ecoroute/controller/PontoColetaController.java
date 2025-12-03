package com.backend_ecoroute.controller;

import com.backend_ecoroute.model.PontoColeta;
import com.backend_ecoroute.repository.PontoColetaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pontos-coleta")
@CrossOrigin(origins = "*")
public class PontoColetaController {

    private final PontoColetaRepository repository;

    public PontoColetaController(PontoColetaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PontoColeta> listarTodos() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColeta> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PontoColeta criar(@RequestBody PontoColeta ponto) {
        return repository.save(ponto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoColeta> atualizar(@PathVariable Long id, @RequestBody PontoColeta dados) {
        return repository.findById(id)
                .map(ponto -> {
                    ponto.setNome(dados.getNome());
                    ponto.setResponsavel(dados.getResponsavel());
                    ponto.setTelefone(dados.getTelefone());
                    ponto.setEmail(dados.getEmail());
                    ponto.setEndereco(dados.getEndereco());
                    ponto.setHorario(dados.getHorario());
                    ponto.setTiposResiduo(dados.getTiposResiduo());
                    return ResponseEntity.ok(repository.save(ponto));
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
