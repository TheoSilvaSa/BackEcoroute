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


        Map<String, Object> resultado = new HashMap<>();

        List<Long> rotaOtimizadaIds = new ArrayList<>();
        rotaOtimizadaIds.add(2L);
        rotaOtimizadaIds.addAll(idsBairros);
        rotaOtimizadaIds.add(2L);

        List<Map<String, Object>> bairrosVisitados = new ArrayList<>();
        for (Long id : rotaOtimizadaIds) {
            bairroRepository.findById(id).ifPresent(bairro -> {
                Map<String, Object> bMap = new HashMap<>();
                bMap.put("id", bairro.getId());
                bMap.put("nome", bairro.getNome());
                bairrosVisitados.add(bMap);
            });
        }

        resultado.put("distancia", 50.5);
        resultado.put("bairrosVisitados", bairrosVisitados);

        return ResponseEntity.ok(resultado);
    }
}
