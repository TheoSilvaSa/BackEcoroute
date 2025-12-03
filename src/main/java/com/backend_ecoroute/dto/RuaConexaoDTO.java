package com.backend_ecoroute.dto;

public record RuaConexaoDTO(
        Long id,
        Long origemId,
        Long destinoId,
        Double distanciaKm
) {}
