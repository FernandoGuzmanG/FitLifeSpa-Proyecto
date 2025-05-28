package com.fitlifespa.microservice_clases.controller;

import com.fitlifespa.microservice_clases.dto.CrearClaseRequest;
import com.fitlifespa.microservice_clases.security.RoleValidator;
import com.fitlifespa.microservice_clases.model.Clase;
import com.fitlifespa.microservice_clases.service.ClaseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases")
@RequiredArgsConstructor
public class ClaseController {

    private final ClaseService claseService;
    private final RoleValidator validator;

    @PostMapping
    public ResponseEntity<Clase> crearClase(@RequestBody CrearClaseRequest requestBody, HttpServletRequest request) {
        validator.requireRole(request, "COORDINADOR");
        return ResponseEntity.ok(
                claseService.crearClase(
                        requestBody.getNombre(),
                        requestBody.getDescripcion(),
                        requestBody.getIdServicio(),
                        requestBody.getIdEntrenador(),
                        requestBody.getFecha()
                )
        );
    }

    @PutMapping("/{id}/entrenador/{nuevoIdEntrenador}")
    public ResponseEntity<Clase> cambiarEntrenador(@PathVariable Long id, @PathVariable Long nuevoIdEntrenador, HttpServletRequest request) {
        validator.requireRole(request, "COORDINADOR");
        Clase clase = claseService.buscarPorId(id).orElseThrow();
        clase.setIdUsuario(nuevoIdEntrenador);
        return ResponseEntity.ok(claseService.actualizar(clase));
    }

    @PutMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<Clase> cambiarEstado(@PathVariable Long id, @PathVariable String nuevoEstado, HttpServletRequest request) {
        validator.requireRole(request, "COORDINADOR");
        return ResponseEntity.ok(claseService.cambiarEstado(id, nuevoEstado));
    }

    @GetMapping("/mias")
    public ResponseEntity<List<Clase>> clasesDelEntrenador(HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        Long idEntrenador = validator.getUserId(request);
        return ResponseEntity.ok(claseService.listarPorEntrenador(idEntrenador));
    }
}
