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
    private final PontoColetaRepository pontoRepository; // Adicionado
    private final UsuarioRepository usuarioRepository;

    // Construtor atualizado com a injeção do PontoColetaRepository
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
        carregarPontosColeta(); // Novo método chamado
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
            br.readLine(); // Pular cabeçalho

            int linhasSalvas = 0;
            System.out.println("--- DEBUG: INICIANDO CARGA DE RUAS ---");

            while ((line = br.readLine()) != null) {

                // Verifica separador (vírgula ou ponto e vírgula)
                String separator = line.contains(";") ? ";" : ",";
                String[] data = line.split(separator, -1);

                if (data.length < 4) {
                    System.err.println("DEBUG ERRO: Faltam colunas na linha: " + line);
                    continue;
                }

                try {
                    // 1. Pega os dados e faz o trim
                    Long id = Long.parseLong(data[0].trim());
                    Long idOrigem = Long.parseLong(data[1].trim());
                    Long idDestino = Long.parseLong(data[2].trim());

                    // 2. CORREÇÃO DECIMAL: Substitui vírgula por ponto para parsing correto
                    String distanciaStr = data[3].trim().replace(",", ".");
                    Double distancia = Double.parseDouble(distanciaStr);

                    // 💡 NOVO: IMPRIME NO CONSOLE PARA DEBUG 💡
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
                    // Loga a exceção se a conversão falhar (isso nos diz que o separador/formato está errado)
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
            br.readLine(); // Pular cabeçalho

            while ((line = br.readLine()) != null) {
                // 💡 NOVO SPLIT: Usando regex para compensar a vírgula dentro das aspas do endereço.
                // Esta regex é mais robusta para ignorar vírgulas dentro de campos entre aspas.
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (data.length < 9) {
                    // Se a linha for malformada, fazemos o split simples como fallback:
                    data = line.split(",", -1);
                    if (data.length < 9) continue;
                }

                Long id = Long.parseLong(data[0].trim());
                Long bairroId = Long.parseLong(data[1].trim());

                Bairro bairro = bairroRepository.findById(bairroId).orElse(null);

                if (bairro != null) {

                    // Mapeamento dos índices (Agora assumindo que o campo 'endereco' está unido, mas o índice está correto)
                    // Usamos .replace("\"", "") para remover as aspas que sobraram na leitura

                    PontoColeta ponto = new PontoColeta(
                            id,
                            bairro,
                            data[2].trim(), // nome
                            data[3].trim(), // responsavel
                            data[4].trim(), // telefone
                            data[5].trim(), // email
                            data[6].trim().replace("\"", ""), // endereco (Limpa as aspas)

                            data[7].trim().replace("\"", ""), // HORÁRIO (Lê a coluna 8)
                            data[8].trim().replace("\"", "")  // RESÍDUO (Lê a coluna 9)
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
