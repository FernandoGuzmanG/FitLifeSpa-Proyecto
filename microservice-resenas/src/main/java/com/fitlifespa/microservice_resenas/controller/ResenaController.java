package com.fitlifespa.microservice_resenas.controller;

import com.fitlifespa.microservice_resenas.model.Resena;
import com.fitlifespa.microservice_resenas.security.RoleValidator;
import com.fitlifespa.microservice_resenas.service.ResenaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;
    private final RoleValidator validator;

    @PostMapping
    public ResponseEntity<Resena> crear(@RequestBody Resena resena, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(resenaService.crear(idUsuario, resena.getIdServicio(), resena.getComentario(), resena.getCalificacion()));
    }

    @GetMapping("/mis")
    public ResponseEntity<List<Resena>> misResenas(HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(resenaService.obtenerPorUsuario(idUsuario));
    }

    @GetMapping("/servicio/{idServicio}/promedio")
    public ResponseEntity<Double> promedioPorServicio(@PathVariable Long idServicio, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");
        return ResponseEntity.ok(resenaService.obtenerPromedioCalificacionPorServicio(idServicio));
    }

    @GetMapping("/servicio/{idServicio}")
    public ResponseEntity<List<Resena>> porServicio(@PathVariable Long idServicio, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR", "COORDINADOR", "CLIENTE", "SOPORTE", "ADMINISTRADOR");
        return ResponseEntity.ok(resenaService.obtenerPorServicio(idServicio));
    }
}

