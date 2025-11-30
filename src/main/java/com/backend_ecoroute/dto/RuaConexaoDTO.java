package com.backend_ecoroute.dto;

// Usamos Record (Java 17) para simplificar DTOs sem getters/setters longos
public record RuaConexaoDTO(
        Long id,
        Long origemId,
        Long destinoId,
        Double distanciaKm
) {}
