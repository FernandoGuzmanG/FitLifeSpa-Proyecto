package com.fitlifespa.microservice_usuarios.controller;

import com.fitlifespa.microservice_usuarios.model.Rol;
import com.fitlifespa.microservice_usuarios.security.RoleValidator;
import com.fitlifespa.microservice_usuarios.service.RolService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@Controller
@RequestMapping("/api/roles")
public class RolesController {
    @Autowired
    private RoleValidator roleValidator;
    @Autowired
    private RolService rolService;
    @GetMapping
    public ResponseEntity<List<Rol>> mostrarRoles(HttpServletRequest request){
        roleValidator.requireRole(request, "ADMINISTRADOR");
        List<Rol> roles = rolService.findAll();
        if (roles.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<?> mostrarRol(@PathVariable Long id, HttpServletRequest request){
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            Rol rol = rolService.findById(id);
            return ResponseEntity.ok(rol);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/nombre/{nombreRol}")
    public ResponseEntity<?> buscarRolNombre(@PathVariable String nombreRol, HttpServletRequest request){
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            String nomRol = nombreRol.toUpperCase();
            Rol rol = rolService.findByNombre(nomRol);
            return ResponseEntity.ok(rol);
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
}
