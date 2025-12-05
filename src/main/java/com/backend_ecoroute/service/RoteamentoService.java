package com.backend_ecoroute.service;

import com.backend_ecoroute.model.Bairro;
import com.backend_ecoroute.model.RuaConexao;
import com.backend_ecoroute.repository.BairroRepository;
import com.backend_ecoroute.repository.RuaConexaoRepository;
import org.springframework.stereotype.Service;

import java.util.*;

// SINGLETON
// O BeanFactory do Spring Boot inicializa a Service em um padrão Singleton e garante que apenas uma instância será instanciada
// FACTORY
// Pois toda Service é manejada pela BeanFactory do Spring Boot
// SingletonFactory/RoteamentoService é um exemplo disso, ao deixar @Service sem comentários, é possível visualizar que o programa
// crasha ao tentar ser executado, pois já existe uma Service com o mesmo nome
@Service
public class RoteamentoService {

    private final BairroRepository bairroRepo;
    private final RuaConexaoRepository ruaRepo;

    public RoteamentoService(BairroRepository bairroRepo, RuaConexaoRepository ruaRepo) {
        this.bairroRepo = bairroRepo;
        this.ruaRepo = ruaRepo;
    }

    // Classe auxiliar interna para representar o Vizinho
    private static class Neighbor {
        Long nodeId;
        double weight;

        public Neighbor(Long nodeId, double weight) {
            this.nodeId = nodeId;
            this.weight = weight;
        }
    }

    public Map<String, Object> calcularMenorRota(Long origemId, Long destinoId) {

        //Monta o grafo em memória
        List<Bairro> nodes = bairroRepo.findAll();
        List<RuaConexao> edges = ruaRepo.findAll();

        //Verificação básica
        if (!bairroRepo.existsById(origemId) || !bairroRepo.existsById(destinoId)) {
            return criarErro("Bairro de origem ou destino não encontrado.");
        }

        // 2. Estruturas do Dijkstra
        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> previous = new HashMap<>();
        Set<Long> pq = new HashSet<>();

        // Inicialização
        // ITERATOR
        // É um Iterator pois ocorre a Iteração dos Objetos nodes
        for (Bairro node : nodes) {
            distances.put(node.getId(), Double.POSITIVE_INFINITY);
            previous.put(node.getId(), null);
            pq.add(node.getId());
        }

        // Garante que o nó inicial tem distância 0
        distances.put(origemId, 0.0);

        // 3. Montagem da Lista de Adjacência (Mapeando RuaConexao -> Neighbor)
        Map<Long, List<Neighbor>> adj = new HashMap<>();
        // ITERATOR
        // É um Iterator pois ocorre a Iteração dos Objetos nodes
        for (Bairro node : nodes) {
            adj.put(node.getId(), new ArrayList<>());
        }

        for (RuaConexao edge : edges) {
            // O grafo é bi-direcional? Se sim, adicionamos nos dois sentidos.
            // Baseado no seu CSV, as ruas parecem ter sentido único ou duplo dependendo do cadastro.
            // Mas o algoritmo original TypeScript tratava como não-direcionado (adicionava nas duas pontas).
            // Vou manter a lógica do TypeScript original:
            if (edge.getOrigem() != null && edge.getDestino() != null) {
                Long from = edge.getOrigem().getId();
                Long to = edge.getDestino().getId();
                double dist = edge.getDistanciaKm();

                // Adiciona ida
                if (adj.containsKey(from)) adj.get(from).add(new Neighbor(to, dist));
                // Adiciona volta (Se quiser grafo direcionado, remova esta linha abaixo)
                if (adj.containsKey(to)) adj.get(to).add(new Neighbor(from, dist));
            }
        }

        // 4. Execução do Algoritmo (Lógica Core)
        while (!pq.isEmpty()) {
            Long closestNode = null;
            double minDistance = Double.POSITIVE_INFINITY;

            // Busca linear pelo menor nó (igual ao original TS)
            for (Long node : pq) {
                double dist = distances.get(node);
                if (dist < minDistance) {
                    minDistance = dist;
                    closestNode = node;
                }
            }

            if (closestNode == null || distances.get(closestNode) == Double.POSITIVE_INFINITY) break;

            pq.remove(closestNode);

            // Se chegou ao destino
            if (closestNode.equals(destinoId)) {
                return construirRespostaSucesso(destinoId, origemId, previous, distances);
            }

            // Relaxamento das arestas
            if (adj.containsKey(closestNode)) {
                for (Neighbor neighbor : adj.get(closestNode)) {
                    // Importante: verifica se o vizinho ainda está no set de não visitados (opcional mas otimiza)
                    // ou apenas relaxa a aresta:
                    double newDist = distances.get(closestNode) + neighbor.weight;
                    if (newDist < distances.get(neighbor.nodeId)) {
                        distances.put(neighbor.nodeId, newDist);
                        previous.put(neighbor.nodeId, closestNode);
                    }
                }
            }
        }

        return criarErro("Não foi encontrado caminho entre os bairros selecionados.");
    }

    // --- Métodos Auxiliares para montar o JSON de resposta (PathResult) ---

    private Map<String, Object> construirRespostaSucesso(Long destinoId, Long origemId, Map<Long, Long> previous, Map<Long, Double> distances) {
        List<String> pathNames = new ArrayList<>();
        Long currentNode = destinoId;

        // Reconstrói o caminho de trás para frente
        while (currentNode != null) {
            // Busca o nome do bairro no banco para exibir (ou poderia ter cacheado num Map<Long, String> no início)
            String nomeBairro = bairroRepo.findById(currentNode).map(Bairro::getNome).orElse("Desconhecido");
            pathNames.add(0, nomeBairro); // Adiciona no início (unshift)

            currentNode = previous.get(currentNode);
        }

        // Validação de segurança
        String nomeOrigem = bairroRepo.findById(origemId).map(Bairro::getNome).orElse("");
        if (!pathNames.isEmpty() && !pathNames.get(0).equals(nomeOrigem)) {
            return criarErro("Erro na reconstrução do caminho.");
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("caminho", pathNames);
        resultado.put("distanciaTotal",  Math.round(distances.get(destinoId) * 100.0) / 100.0); // Arredonda 2 casas
        return resultado;
    }

    private Map<String, Object> criarErro(String mensagem) {
        Map<String, Object> erro = new HashMap<>();
        erro.put("erro", mensagem);
        return erro;
    }
}