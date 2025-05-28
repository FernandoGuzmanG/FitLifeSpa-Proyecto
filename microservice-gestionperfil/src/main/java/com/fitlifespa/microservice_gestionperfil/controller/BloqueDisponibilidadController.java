package com.fitlifespa.microservice_gestionperfil.controller;

import com.fitlifespa.microservice_gestionperfil.model.BloqueDisponibilidad;
import com.fitlifespa.microservice_gestionperfil.security.RoleValidator;
import com.fitlifespa.microservice_gestionperfil.service.BloqueDisponibilidadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfil/disponibilidad")
@RequiredArgsConstructor
public class BloqueDisponibilidadController {

    private final BloqueDisponibilidadService service;
    private final RoleValidator validator;

    @GetMapping
    public ResponseEntity<List<BloqueDisponibilidad>> listar(HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(service.listarPorEntrenador(idUsuario));
    }

    @PostMapping
    public ResponseEntity<BloqueDisponibilidad> crear(@RequestBody BloqueDisponibilidad bloque, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(service.agregar(idUsuario, bloque));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BloqueDisponibilidad> actualizar(@PathVariable Long id, @RequestBody BloqueDisponibilidad bloque, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "ENTRENADOR");

        return service.actualizar(idUsuario, id, bloque)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "ENTRENADOR");

        boolean eliminado = service.eliminar(idUsuario, id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/entrenador/{idEntrenador}")
    public ResponseEntity<List<BloqueDisponibilidad>> obtenerDisponibilidadEntrenador(@PathVariable Long idEntrenador, HttpServletRequest request) {
        validator.requireRole(request, "COORDINADOR");
        List<BloqueDisponibilidad> disponibilidad = service.listarPorEntrenador(idEntrenador);
        return ResponseEntity.ok(disponibilidad);
    }

}
