package com.backend_ecoroute.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

@Entity
public class Caminhao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "[A-Z]{3}[0-9][A-Z0-9][0-9]{2}", message = "Placa inválida")
    private String placa;

    private String motorista;
    private Double capacidadeCarga;
    private String residuosSuportados;

    public Caminhao() {
    }

    public Caminhao(String placa, String motorista, Double capacidadeCarga, String residuosSuportados) {
        this.placa = placa;
        this.motorista = motorista;
        this.capacidadeCarga = capacidadeCarga;
        this.residuosSuportados = residuosSuportados;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMotorista() {
        return motorista;
    }

    public void setMotorista(String motorista) {
        this.motorista = motorista;
    }

    public Double getCapacidadeCarga() {
        return capacidadeCarga;
    }

    public void setCapacidadeCarga(Double capacidadeCarga) {
        this.capacidadeCarga = capacidadeCarga;
    }

    public String getResiduosSuportados() {
        return residuosSuportados;
    }

    public void setResiduosSuportados(String residuosSuportados) {
        this.residuosSuportados = residuosSuportados;
    }
}
