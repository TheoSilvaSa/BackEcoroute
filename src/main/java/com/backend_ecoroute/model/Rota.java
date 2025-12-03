package com.backend_ecoroute.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Rota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ElementCollection
    private List<String> sequenciaBairros;

    private Double distanciaTotal;

    private String caminhaoDesignadoPlaca;
    private String tiposResiduosAtendidos;

    public Rota() {}

    public Rota(String nome, List<String> sequenciaBairros, Double distanciaTotal, String caminhaoDesignadoPlaca, String tiposResiduosAtendidos) {
        this.nome = nome;
        this.sequenciaBairros = sequenciaBairros;
        this.distanciaTotal = distanciaTotal;
        this.caminhaoDesignadoPlaca = caminhaoDesignadoPlaca;
        this.tiposResiduosAtendidos = tiposResiduosAtendidos;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<String> getSequenciaBairros() { return sequenciaBairros; }
    public void setSequenciaBairros(List<String> sequenciaBairros) { this.sequenciaBairros = sequenciaBairros; }
    public Double getDistanciaTotal() { return distanciaTotal; }
    public void setDistanciaTotal(Double distanciaTotal) { this.distanciaTotal = distanciaTotal; }
    public String getCaminhaoDesignadoPlaca() { return caminhaoDesignadoPlaca; }
    public void setCaminhaoDesignadoPlaca(String caminhaoDesignadoPlaca) { this.caminhaoDesignadoPlaca = caminhaoDesignadoPlaca; }
    public String getTiposResiduosAtendidos() { return tiposResiduosAtendidos; }
    public void setTiposResiduosAtendidos(String tiposResiduosAtendidos) { this.tiposResiduosAtendidos = tiposResiduosAtendidos; }
}