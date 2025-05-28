package com.fitlifespa.microservice_clases.controller;

import com.fitlifespa.microservice_clases.model.Inscripcion;
import com.fitlifespa.microservice_clases.security.RoleValidator;
import com.fitlifespa.microservice_clases.service.InscripcionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final RoleValidator validator;

    // CLIENTE: Inscribirse a una clase
    @PostMapping("/clase/{idClase}")
    public ResponseEntity<Inscripcion> inscribirse(@PathVariable Long idClase,
                                                   HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(inscripcionService.inscribirse(idUsuario, idClase));
    }

    // CLIENTE: Ver mis inscripciones activas
    @GetMapping("/mis")
    public ResponseEntity<List<Inscripcion>> misInscripciones(HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(inscripcionService.inscripcionesDeUsuario(idUsuario));
    }

    // CLIENTE: Cancelar una inscripción
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Inscripcion> cancelar(@PathVariable Long id,
                                                HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        return ResponseEntity.ok(inscripcionService.cancelarInscripcion(id));
    }

    // ENTRENADOR / COORDINADOR: Ver inscritos en una clase
    @GetMapping("/clase/{idClase}/inscritos")
    public ResponseEntity<List<Inscripcion>> inscritos(@PathVariable Long idClase,
                                                       HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR", "COORDINADOR");
        return ResponseEntity.ok(inscripcionService.inscritosEnClase(idClase));
    }

    // CLIENTE: Ver historial (clases finalizadas)
    @GetMapping("/historial")
    public ResponseEntity<List<Inscripcion>> historial(HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(inscripcionService.historialFinalizadas(idUsuario));
    }
}

