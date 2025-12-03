package com.backend_ecoroute.service;

import com.backend_ecoroute.model.Bairro;
import com.backend_ecoroute.model.PontoColeta;
import com.backend_ecoroute.model.RuaConexao;
import com.backend_ecoroute.model.Usuario;
import com.backend_ecoroute.repository.BairroRepository;
import com.backend_ecoroute.repository.PontoColetaRepository;
import com.backend_ecoroute.repository.RuaConexaoRepository;
import com.backend_ecoroute.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BairroRepository bairroRepository;
    private final RuaConexaoRepository ruaRepository;
    private final PontoColetaRepository pontoRepository;
    private final UsuarioRepository usuarioRepository;

    public DataInitializer(BairroRepository bairroRepository, RuaConexaoRepository ruaRepository, PontoColetaRepository pontoRepository, UsuarioRepository usuarioRepository) {
        this.bairroRepository = bairroRepository;
        this.ruaRepository = ruaRepository;
        this.pontoRepository = pontoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        carregarBairros();
        carregarRuas();
        carregarPontosColeta();
        criarUsuarioAdmin();
    }

    private void carregarBairros() {
        try {
            if (bairroRepository.count() > 0) return;
            var inputStream = getClass().getResourceAsStream("/bairros.csv");
            if (inputStream == null) return;
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Bairro bairro = new Bairro(Long.parseLong(data[0]), data[1]);
                bairroRepository.save(bairro);
            }
            System.out.println("Bairros carregados!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carregarRuas() {
        if (ruaRepository.count() > 0) return;
        try {
            var inputStream = getClass().getResourceAsStream("/ruas_conexoes.csv");
            if (inputStream == null) return;

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            br.readLine();

            int linhasSalvas = 0;
            System.out.println("--- DEBUG: INICIANDO CARGA DE RUAS ---");

            while ((line = br.readLine()) != null) {

                String separator = line.contains(";") ? ";" : ",";
                String[] data = line.split(separator, -1);

                if (data.length < 4) {
                    System.err.println("DEBUG ERRO: Faltam colunas na linha: " + line);
                    continue;
                }

                try {
                    Long id = Long.parseLong(data[0].trim());
                    Long idOrigem = Long.parseLong(data[1].trim());
                    Long idDestino = Long.parseLong(data[2].trim());
                    String distanciaStr = data[3].trim().replace(",", ".");
                    Double distancia = Double.parseDouble(distanciaStr);
                    System.out.println("RAW: " + line);
                    System.out.println(String.format("PARSED: ID=%d, Origem=%d, Destino=%d, Distancia=%.1f",
                            id, idOrigem, idDestino, distancia));


                    Bairro origem = bairroRepository.findById(idOrigem).orElse(null);
                    Bairro destino = bairroRepository.findById(idDestino).orElse(null);

                    if (origem != null && destino != null) {
                        RuaConexao rua = new RuaConexao(id, origem, destino, distancia);
                        ruaRepository.save(rua);
                        linhasSalvas++;
                    } else {
                        System.err.println("AVISO: Bairro ID " + idOrigem + " ou " + idDestino + " não encontrado, rota ignorada.");
                    }

                } catch (NumberFormatException e) {
                    System.err.println("ERRO PARSING RUA: Falha ao converter número/ID: " + e.getMessage() + " na linha: " + line);
                }
            }
            System.out.println("--- DEBUG: Ruas carregadas! Total de linhas salvas: " + linhasSalvas + " ---");

        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO ao carregar ruas: " + e.getMessage());
        }
    }

    private void carregarPontosColeta() {
        if (pontoRepository.count() > 0) return;
        try {
            var inputStream = getClass().getResourceAsStream("/pontos_coleta.csv");
            if (inputStream == null) return;

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (data.length < 9) {
                    data = line.split(",", -1);
                    if (data.length < 9) continue;
                }

                // AVISO: Mantemos a leitura do ID do CSV apenas para processar a linha corretamente,
                // mas NÃO vamos usá-lo no construtor para evitar conflito com o @GeneratedValue
                Long idOriginalDoCsv = Long.parseLong(data[0].trim());

                Long bairroId = Long.parseLong(data[1].trim());

                Bairro bairro = bairroRepository.findById(bairroId).orElse(null);

                if (bairro != null) {

                    PontoColeta ponto = new PontoColeta(
                            null, // <--- AQUI ESTÁ A CORREÇÃO: Passamos null no ID para o banco gerar
                            bairro,
                            data[2].trim(),
                            data[3].trim(),
                            data[4].trim(),
                            data[5].trim(),
                            data[6].trim().replace("\"", ""),
                            data[7].trim().replace("\"", ""),
                            data[8].trim().replace("\"", "")
                    );
                    pontoRepository.save(ponto);
                }
            }
            System.out.println("Pontos de Coleta carregados!");
        } catch (Exception e) {
            System.err.println("ERRO ao carregar pontos: " + e.getMessage());
        }
    }

    private void criarUsuarioAdmin() {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario("admin@greenlog.com", "123456", "Administrador", "ADMIN");
            usuarioRepository.save(admin);
            System.out.println("Usuário Admin criado!");
        }
    }
}