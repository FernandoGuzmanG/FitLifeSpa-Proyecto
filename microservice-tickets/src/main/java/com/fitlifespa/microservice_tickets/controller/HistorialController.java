package com.fitlifespa.microservice_tickets.controller;

import com.fitlifespa.microservice_tickets.model.Historial;
import com.fitlifespa.microservice_tickets.security.RoleValidator;
import com.fitlifespa.microservice_tickets.service.HistorialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {
    @Autowired
    private HistorialService historialService;
    @Autowired
    private RoleValidator roleValidator;

    @Operation(
            summary = "Responder en el historial de un ticket",
            description = "Este endpoint permite a un CLIENTE o SOPORTE agregar una entrada al historial de mensajes de un ticket determinado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Mensaje agregado exitosamente al historial",
                    content = @Content(schema = @Schema(implementation = Historial.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @PostMapping("/{idTicket}/responder")
    public ResponseEntity<EntityModel<Historial>> responder(
            @Parameter(description = "ID del ticket al cual se agregará el mensaje", example = "5", required = true)
            @PathVariable Long idTicket,

            @Parameter(description = "Mensaje a registrar en el historial", example = "Ya resolví el problema", required = true)
            @RequestParam String mensaje,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        String rol = request.getHeader("X-User-Roles");
        roleValidator.requireRole(request, "CLIENTE", "SOPORTE");

        String tipo = rol.contains("SOPORTE") ? "SOPORTE" : "CLIENTE";

        Historial nuevo = historialService.agregarEntrada(idTicket, tipo, mensaje);

        EntityModel<Historial> historialModel = EntityModel.of(nuevo,
                linkTo(methodOn(HistorialController.class).historial(idTicket, null)).withRel("ver_historial")
        );

        return ResponseEntity.ok(historialModel);
    }


    @Operation(
            summary = "Obtener historial de un ticket",
            description = "Este endpoint permite a un CLIENTE o SOPORTE obtener la lista de mensajes registrados en el historial de un ticket."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Historial del ticket obtenido exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Historial.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "El ticket no tiene historial registrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/{idTicket}")
    public ResponseEntity<CollectionModel<EntityModel<Historial>>> historial(
            @Parameter(description = "ID del ticket del cual se desea obtener el historial", example = "5", required = true)
            @PathVariable Long idTicket,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "CLIENTE", "SOPORTE");

        List<Historial> historial = historialService.listarPorTicket(idTicket);

        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Historial>> historialModels = historial.stream()
                .map(EntityModel::of)
                .toList();

        CollectionModel<EntityModel<Historial>> collectionModel = CollectionModel.of(historialModels,
                linkTo(methodOn(HistorialController.class).responder(idTicket, null, null)).withRel("responder")
        );

        return ResponseEntity.ok(collectionModel);
    }

}
