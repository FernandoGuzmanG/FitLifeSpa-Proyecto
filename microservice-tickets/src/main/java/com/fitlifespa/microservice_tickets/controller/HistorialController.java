package com.fitlifespa.microservice_tickets.controller;

import com.fitlifespa.microservice_tickets.model.Historial;
import com.fitlifespa.microservice_tickets.security.RoleValidator;
import com.fitlifespa.microservice_tickets.service.HistorialService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {
    @Autowired
    private HistorialService historialService;
    @Autowired
    private RoleValidator roleValidator;

    @PostMapping("/{idTicket}/responder")
    public ResponseEntity<Historial> responder(
            @PathVariable Long idTicket,
            @RequestParam String mensaje,
            HttpServletRequest request) {

        String rol = request.getHeader("X-User-Roles");
        roleValidator.requireRole(request, "CLIENTE", "SOPORTE");

        String tipo = rol.contains("SOPORTE") ? "SOPORTE" : "CLIENTE";
        return ResponseEntity.ok(historialService.agregarEntrada(idTicket, tipo, mensaje));
    }

    @GetMapping("/{idTicket}")
    public ResponseEntity<List<Historial>> historial(@PathVariable Long idTicket, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        List<Historial> historial = historialService.listarPorTicket(idTicket);
        if (historial.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(historial);
    }
}
