package com.backend_ecoroute.controller;


import com.backend_ecoroute.model.Bairro;
import com.backend_ecoroute.repository.BairroRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/bairros")
@CrossOrigin(origins = "*")
public class BairroController {
    private final BairroRepository repository;
    public BairroController(BairroRepository repository) { this.repository = repository; }
    @GetMapping
    public List<Bairro> listarTodos() { return repository.findAll(); }
}
