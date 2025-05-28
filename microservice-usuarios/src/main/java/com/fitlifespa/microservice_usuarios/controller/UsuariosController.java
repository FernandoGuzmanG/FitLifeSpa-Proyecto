package com.fitlifespa.microservice_usuarios.controller;

import java.util.List;
import java.util.Map;

import com.fitlifespa.microservice_usuarios.dto.UsuarioPerfil;
import com.fitlifespa.microservice_usuarios.security.RoleValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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

import com.fitlifespa.microservice_usuarios.model.Rol;
import com.fitlifespa.microservice_usuarios.model.Usuario;
import com.fitlifespa.microservice_usuarios.service.EstadoUsuarioService;
import com.fitlifespa.microservice_usuarios.service.RolService;
import com.fitlifespa.microservice_usuarios.service.UsuarioService;

@Controller
@RequestMapping("/api/usuarios")
public class UsuariosController {
    @Autowired
    private EstadoUsuarioService estadoUsuarioService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RolService rolService;
    @Autowired
    private RoleValidator roleValidator;

    @GetMapping
    public ResponseEntity<List<Usuario>> mostrarUsuarios(HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        List<Usuario> usuarios = usuarioService.findAll();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> mostrarUsuario(@PathVariable Long id, HttpServletRequest request){
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            Usuario usuario = usuarioService.findById(id);
            return ResponseEntity.ok(usuario);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<Usuario> buscarPorCorreoUsuario(@PathVariable String correo, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Usuario usuario = usuarioService.findByCorreo(correo);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<Usuario> buscarPorRut(@PathVariable String rut, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Usuario usuario = usuarioService.findByRut(rut);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            usuario.setIdUsuario(null);
            Usuario nuevoUsuario = usuarioService.save(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<Usuario> actualizarUsuario(@RequestBody Usuario usuario, @PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            Usuario usu = usuarioService.findById(id);
            usu.setRut(usuario.getRut());
            usu.setPnombre(usuario.getPnombre());
            usu.setSnombre(usuario.getSnombre());
            usu.setAppaterno(usuario.getAppaterno());
            usu.setApmaterno(usuario.getApmaterno());
            usu.setCorreo(usuario.getCorreo());
            usu.setClave(usuario.getClave());
            usu.setRol(usuario.getRol());
            usu.setEstado(usuario.getEstado());
            usuarioService.save(usu);

            return ResponseEntity.ok(usu);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/id/{id}/activar")
    public ResponseEntity<Usuario> activarUsuario(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            Usuario usu = usuarioService.findById(id);
            usu.setEstado(estadoUsuarioService.findByNombre("ACTIVO"));
            return ResponseEntity.ok(usu);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/id/{id}/desactivar")
    public ResponseEntity<Usuario> desactivarUsuario(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            Usuario usu = usuarioService.findById(id);
            usu.setEstado(estadoUsuarioService.findByNombre("INACTIVO"));
            return ResponseEntity.ok(usu);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/id/{id}/{nombreRol}")
    public ResponseEntity<Usuario> cambiarRolUsuario(@PathVariable Long id, @PathVariable String nombreRol, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
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

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id, HttpServletRequest request){
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }

    }


    @PutMapping("/{id}/cambiar-password")
    public ResponseEntity<String> cambiarPassword(@PathVariable Long id,
                                                  @RequestBody Map<String, String> datos) {
        String nueva = datos.get("nuevaContrasena");
        String confirmar = datos.get("confirmarContrasena");

        try {
            usuarioService.cambiarClave(id, nueva, confirmar);
            return ResponseEntity.ok("Contraseña actualizada exitosamente.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfil> verPerfil(HttpServletRequest request) {
        Long idUsuario = roleValidator.getUserId(request);
        Usuario usuario = usuarioService.findById(idUsuario);

        UsuarioPerfil dto = new UsuarioPerfil(
                usuario.getRut(),
                usuario.getPnombre(),
                usuario.getSnombre(),
                usuario.getAppaterno(),
                usuario.getApmaterno(),
                usuario.getCorreo()
        );

        return ResponseEntity.ok(dto);
    }


    @PutMapping("/perfil")
    public ResponseEntity<UsuarioPerfil> actualizarPerfil(@RequestBody UsuarioPerfil nuevosDatos,
                                                             HttpServletRequest request) {
        Long idUsuario = roleValidator.getUserId(request);
        Usuario usuario = usuarioService.findById(idUsuario);

        usuario.setRut(nuevosDatos.getRut());
        usuario.setPnombre(nuevosDatos.getPnombre());
        usuario.setSnombre(nuevosDatos.getSnombre());
        usuario.setAppaterno(nuevosDatos.getAppaterno());
        usuario.setApmaterno(nuevosDatos.getApmaterno());
        usuario.setCorreo(nuevosDatos.getCorreo());

        usuarioService.save(usuario);

        UsuarioPerfil dto = new UsuarioPerfil(
                usuario.getRut(),
                usuario.getPnombre(),
                usuario.getSnombre(),
                usuario.getAppaterno(),
                usuario.getApmaterno(),
                usuario.getCorreo()
        );

        return ResponseEntity.ok(dto);
    }
}
