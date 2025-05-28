package com.fitlifespa.microservice_membresias.controller;

import com.fitlifespa.microservice_membresias.model.Incluido;
import com.fitlifespa.microservice_membresias.security.RoleValidator;
import com.fitlifespa.microservice_membresias.service.IncluidoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incluidos")
public class IncluidoController {
    @Autowired
    private RoleValidator roleValidator;

    @Autowired
    private IncluidoService incluidoService;

    @GetMapping("/plan/{idPlan}")
    public ResponseEntity<List<Incluido>> listarPorPlan(@PathVariable Long idPlan, HttpServletRequest request) {
        List<Incluido> incluidos = incluidoService.listarPorPlan(idPlan);
        if (incluidos.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(incluidos);
    }

    @PostMapping
    public ResponseEntity<Incluido> crearIncluido(@RequestBody Incluido incluido, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            incluido.setIdIncluido(null);
            Incluido nuevoIncluido = incluidoService.save(incluido);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoIncluido);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIncluido(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try{
            incluidoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}

