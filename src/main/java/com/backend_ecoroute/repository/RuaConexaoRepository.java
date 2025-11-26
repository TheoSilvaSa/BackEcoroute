package com.backend_ecoroute.repository;

import com.backend_ecoroute.dto.RuaConexaoDTO;
import com.backend_ecoroute.model.Bairro;
import com.backend_ecoroute.model.RuaConexao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RuaConexaoRepository extends JpaRepository<RuaConexao, Long> {

    List<RuaConexao> findByOrigem(Bairro origem);

    @Query("SELECT new com.backend_ecoroute.dto.RuaConexaoDTO(r.id, r.origem.id, r.destino.id, r.distanciaKm) FROM RuaConexao r")
    List<RuaConexaoDTO> findAllAsDto();
}