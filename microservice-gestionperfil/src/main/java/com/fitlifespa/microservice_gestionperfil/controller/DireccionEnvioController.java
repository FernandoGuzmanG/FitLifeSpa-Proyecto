package com.fitlifespa.microservice_gestionperfil.controller;

import com.fitlifespa.microservice_gestionperfil.model.DireccionEnvio;
import com.fitlifespa.microservice_gestionperfil.security.RoleValidator;
import com.fitlifespa.microservice_gestionperfil.service.DireccionEnvioService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfil/direcciones")
@RequiredArgsConstructor
public class DireccionEnvioController {

    private final DireccionEnvioService direccionEnvioService;
    private final RoleValidator validator;

    @GetMapping
    public ResponseEntity<List<DireccionEnvio>> listarDirecciones(HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        List<DireccionEnvio> direcciones = direccionEnvioService.listarPorUsuario(idUsuario);
        return ResponseEntity.ok(direcciones);
    }

    @PostMapping
    public ResponseEntity<DireccionEnvio> agregarDireccion(@RequestBody DireccionEnvio direccion,
                                                           HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        DireccionEnvio guardada = direccionEnvioService.agregar(idUsuario, direccion);
        return ResponseEntity.ok(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DireccionEnvio> actualizarDireccion(@PathVariable Long id,
                                                              @RequestBody DireccionEnvio direccion,
                                                              HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        return direccionEnvioService.actualizar(idUsuario, id, direccion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long id, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        boolean eliminada = direccionEnvioService.eliminar(idUsuario, id);
        return eliminada ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

