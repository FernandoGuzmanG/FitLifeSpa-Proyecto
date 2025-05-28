package com.fitlifespa.microservice_membresias.controller;

import com.fitlifespa.microservice_membresias.model.EstadoReservaHistorial;
import com.fitlifespa.microservice_membresias.model.Reserva;
import com.fitlifespa.microservice_membresias.security.RoleValidator;
import com.fitlifespa.microservice_membresias.service.ReservaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final RoleValidator validator;

    @PostMapping
    public ResponseEntity<Reserva> crear(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                         @RequestParam String descripcion,
                                         @RequestParam Long idServicio,
                                         @RequestParam Long idEntrenador,
                                         HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        return ResponseEntity.ok(reservaService.crear(fecha, descripcion, idUsuario, idServicio, idEntrenador));
    }

    @GetMapping("/mis")
    public ResponseEntity<List<Reserva>> misReservas(HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        return ResponseEntity.ok(reservaService.listarPorUsuario(validator.getUserId(request)));
    }

    @GetMapping("/asignadas")
    public ResponseEntity<List<Reserva>> asignadas(HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(reservaService.listarPorEntrenador(validator.getUserId(request)));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Reserva> cancelar(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        return ResponseEntity.ok(reservaService.cambiarEstado(id, "CANCELADA", "Cancelado por cliente"));
    }

    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Reserva> confirmar(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(reservaService.cambiarEstado(id, "CONFIRMADA", "Confirmado por entrenador"));
    }

    @PutMapping("/{id}/completar")
    public ResponseEntity<Reserva> completar(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        return ResponseEntity.ok(reservaService.cambiarEstado(id, "COMPLETADA", "Servicio completado"));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<EstadoReservaHistorial>> historial(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");
        return ResponseEntity.ok(reservaService.historial(id));
    }

    @GetMapping("/ocupacion")
    public ResponseEntity<List<Object[]>> ocupacion(HttpServletRequest request) {
        validator.requireRole(request, "COORDINADOR");
        return ResponseEntity.ok(reservaService.conteoReservasPorServicio());
    }

    @GetMapping("/ocupacion/fecha")
    public ResponseEntity<List<Object[]>> ocupacionPorFecha(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
                                                            HttpServletRequest request) {
        validator.requireRole(request, "COORDINADOR");
        return ResponseEntity.ok(reservaService.conteoReservasPorFecha(fecha));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<Object[]>> ranking(HttpServletRequest request) {
        validator.requireRole(request, "COORDINADOR");
        return ResponseEntity.ok(reservaService.topServicios());
    }
}

