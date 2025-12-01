package com.backend_ecoroute.service;

import com.backend_ecoroute.model.Bairro;
import com.backend_ecoroute.model.RuaConexao;
import com.backend_ecoroute.repository.BairroRepository;
import com.backend_ecoroute.repository.RuaConexaoRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RoteamentoService {

    private final BairroRepository bairroRepo;
    private final RuaConexaoRepository ruaRepo;

    public RoteamentoService(BairroRepository bairroRepo, RuaConexaoRepository ruaRepo) {
        this.bairroRepo = bairroRepo;
        this.ruaRepo = ruaRepo;
    }

    public Map<String, Object> calcularMenorRotaBier(Long origemId, Long destinoId){
        Map<Long, Double> distancias = new HashMap<>();
        Map<Long, Long> predecessores = new HashMap<>();

        return null;
    }

    public Map<String, Object> calcularMenorRota(Long origemId, Long destinoId) {
        Map<Long, Double> distancias = new HashMap<>();
        Map<Long, Long> predecessores = new HashMap<>();

        // Custom Comparator para ordenar a fila pela menor distância
        Comparator<Long> distanciaComparator = Comparator.comparing(distancias::get);
        PriorityQueue<Long> fila = new PriorityQueue<>(distanciaComparator);

        // Inicialização, carregando o grafo aqui
        List<Bairro> todosBairros = bairroRepo.findAll();
        for (Bairro b : todosBairros) {
            distancias.put(b.getId(), Double.MAX_VALUE);
        }

        distancias.put(origemId, 0.0);
        fila.add(origemId);

        // --- Execução do Dijkstra ---
        while (!fila.isEmpty()) {
            Long uId = fila.poll();

            if (uId.equals(destinoId)) break;

            Bairro u = bairroRepo.findById(uId)
                    .orElseThrow(() -> new RuntimeException("Bairro de origem não encontrado: " + uId));

            // Busca vizinhos (conexões de saída)
            List<RuaConexao> vizinhos = ruaRepo.findByOrigem(u);

            for (RuaConexao rua : vizinhos) {
                Long vId = rua.getDestino().getId();
                Double novoPeso = distancias.get(uId) + rua.getDistanciaKm();

                if (novoPeso < distancias.get(vId)) {
                    distancias.put(vId, novoPeso);
                    predecessores.put(vId, uId);
                    // Adiciona o nó vizinho à fila, reordenando-a
                    fila.add(vId);
                }
            }
        }

        // --- Reconstrução do Caminho ---

        List<String> caminhoNomes = new ArrayList<>();
        Double distanciaFinal = distancias.get(destinoId);

        if (distanciaFinal == null || distanciaFinal == Double.MAX_VALUE) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Caminho não encontrado ou bairros desconectados.");
            return erro;
        }

        Long atual = destinoId;

        // Loop que traça o caminho de trás para frente usando o mapa de predecessores
        while (atual != null) {
            Bairro b = bairroRepo.findById(atual).orElse(null);
            if (b != null) {
                // Adiciona o nome do bairro no início da lista (índice 0)
                caminhoNomes.add(0, b.getNome());
            }

            // Move para o predecessor
            atual = predecessores.get(atual);

            // Condição de parada de segurança para evitar loop infinito ou travamento em origem desconectada
            if (atual != null && atual.equals(origemId)) {
                // Adiciona a origem se ainda não foi adicionada (deve ser o primeiro item)
                if (bairroRepo.findById(origemId).isPresent() && !caminhoNomes.get(0).equals(bairroRepo.findById(origemId).get().getNome())) {
                    caminhoNomes.add(0, bairroRepo.findById(origemId).get().getNome());
                }
                break;
            }
            if (atual != null && caminhoNomes.contains(bairroRepo.findById(atual).orElse(null).getNome())) {
                // Quebra se detectar loop (embora improvável no Dijkstra)
                break;
            }
        }

        // Se a reconstrução resultou em apenas um nó, mas o destino é diferente, algo falhou.
        if (caminhoNomes.size() <= 1 && !origemId.equals(destinoId)) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro interno na reconstrução ou rota inválida.");
            return erro;
        }


        Map<String, Object> resultado = new HashMap<>();
        resultado.put("distanciaTotal", distanciaFinal);
        resultado.put("caminho", caminhoNomes);
        return resultado;
    }
}