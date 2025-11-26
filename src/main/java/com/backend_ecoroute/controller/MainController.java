package com.backend_ecoroute.controller;

import com.backend_ecoroute.model.Usuario;
import com.backend_ecoroute.repository.UsuarioRepository;
import com.backend_ecoroute.repository.BairroRepository;
import com.backend_ecoroute.service.RoteamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MainController {

    private final UsuarioRepository usuarioRepository;
    private final RoteamentoService roteamentoService;
    private final BairroRepository bairroRepository;

    public MainController(UsuarioRepository usuarioRepository, RoteamentoService roteamentoService, BairroRepository bairroRepository) {
        this.usuarioRepository = usuarioRepository;
        this.roteamentoService = roteamentoService;
        this.bairroRepository = bairroRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String senha = loginData.get("senha");

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario != null && usuario.getSenha().equals(senha)) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(401).body("Credenciais inválidas");
    }

    @GetMapping("/rota")
    public ResponseEntity<?> calcularRota(@RequestParam Long origemId, @RequestParam Long destinoId) {
        Map<String, Object> resultado = roteamentoService.calcularMenorRota(origemId, destinoId);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/itinerario/calcular")
    public ResponseEntity<?> calcularItinerario(@RequestBody List<Long> idsBairros) {
        if (idsBairros == null || idsBairros.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum ponto de coleta selecionado.");
        }

        // --- Lógica Placeholder: Simula a rota otimizada ---

        Map<String, Object> resultado = new HashMap<>();

        // 1. A rota sempre começa no Centro (ID 2)
        List<Long> rotaOtimizadaIds = new ArrayList<>();
        rotaOtimizadaIds.add(2L);
        rotaOtimizadaIds.addAll(idsBairros); // Adiciona os pontos selecionados
        rotaOtimizadaIds.add(2L); // 2. A rota deve terminar no Centro (Garagem - ID 2)

        // 2. Converte IDs para Nomes (para o Front-end)
        List<Map<String, Object>> bairrosVisitados = new ArrayList<>();
        for (Long id : rotaOtimizadaIds) {
            // Assume que bairroRepository está injetado na classe
            bairroRepository.findById(id).ifPresent(bairro -> {
                Map<String, Object> bMap = new HashMap<>();
                bMap.put("id", bairro.getId());
                bMap.put("nome", bairro.getNome());
                bairrosVisitados.add(bMap);
            });
        }

        // Retorna a distância e a sequência de bairros (simulando a saída do TSP)
        resultado.put("distancia", 50.5); // Distância simulada em km
        resultado.put("bairrosVisitados", bairrosVisitados);

        return ResponseEntity.ok(resultado);
    }
}
