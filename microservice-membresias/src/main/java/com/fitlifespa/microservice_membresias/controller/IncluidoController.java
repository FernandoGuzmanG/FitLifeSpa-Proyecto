package com.fitlifespa.microservice_membresias.controller;

import com.fitlifespa.microservice_membresias.model.Incluido;
import com.fitlifespa.microservice_membresias.security.RoleValidator;
import com.fitlifespa.microservice_membresias.service.IncluidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/incluidos")
public class IncluidoController {
    @Autowired
    private RoleValidator roleValidator;

    @Autowired
    private IncluidoService incluidoService;

    @Operation(
            summary = "Listar servicios incluidos en un plan",
            description = "Este endpoint devuelve la lista de servicios asociados a un plan específico mediante su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de servicios incluidos obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EntityModel.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay servicios incluidos en el plan"
            )
    })
    @GetMapping("/plan/{idPlan}")
    public ResponseEntity<CollectionModel<EntityModel<Incluido>>> listarPorPlan(
            @PathVariable Long idPlan,
            HttpServletRequest request) {

        List<Incluido> incluidos = incluidoService.listarPorPlan(idPlan);

        if (incluidos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Incluido>> modelos = incluidos.stream().map(incluido -> {
            EntityModel<Incluido> model = EntityModel.of(incluido);

            model.add(linkTo(methodOn(IncluidoController.class)
                    .eliminarIncluido(incluido.getIdIncluido(), null))
                    .withRel("eliminar"));

            return model;
        }).toList();

        CollectionModel<EntityModel<Incluido>> collectionModel = CollectionModel.of(modelos);
        collectionModel.add(linkTo(methodOn(IncluidoController.class)
                .crearIncluido(null, null))
                .withRel("crear"));

        return ResponseEntity.ok(collectionModel);
    }


    @Operation(
            summary = "Crear un nuevo servicio incluido en un plan",
            description = "Este endpoint permite a un ADMINISTRADOR agregar un nuevo servicio a un plan mediante su ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto Incluido con el ID del plan y del servicio a asociar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Incluido.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Incluido creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntityModel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Error al crear el recurso (plan inexistente o error de datos)"
            )
    })
    @PostMapping
    public ResponseEntity<?> crearIncluido(@RequestBody Incluido incluido, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");

        try {
            Incluido nuevoIncluido = incluidoService.save(incluido);
            EntityModel<Incluido> model = EntityModel.of(nuevoIncluido);

            // Link para listar todos los incluidos de este plan
            model.add(linkTo(methodOn(IncluidoController.class)
                    .listarPorPlan(nuevoIncluido.getPlan().getIdPlan(), null))
                    .withRel("listar-por-plan"));

            // Link para eliminar este incluido
            model.add(linkTo(methodOn(IncluidoController.class)
                    .eliminarIncluido(nuevoIncluido.getIdIncluido(), null))
                    .withRel("eliminar"));

            return ResponseEntity.status(HttpStatus.CREATED).body(model);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Error al crear el servicio incluido"));
        }
    }


    @Operation(
            summary = "Eliminar un servicio incluido de un plan",
            description = "Este endpoint permite a un ADMINISTRADOR eliminar un servicio previamente incluido en un plan mediante su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Servicio incluido eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "El servicio incluido no fue encontrado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIncluido(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");

        try {
            incluidoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}

