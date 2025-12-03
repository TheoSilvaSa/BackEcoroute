package com.backend_ecoroute.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Itinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "caminhao_id")
    private Caminhao caminhao;

    private String rotaDescricao;

    private LocalDate dataAgendada;

    private String status;

    public Itinerario() {
    }

    public Itinerario(Caminhao caminhao, String rotaDescricao, LocalDate dataAgendada, String status) {
        this.caminhao = caminhao;
        this.rotaDescricao = rotaDescricao;
        this.dataAgendada = dataAgendada;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Caminhao getCaminhao() { return caminhao; }
    public void setCaminhao(Caminhao caminhao) { this.caminhao = caminhao; }

    public String getRotaDescricao() { return rotaDescricao; }
    public void setRotaDescricao(String rotaDescricao) { this.rotaDescricao = rotaDescricao; }

    public LocalDate getDataAgendada() { return dataAgendada; }
    public void setDataAgendada(LocalDate dataAgendada) { this.dataAgendada = dataAgendada; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
