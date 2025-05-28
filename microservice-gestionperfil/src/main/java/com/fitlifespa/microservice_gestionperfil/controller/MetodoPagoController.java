package com.fitlifespa.microservice_gestionperfil.controller;

import com.fitlifespa.microservice_gestionperfil.model.MetodoPago;
import com.fitlifespa.microservice_gestionperfil.security.RoleValidator;
import com.fitlifespa.microservice_gestionperfil.service.MetodoPagoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfil/metodos-pago")
@RequiredArgsConstructor
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;
    private final RoleValidator validator;

    @GetMapping
    public ResponseEntity<List<MetodoPago>> listar(HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        List<MetodoPago> metodos = metodoPagoService.listarPorUsuario(idUsuario);
        return ResponseEntity.ok(metodos);
    }

    @PostMapping
    public ResponseEntity<MetodoPago> agregar(@RequestBody MetodoPago metodo, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        MetodoPago guardado = metodoPagoService.agregar(idUsuario, metodo);
        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MetodoPago> actualizar(@PathVariable Long id, @RequestBody MetodoPago metodo, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        return metodoPagoService.actualizar(idUsuario, id, metodo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, HttpServletRequest request) {
        Long idUsuario = validator.getUserId(request);
        validator.requireRole(request, "CLIENTE");

        boolean eliminado = metodoPagoService.eliminar(idUsuario, id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

