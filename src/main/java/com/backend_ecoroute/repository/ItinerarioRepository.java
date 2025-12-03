package com.backend_ecoroute.repository;

import com.backend_ecoroute.model.Itinerario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface ItinerarioRepository extends JpaRepository<Itinerario, Long> {

    boolean existsByRotaDescricao(String rotaDescricao);
    boolean existsByCaminhaoIdAndDataAgendada(Long caminhaoId, LocalDate dataAgendada);
}