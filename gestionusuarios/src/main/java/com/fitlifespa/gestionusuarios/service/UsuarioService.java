package com.fitlifespa.gestionusuarios.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlifespa.gestionusuarios.model.Usuario;
import com.fitlifespa.gestionusuarios.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAllUsuario(){
        return usuarioRepository.findAll();
    }

    public Usuario findByIdUsuario(Long id){
        return usuarioRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuario no encontrado con ID: "+ id));
    }

    public Usuario saveUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteByIdUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario findByCorreoUsuario(String correo){
        return usuarioRepository.findByCorreo(correo);
    }

    public Usuario findByRutUsuario(String rut){
        return usuarioRepository.findByRut(rut);
    }
    
}
