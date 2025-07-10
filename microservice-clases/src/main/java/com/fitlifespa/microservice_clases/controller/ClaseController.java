package com.fitlifespa.microservice_clases.controller;

import com.fitlifespa.microservice_clases.dto.CrearClaseRequest;
import com.fitlifespa.microservice_clases.security.RoleValidator;
import com.fitlifespa.microservice_clases.model.Clase;
import com.fitlifespa.microservice_clases.service.ClaseService;
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
@RequestMapping("/api/clases")
@RequiredArgsConstructor
public class ClaseController {

    private final ClaseService claseService;
    private final RoleValidator validator;

    @Operation(
        summary = "Crear una nueva clase",
        description = "Permite a un usuario con rol COORDINADOR registrar una nueva clase especificando nombre, descripción, servicio relacionado, entrenador asignado y fecha programada.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Clase creada exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Clase.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: el usuario no tiene el rol COORDINADOR"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida: datos faltantes o incorrectos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<?> crearClase(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos necesarios para registrar la clase",
            required = true,
            content = @Content(schema = @Schema(implementation = CrearClaseRequest.class))
        )
        @RequestBody CrearClaseRequest requestBody,

        @Parameter(hidden = true) HttpServletRequest request) {

        validator.requireRole(request, "COORDINADOR");
        try {
            Clase claseNueva = claseService.crearClase(
                requestBody.getNombre(),
                requestBody.getDescripcion(),
                requestBody.getIdServicio(),
                requestBody.getIdEntrenador(),
                requestBody.getFecha()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(claseNueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: No se pudo registrar la clase. Verifica los datos.");
        }
    }

    @Operation(
        summary = "Actualizar entrenador de una clase",
        description = "Permite a un usuario con rol COORDINADOR cambiar el entrenador responsable de una clase existente.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entrenador actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Clase.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: el usuario no tiene el rol COORDINADOR"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}/entrenador/{nuevoIdEntrenador}")
    public ResponseEntity<?> cambiarEntrenador(
        @Parameter(description = "ID de la clase que se desea actualizar", example = "1")
        @PathVariable Long id,

        @Parameter(description = "ID del nuevo entrenador que se asignará", example = "15")
        @PathVariable Long nuevoIdEntrenador,

        @Parameter(hidden = true) HttpServletRequest request) {

        validator.requireRole(request, "COORDINADOR");
        try {
            Clase clase = claseService.actualizarEntrenador(id, nuevoIdEntrenador);
            return ResponseEntity.ok(clase);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Cambiar estado de una clase",
        description = "Permite a un usuario con rol COORDINADOR modificar el estado actual de una clase. Estados válidos: PROGRAMADA, CANCELADA, FINALIZADA, etc.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de la clase actualizado exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Clase.class))),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: el usuario no tiene el rol COORDINADOR"),
        @ApiResponse(responseCode = "404", description = "Clase no encontrada o estado inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}/estado/{nuevoEstado}")
    public ResponseEntity<?> cambiarEstado(
        @Parameter(description = "ID de la clase que se desea actualizar", example = "3")
        @PathVariable Long id,

        @Parameter(description = "Nuevo estado que se asignará (ejemplo: CANCELADA)", example = "CANCELADA")
        @PathVariable String nuevoEstado,

        @Parameter(hidden = true) HttpServletRequest request) {

        validator.requireRole(request, "COORDINADOR");
        try {
            Clase claseActualizada = claseService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(claseActualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @Operation(
        summary = "Listar clases asignadas al entrenador autenticado",
        description = "Permite a un usuario con rol ENTRENADOR obtener todas las clases que tiene asignadas.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clases obtenida correctamente",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = Clase.class)))),
        @ApiResponse(responseCode = "204", description = "No hay clases asignadas"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado: el usuario no tiene el rol ENTRENADOR"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/mias")
    public ResponseEntity<List<Clase>> clasesDelEntrenador(@Parameter(hidden = true) HttpServletRequest request) {
        validator.requireRole(request, "ENTRENADOR");
        Long idEntrenador = validator.getUserId(request);
        List<Clase> clasesPorEntrenador = claseService.listarPorEntrenador(idEntrenador);
        if (clasesPorEntrenador.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(clasesPorEntrenador);
    }
}

