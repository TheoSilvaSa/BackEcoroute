package com.backend_ecoroute.controller;

import com.backend_ecoroute.model.Rota;
import com.backend_ecoroute.repository.RotaRepository;
import com.backend_ecoroute.repository.ItinerarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rotas")
@CrossOrigin(origins = "*")
public class RotaController {

    private final RotaRepository rotaRepository;
    private final ItinerarioRepository itinerarioRepository;

    public RotaController(RotaRepository rotaRepository, ItinerarioRepository itinerarioRepository) {
        this.rotaRepository = rotaRepository;
        this.itinerarioRepository = itinerarioRepository;
    }

    @GetMapping
    public List<Rota> listarTodas() {
        return rotaRepository.findAll();
    }

    @PostMapping
    public Rota salvarRota(@RequestBody Rota rota) {
        return rotaRepository.save(rota);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        return rotaRepository.findById(id).map(rota -> {
            if (itinerarioRepository.existsByRotaDescricao(rota.getNome())) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Não é possível excluir: Esta rota possui agendamentos ativos."));
            }

            rotaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}