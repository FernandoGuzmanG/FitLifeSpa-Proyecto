package com.fitlifespa.microservice_reservas.controller;

import com.fitlifespa.microservice_reservas.model.EstadoReservaHistorial;
import com.fitlifespa.microservice_reservas.model.Reserva;
import com.fitlifespa.microservice_reservas.security.RoleValidator;
import com.fitlifespa.microservice_reservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;
    private final RoleValidator validator;

    @Operation(
        summary = "Crear una nueva reserva",
        description = "Permite a un usuario con rol CLIENTE crear una reserva para un servicio con un entrenador en una fecha específica.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva creada correctamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Recurso no encontrado")
    })
    @PostMapping
    public ResponseEntity<Reserva> crear(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
        @RequestParam String descripcion,
        @RequestParam Long idServicio,
        @RequestParam Long idEntrenador,
        HttpServletRequest request) {

        validator.requireRole(request, "CLIENTE");
        try {
            Long idUsuario = validator.getUserId(request);
            Reserva nuevaReserva = reservaService.crear(fecha, descripcion, idUsuario, idServicio, idEntrenador);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Obtener mis reservas",
        description = "Retorna todas las reservas realizadas por el usuario autenticado con rol CLIENTE.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservas obtenidas correctamente",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Reserva.class)))),
        @ApiResponse(responseCode = "204", description = "El usuario no tiene reservas"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/mis")
    public ResponseEntity<List<Reserva>> misReservas(HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        List<Reserva> reservas = reservaService.listarPorUsuario(validator.getUserId(request));
        if (reservas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservas);
    }

    @Operation(
        summary = "Obtener reservas asignadas",
        description = "Permite a un ENTRENADOR ver todas las reservas que le han sido asignadas.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reservas obtenidas correctamente",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Reserva.class)))),
        @ApiResponse(responseCode = "204", description = "No hay reservas asignadas"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/asignadas")
    public ResponseEntity<List<Reserva>> asignadas(HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        List<Reserva> reservas = reservaService.listarPorEntrenador(validator.getUserId(request));
        if (reservas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservas);
    }

    @Operation(
        summary = "Cancelar una reserva",
        description = "Permite a un CLIENTE cancelar una reserva existente.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva cancelada correctamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        try {
            Reserva reservaCancelada = reservaService.cambiarEstado(id, "CANCELADA", "Cancelado por cliente");
            return ResponseEntity.ok(reservaCancelada);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Confirmar una reserva",
        description = "Permite a un ENTRENADOR confirmar la reserva que le fue asignada.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva confirmada correctamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Reserva> confirmar(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        try {
            Reserva reservaConfirmada = reservaService.cambiarEstado(id, "CONFIRMADA", "Confirmado por entrenador");
            return ResponseEntity.ok(reservaConfirmada);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Marcar una reserva como completada",
        description = "Permite a un ENTRENADOR marcar una reserva como COMPLETADA una vez prestado el servicio.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reserva marcada como completada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reserva.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PutMapping("/{id}/completar")
    public ResponseEntity<Reserva> completar(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        try {
            Reserva reservaCompletada = reservaService.cambiarEstado(id, "COMPLETADA", "Servicio completado");
            return ResponseEntity.ok(reservaCompletada);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Obtener historial de estados de una reserva",
        description = "Retorna el historial de cambios de estado de una reserva. Accesible por CLIENTE, ENTRENADOR, COORDINADOR o ADMINISTRADOR.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = EstadoReservaHistorial.class)))),
        @ApiResponse(responseCode = "204", description = "El historial está vacío"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<EstadoReservaHistorial>> historial(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");
        List<EstadoReservaHistorial> reservas = reservaService.historial(id);
        if (reservas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservas);
    }

    @Operation(
        summary = "Ocupación por servicio",
        description = "Devuelve estadísticas de ocupación por tipo de servicio. Solo accesible por COORDINADOR.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos de ocupación obtenidos correctamente"),
        @ApiResponse(responseCode = "204", description = "No hay datos de ocupación"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/ocupacion")
    public ResponseEntity<List<Object[]>> ocupacion(HttpServletRequest request) {
        validator.requireRole(request, "COORDINADOR");
        List<Object[]> reservas = reservaService.conteoReservasPorServicio();
        if (reservas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservas);
    }

    @Operation(
        summary = "Ocupación por fecha",
        description = "Devuelve la cantidad de reservas realizadas para una fecha específica.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos obtenidos correctamente"),
        @ApiResponse(responseCode = "204", description = "No hay datos para la fecha seleccionada"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/ocupacion/fecha")
    public ResponseEntity<List<Object[]>> ocupacionPorFecha(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
        HttpServletRequest request) {
        validator.requireRole(request, "COORDINADOR");
        List<Object[]> reservas = reservaService.conteoReservasPorFecha(fecha);
        if (reservas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservas);
    }

    @Operation(
        summary = "Ranking de servicios",
        description = "Muestra los servicios más solicitados por cantidad de reservas.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ranking generado correctamente"),
        @ApiResponse(responseCode = "204", description = "No hay datos para el ranking"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @GetMapping("/ranking")
    public ResponseEntity<List<Object[]>> ranking(HttpServletRequest request) {
        validator.requireRole(request, "COORDINADOR");
        List<Object[]> reservas = reservaService.topServicios();
        if (reservas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(reservas);
    }
}

