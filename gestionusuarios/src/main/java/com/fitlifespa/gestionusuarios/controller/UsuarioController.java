package com.fitlifespa.gestionusuarios.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fitlifespa.gestionusuarios.model.Rol;
import com.fitlifespa.gestionusuarios.model.Usuario;
import com.fitlifespa.gestionusuarios.service.RolService;
import com.fitlifespa.gestionusuarios.service.UsuarioService;

@Controller
@RequestMapping("/api")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> mostrarUsuarios() {
        List<Usuario> usuarios = usuarioService.findAllUsuario();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios/id/{id}")
    public ResponseEntity<?> mostrarUsuario(@PathVariable Long id){
        try{
            Usuario usuario = usuarioService.findByIdUsuario(id);
            return ResponseEntity.ok(usuario);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuarios/correo/{correo}")
    public ResponseEntity<Usuario> buscarPorCorreoUsuario(@PathVariable String correo) {
        try {
            Usuario usuario = usuarioService.findByCorreoUsuario(correo);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuarios/rut/{rut}")
    public ResponseEntity<Usuario> buscarPorRutUsuario(@PathVariable String rut) {
        try {
            Usuario usuario = usuarioService.findByRutUsuario(rut);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        try{
            usuario.setIdUsuario(null);
            Usuario nuevoUsuario = usuarioService.saveUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/usuarios/id/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestBody Usuario usuario, @PathVariable Long id) {
        try{
            Usuario usu = usuarioService.findByIdUsuario(id);
            usu.setRut(usuario.getRut());
            usu.setPnombre(usuario.getPnombre());
            usu.setSnombre(usuario.getSnombre());
            usu.setApellidop(usuario.getApellidop());
            usu.setApellidom(usuario.getApellidom());
            usu.setCorreo(usuario.getCorreo());
            usu.setNomUsuario(usuario.getNomUsuario());
            usu.setContrasena(usuario.getContrasena());
            usu.setRol(usuario.getRol());
            usuarioService.saveUsuario(usu);

            return ResponseEntity.ok(usu);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }
    
    @Autowired
    private RolService rolService;

    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> mostrarRoles(){
        List<Rol> roles = rolService.findAllRol();
        if (roles.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/roles/{id}")
    public ResponseEntity<?> mostrarRol(@PathVariable Long id){
        try{
            Rol rol = rolService.findByIdRol(id);
            return ResponseEntity.ok(rol);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}
