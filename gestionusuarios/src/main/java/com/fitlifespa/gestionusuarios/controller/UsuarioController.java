package com.fitlifespa.gestionusuarios.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fitlifespa.gestionusuarios.model.Rol;
import com.fitlifespa.gestionusuarios.model.Usuario;
import com.fitlifespa.gestionusuarios.service.EstadoService;
import com.fitlifespa.gestionusuarios.service.RolService;
import com.fitlifespa.gestionusuarios.service.UsuarioService;

@Controller
@RequestMapping("/api")
public class UsuarioController {
    @Autowired
    private EstadoService estadoService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RolService rolService;

    UsuarioController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> mostrarUsuarios() {
        List<Usuario> usuarios = usuarioService.findAll();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/usuarios/id/{id}")
    public ResponseEntity<?> mostrarUsuario(@PathVariable Long id){
        try{
            Usuario usuario = usuarioService.findById(id);
            return ResponseEntity.ok(usuario);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuarios/correo/{correo}")
    public ResponseEntity<Usuario> buscarPorCorreoUsuario(@PathVariable String correo) {
        try {
            Usuario usuario = usuarioService.findByCorreo(correo);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuarios/rut/{rut}")
    public ResponseEntity<Usuario> buscarPorRut(@PathVariable String rut) {
        try {
            Usuario usuario = usuarioService.findByRut(rut);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuarios/username/{nomUsuario}")
    public ResponseEntity<Usuario> buscarPorNomUsuario(@PathVariable String nomUsuario) {
        try {
            Usuario usuario = usuarioService.findByUsername(nomUsuario);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        try{
            usuario.setIdUsuario(null);
            Usuario nuevoUsuario = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/usuarios/id/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestBody Usuario usuario, @PathVariable Long id) {
        try{
            Usuario usu = usuarioService.findById(id);
            usu.setRut(usuario.getRut());
            usu.setPnombre(usuario.getPnombre());
            usu.setSnombre(usuario.getSnombre());
            usu.setAppaterno(usuario.getAppaterno());
            usu.setApmaterno(usuario.getApmaterno());
            usu.setCorreo(usuario.getCorreo());
            usu.setNomUsuario(usuario.getNomUsuario());
            usu.setContrasena(usuario.getContrasena());
            usu.setRol(usuario.getRol());
            usu.setEstado(usuario.getEstado());
            usuarioService.save(usu);

            return ResponseEntity.ok(usu);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/usuarios/id/{id}/activar")
    public ResponseEntity<Usuario> activarUsuario(@PathVariable Long id) {
        try{
            Usuario usu = usuarioService.findById(id);
            usu.setEstado(estadoService.findByNombre("ACTIVO"));
            return ResponseEntity.ok(usu);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/usuarios/id/{id}/desactivar")
    public ResponseEntity<Usuario> desactivarUsuario(@PathVariable Long id) {
        try{
            Usuario usu = usuarioService.findById(id);
            usu.setEstado(estadoService.findByNombre("INACTIVO"));
            return ResponseEntity.ok(usu);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/usuarios/id/{id}/{nombreRol}")
    public ResponseEntity<Usuario> cambiarRolUsuario(@PathVariable Long id, @PathVariable String nombreRol) {
        try{
            String nomRol = nombreRol.toUpperCase();
            Usuario usu = usuarioService.findById(id);
            Rol rol = rolService.findByNombre(nomRol);
            usu.setRol(rol);
            return ResponseEntity.ok(usu);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/usuarios/id/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id){
        try{
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
        
    }
    
    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> mostrarRoles(){
        List<Rol> roles = rolService.findAll();
        if (roles.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/roles/id/{id}")
    public ResponseEntity<?> mostrarRol(@PathVariable Long id){
        try{
            Rol rol = rolService.findById(id);
            return ResponseEntity.ok(rol);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/roles/nombre/{nombreRol}")
    public ResponseEntity<?> buscarRolNombre(@PathVariable String nombreRol){
        try{
            String nomRol = nombreRol.toUpperCase();
            Rol rol = rolService.findByNombre(nomRol);
            return ResponseEntity.ok(rol);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}
