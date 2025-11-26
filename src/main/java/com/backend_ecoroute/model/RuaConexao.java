package com.backend_ecoroute.model;

import jakarta.persistence.*;

@Entity
public class RuaConexao {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bairro_origem_id")
    private Bairro origem;

    @ManyToOne
    @JoinColumn(name = "bairro_destino_id")
    private Bairro destino;

    private Double distanciaKm;

    public RuaConexao() {
    }

    public RuaConexao(Long id, Bairro origem, Bairro destino, Double distanciaKm) {
        this.id = id;
        this.origem = origem;
        this.destino = destino;
        this.distanciaKm = distanciaKm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bairro getOrigem() {
        return origem;
    }

    public void setOrigem(Bairro origem) {
        this.origem = origem;
    }

    public Bairro getDestino() {
        return destino;
    }

    public void setDestino(Bairro destino) {
        this.destino = destino;
    }

    public Double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(Double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }
}