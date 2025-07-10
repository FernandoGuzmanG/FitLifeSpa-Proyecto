package com.fitlifespa.microservice_membresias.controller;

import com.fitlifespa.microservice_membresias.model.Plan;
import com.fitlifespa.microservice_membresias.security.RoleValidator;
import com.fitlifespa.microservice_membresias.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/planes")
public class PlanController {
    @Autowired
    private RoleValidator roleValidator;

    @Autowired
    private PlanService planService;

    @Operation(
            summary = "Listar todos los planes disponibles",
            description = "Este endpoint permite obtener una lista de todos los planes de suscripción disponibles en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de planes obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Plan.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay planes disponibles"
            )
    })
    @GetMapping
    public ResponseEntity<List<Plan>> listarPlanes() {
        List<Plan> planes = planService.findAll();
        if (planes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(planes);
    }


    @Operation(
            summary = "Obtener un plan por su ID",
            description = "Este endpoint permite obtener los detalles de un plan de suscripción específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plan encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Plan.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Plan no encontrado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Plan> obtenerPlan(@PathVariable Long id) {
        return planService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Crear un nuevo plan de suscripción",
            description = "Este endpoint permite a un usuario con rol ADMINISTRADOR registrar un nuevo plan de membresía.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto Plan con los datos del nuevo plan a crear",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Plan.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Plan creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Plan.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Error al crear el plan (posiblemente por datos inválidos o conflicto interno)",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping
    public ResponseEntity<Plan> crearPlan(@RequestBody Plan plan, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Plan nuevoPlan = planService.crearPlan(plan);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPlan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @Operation(
            summary = "Actualizar un plan existente",
            description = "Este endpoint permite a un usuario con rol ADMINISTRADOR actualizar los datos de un plan de suscripción.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto Plan con los nuevos datos",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Plan.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plan actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Plan.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El plan no fue encontrado",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPlan(@PathVariable Long id, @RequestBody Plan plan, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Plan actualizado = planService.actualizarPlan(id, plan);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Plan no encontrado"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al actualizar el plan"));
        }
    }


}

