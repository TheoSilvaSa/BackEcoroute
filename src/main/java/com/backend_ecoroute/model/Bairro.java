package com.backend_ecoroute.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Bairro {

    @Id
    private Long id;
    private String nome;

    public Bairro() {
    }

    public Bairro(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}