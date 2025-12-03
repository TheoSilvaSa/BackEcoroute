package com.backend_ecoroute.controller;

import com.backend_ecoroute.model.Caminhao;
import com.backend_ecoroute.model.Itinerario;
import com.backend_ecoroute.model.Rota;
import com.backend_ecoroute.repository.CaminhaoRepository;
import com.backend_ecoroute.repository.ItinerarioRepository;
import com.backend_ecoroute.repository.RotaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/itinerarios")
@CrossOrigin(origins = "*")
public class ItinerarioController {

    private final ItinerarioRepository itinerarioRepository;
    private final RotaRepository rotaRepository;
    private final CaminhaoRepository caminhaoRepository;

    public ItinerarioController(ItinerarioRepository itinerarioRepository,
                                RotaRepository rotaRepository,
                                CaminhaoRepository caminhaoRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.rotaRepository = rotaRepository;
        this.caminhaoRepository = caminhaoRepository;
    }

    @GetMapping
    public List<Itinerario> listarAgenda() {
        return itinerarioRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> agendarRota(@RequestBody Itinerario itinerario) {
        if (itinerario.getCaminhao() == null || itinerario.getCaminhao().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Caminhão é obrigatório."));
        }
        if (itinerario.getDataAgendada() == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Data é obrigatória."));
        }

        boolean jaAgendado = itinerarioRepository.existsByCaminhaoIdAndDataAgendada(
                itinerario.getCaminhao().getId(),
                itinerario.getDataAgendada()
        );

        if (jaAgendado) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Este caminhão já tem rota neste dia!"));
        }

        Caminhao caminhaoBanco = caminhaoRepository.findById(itinerario.getCaminhao().getId()).orElse(null);
        Rota rotaBanco = rotaRepository.findByNome(itinerario.getRotaDescricao()).orElse(null);

        if (caminhaoBanco != null && rotaBanco != null) {
            String lixoRotaStr = rotaBanco.getTiposResiduosAtendidos();
            String lixoCaminhaoStr = caminhaoBanco.getResiduosSuportados();

            if (lixoCaminhaoStr == null || lixoCaminhaoStr.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Caminhão não suporta nenhum resíduo."));
            }

            List<String> requisitosRota = Arrays.stream(lixoRotaStr.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            List<String> capacidadesCaminhao = Arrays.stream(lixoCaminhaoStr.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());

            if (!capacidadesCaminhao.containsAll(requisitosRota)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "erro", "Erro: Caminhão (" + lixoCaminhaoStr + ") não atende aos requisitos da rota (" + lixoRotaStr + ")."
                ));
            }
        }

        return ResponseEntity.ok(itinerarioRepository.save(itinerario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (itinerarioRepository.existsById(id)) {
            itinerarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}