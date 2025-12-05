package com.backend_ecoroute.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

@Entity
public class PontoColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bairro_id")
    private Bairro bairro;

    private String nome;
    private String responsavel;

    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "Telefone inválido")
    private String telefone;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "E-mail inválido")
    private String email;

    private String endereco;
    private String horario;
    private String tiposResiduo;

    public PontoColeta() {
    }

    public PontoColeta(Long id, Bairro bairro, String nome, String responsavel, String telefone, String email, String endereco, String horario, String tiposResiduo) {
        this.id = id;
        this.bairro = bairro;
        this.nome = nome;
        this.responsavel = responsavel;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.horario = horario;
        this.tiposResiduo = tiposResiduo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getTiposResiduo() {
        return tiposResiduo;
    }

    public void setTiposResiduo(String tiposResiduo) {
        this.tiposResiduo = tiposResiduo;
    }
}