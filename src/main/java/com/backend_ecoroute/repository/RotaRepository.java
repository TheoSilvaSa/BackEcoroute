package com.backend_ecoroute.repository;

import com.backend_ecoroute.model.Rota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RotaRepository extends JpaRepository<Rota, Long> {

    Optional<Rota> findByNome(String nome);
}