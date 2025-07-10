package com.fitlifespa.microservice_usuarios.service;

import java.util.List;

import com.fitlifespa.microservice_usuarios.model.Rol;
import com.fitlifespa.microservice_usuarios.repository.EstadoUsuarioRepository;
import com.fitlifespa.microservice_usuarios.repository.RolRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fitlifespa.microservice_usuarios.model.Usuario;
import com.fitlifespa.microservice_usuarios.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EstadoUsuarioRepository estadoUsuarioRepository;
    @Autowired
    private RolRepository rolRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> findAll(){
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id){
        return usuarioRepository.findById(id).orElseThrow(()-> new RuntimeException("Usuario no encontrado con ID: "+ id));
    }

    public Usuario save(Usuario usuario) {
        if (usuario.getClave().length() < 4 || usuario.getClave().length() > 16) {
            return null;
        }

        usuario.setIdUsuario(null);
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario findByCorreo(String correo){
        return usuarioRepository.findByCorreo(correo).orElseThrow(()-> new RuntimeException("Usuario no encontrado con Correo: "+ correo));
    }

    public Usuario findByRut(String rut){
        return usuarioRepository.findByRut(rut).orElseThrow(()-> new RuntimeException("Usuario no encontrado con Rut: "+ rut));
    }

    public void cambiarClave(Long id, String nueva, String confirmar) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        if (!nueva.equals(confirmar)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        usuario.setClave(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }

    public Usuario actualizarUsuario (Usuario usuario, Long id){
        Usuario usu = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " +id));
        usu.setRut(usuario.getRut());
        usu.setPnombre(usuario.getPnombre());
        usu.setSnombre(usuario.getSnombre());
        usu.setAppaterno(usuario.getAppaterno());
        usu.setApmaterno(usuario.getApmaterno());
        usu.setCorreo(usuario.getCorreo());
        usu.setClave(usuario.getClave());
        usu.setRol(usuario.getRol());
        usu.setEstado(usuario.getEstado());
        usuarioRepository.save(usu);
        return usuario;
    }

    public Usuario activarUsuario (Long id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " +id));
        usuario.setEstado(estadoUsuarioRepository.findByNombre("ACTIVO"));
        return usuario;
    }

    public Usuario desactivarUsuario (Long id){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " +id));
        usuario.setEstado(estadoUsuarioRepository.findByNombre("INACTIVO"));
        return usuario;
    }

    public Usuario cambiarRol (Long id, String nombreRol){
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " +id));
        Rol rol = rolRepository.findByNombre(nombreRol.toUpperCase());
        usuario.setRol(rol);
        return usuario;
    }




}
