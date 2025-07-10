package com.fitlifespa.microservice_clases.controller;

import com.fitlifespa.microservice_clases.model.Inscripcion;
import com.fitlifespa.microservice_clases.security.RoleValidator;
import com.fitlifespa.microservice_clases.service.InscripcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final RoleValidator validator;

    @Operation(
        summary = "Inscribirse a una clase",
        description = "Permite a un usuario con rol CLIENTE inscribirse a una clase existente utilizando su ID de clase.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inscripción realizada correctamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inscripcion.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: el usuario no tiene el rol CLIENTE"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/clase/{idClase}")
    public ResponseEntity<?> inscribirse(
        @Parameter(description = "ID de la clase a la que se desea inscribir", example = "1")
        @PathVariable Long idClase,

        @Parameter(hidden = true) HttpServletRequest request) {

        validator.requireRole(request, "CLIENTE");
        try {
            Long idUsuario = validator.getUserId(request);
            Inscripcion claseInscrita = inscripcionService.inscribirse(idUsuario, idClase);
            return ResponseEntity.status(HttpStatus.CREATED).body(claseInscrita);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al inscribirse a la clase. Verifica los datos.");
        }
    }

    @Operation(
        summary = "Obtener mis inscripciones",
        description = "Devuelve una lista de todas las clases a las que el usuario autenticado (rol CLIENTE) está inscrito.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inscripciones obtenida correctamente",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Inscripcion.class)))),
        @ApiResponse(responseCode = "204", description = "El usuario no tiene inscripciones"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: el usuario no tiene el rol CLIENTE"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/mis")
    public ResponseEntity<List<Inscripcion>> misInscripciones(
        @Parameter(hidden = true) HttpServletRequest request) {

        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        List<Inscripcion> inscripciones = inscripcionService.inscripcionesDeUsuario(idUsuario);
        if (inscripciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(inscripciones);
    }

    @Operation(
        summary = "Cancelar inscripción a una clase",
        description = "Permite a un usuario con rol CLIENTE cancelar una inscripción existente mediante su ID.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripción cancelada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Inscripcion.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: el usuario no tiene el rol CLIENTE"),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(
        @Parameter(description = "ID de la inscripción que se desea cancelar", example = "1")
        @PathVariable Long id,

        @Parameter(hidden = true) HttpServletRequest request) {

        validator.requireRole(request, "CLIENTE");
        try {
            Inscripcion inscripcionCancelada = inscripcionService.cancelarInscripcion(id);
            return ResponseEntity.ok(inscripcionCancelada);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
        summary = "Listar usuarios inscritos en una clase",
        description = "Permite a un usuario con rol ENTRENADOR o COORDINADOR obtener la lista de inscripciones activas de una clase específica.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inscripciones obtenida correctamente",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Inscripcion.class)))),
        @ApiResponse(responseCode = "204", description = "La clase no tiene inscripciones activas"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: rol no autorizado"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/clase/{idClase}/inscritos")
    public ResponseEntity<List<Inscripcion>> inscritos(
        @Parameter(description = "ID de la clase para consultar inscripciones", example = "2")
        @PathVariable Long idClase,

        @Parameter(hidden = true) HttpServletRequest request) {

        validator.requireRole(request, "ENTRENADOR", "COORDINADOR");
        List<Inscripcion> inscripciones = inscripcionService.inscritosEnClase(idClase);
        if (inscripciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(inscripciones);
    }

    @Operation(
        summary = "Obtener historial de clases finalizadas",
        description = "Permite a un usuario con rol CLIENTE obtener la lista de inscripciones con estado FINALIZADO.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Inscripcion.class)))),
        @ApiResponse(responseCode = "204", description = "No hay clases finalizadas en el historial"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: el usuario no tiene el rol CLIENTE"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/historial")
    public ResponseEntity<List<Inscripcion>> historial(
        @Parameter(hidden = true) HttpServletRequest request) {

        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        List<Inscripcion> inscripciones = inscripcionService.historialFinalizadas(idUsuario);
        if (inscripciones.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(inscripciones);
    }
}
