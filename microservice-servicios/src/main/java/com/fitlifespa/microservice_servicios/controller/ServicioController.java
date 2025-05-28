package com.fitlifespa.microservice_servicios.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fitlifespa.microservice_servicios.model.Servicio;
import com.fitlifespa.microservice_servicios.security.RoleValidator;
import com.fitlifespa.microservice_servicios.service.ServicioService;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;
    private final RoleValidator validator;

    @PostMapping
    public ResponseEntity<Servicio> crear(@RequestBody Servicio servicio, HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        return ResponseEntity.ok(servicioService.crear(servicio.getNombre(), servicio.getDescripcion()));
    }

    @GetMapping
    public ResponseEntity<List<Servicio>> listar(HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        return ResponseEntity.ok(servicioService.listarTodos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Servicio>> listarActivos() {
        return ResponseEntity.ok(servicioService.listarActivos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servicio> actualizar(@PathVariable Long id, @RequestBody Servicio servicio, HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        return ResponseEntity.ok(servicioService.actualizar(id, servicio.getNombre(), servicio.getDescripcion()));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Servicio> desactivarServicio(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        try{
            return ResponseEntity.ok(servicioService.desactivarServicio(id));

        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Servicio> activarServicio(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        try{
            return ResponseEntity.ok(servicioService.activarServicio(id));
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servicio> obtener(@PathVariable Long id, HttpServletRequest request) {
        return servicioService.obtenerPorId(id)
                .map(servicio -> ResponseEntity.ok(servicio))
                .orElse(ResponseEntity.notFound().build());
    }


}

