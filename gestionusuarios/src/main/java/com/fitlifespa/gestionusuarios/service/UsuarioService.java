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

    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id){
        return usuarioRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuario no encontrado con ID: "+ id));
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario findByCorreo(String correo){
        return usuarioRepository.findByCorreo(correo);
    }

    public Usuario findByRut(String rut){
        return usuarioRepository.findByRut(rut);
    }

    public Usuario findByUsername(String nomUsuario){
        return usuarioRepository.findByUsername(nomUsuario);
    }
    
}
