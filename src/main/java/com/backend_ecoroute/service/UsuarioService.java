package com.backend_ecoroute.service;

import com.backend_ecoroute.model.Usuario;
import com.backend_ecoroute.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrarNovoUsuario(Usuario novoUsuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(novoUsuario.getEmail());
        if (usuarioExistente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já está em uso por outro usuário.");
        }

        String senhaCodificadaMock = "{mock-encoded}" + novoUsuario.getSenha();
        novoUsuario.setSenha(senhaCodificadaMock);

        if (novoUsuario.getPerfil() == null || novoUsuario.getPerfil().isEmpty()) {
            novoUsuario.setPerfil("USER");
        }

        return usuarioRepository.save(novoUsuario);
    }

    public Optional<Usuario> validarCredenciais(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            String senhaCodificadaFornecida = "{mock-encoded}" + senha;

            if (usuario.getSenha().equals(senhaCodificadaFornecida)) {
                return Optional.of(usuario);
            }

            if (usuario.getSenha().equals(senha) && !usuario.getSenha().startsWith("{mock-encoded}")) {
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }
}