package com.fitlifespa.microservice_membresias.controller;

import com.fitlifespa.microservice_membresias.security.RoleValidator;
import com.fitlifespa.microservice_membresias.model.Membresia;
import com.fitlifespa.microservice_membresias.service.MembresiaService;
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
@RequestMapping("/api/membresias")
public class MembresiaController {
    @Autowired
    private RoleValidator roleValidator;

    @Autowired
    private MembresiaService membresiaService;

    @Operation(
            summary = "Listar todas las membresías",
            description = "Este endpoint permite al ADMINISTRADOR obtener una lista de todas las membresías registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de membresías obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EntityModel.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay membresías registradas"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido"
            )
    })

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Membresia>>> listarMembresias(HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");

        List<Membresia> membresias = membresiaService.findAll();

        if (membresias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Membresia>> modelos = membresias.stream().map(m -> {
            EntityModel<Membresia> model = EntityModel.of(m);
            model.add(linkTo(methodOn(MembresiaController.class).obtenerPorId(m.getIdMembresia(), request)).withSelfRel());
            model.add(linkTo(methodOn(PlanController.class).obtenerPlan(m.getPlan().getIdPlan())).withRel("plan"));
            return model;
        }).toList();

        CollectionModel<EntityModel<Membresia>> collection = CollectionModel.of(modelos);
        return ResponseEntity.ok(collection);
    }



    @Operation(
            summary = "Obtener una membresía por su ID",
            description = "Este endpoint permite al ADMINISTRADOR obtener una membresía específica por su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Membresía encontrada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntityModel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Membresía no encontrada"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");

        return membresiaService.findById(id)
                .map(membresia -> {
                    EntityModel<Membresia> model = EntityModel.of(membresia);

                    // Agregamos sólo el link al plan
                    model.add(linkTo(methodOn(PlanController.class)
                            .obtenerPlan(membresia.getPlan().getIdPlan()))
                            .withRel("plan"));

                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Renovar una membresía",
            description = "Este endpoint permite a un cliente renovar una membresía existente. La renovación crea una nueva membresía con el mismo plan y fechas actualizadas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Membresía renovada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntityModel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "La membresía no pertenece al usuario o la solicitud es inválida",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Membresía no encontrada",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno al renovar la membresía",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @PutMapping("/renovar")
    public ResponseEntity<?> renovarMembresia(
            @RequestParam Long idMembresia,
            @RequestParam Long idUsuario) {
        try {
            Membresia nuevaMembresia = membresiaService.renovarMembresia(idMembresia, idUsuario);

            EntityModel<Membresia> model = EntityModel.of(nuevaMembresia);

            model.add(linkTo(methodOn(PlanController.class)
                    .obtenerPlan(nuevaMembresia.getPlan().getIdPlan()))
                    .withRel("plan"));

            return ResponseEntity.ok(model);

        } catch (RuntimeException e) {
            String mensaje = e.getMessage();

            if (mensaje.contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Membresía no encontrada"));
            }

            if (mensaje.contains("no pertenece")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "La membresía no pertenece al usuario"));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al renovar la membresía"));
        }
    }


    @Operation(
            summary = "Listar las membresías del usuario autenticado",
            description = "Este endpoint permite a un usuario con rol CLIENTE listar todas sus membresías activas o pasadas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de membresías del usuario obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EntityModel.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "El usuario no tiene membresías registradas"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol requerido"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/mis-membresias")
    public ResponseEntity<CollectionModel<EntityModel<Membresia>>> listarMembresiasCliente(HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");
        Long idUsuario = Long.parseLong(request.getHeader("X-User-Id"));

        List<Membresia> membresias = membresiaService.findAllByIdUser(idUsuario);

        if (membresias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Membresia>> modelos = membresias.stream().map(m -> {
            EntityModel<Membresia> model = EntityModel.of(m);

            model.add(linkTo(methodOn(PlanController.class)
                    .obtenerPlan(m.getPlan().getIdPlan()))
                    .withRel("plan"));

            model.add(linkTo(methodOn(MembresiaController.class)
                    .renovarMembresia(m.getIdMembresia(), idUsuario))
                    .withRel("renovar"));

            model.add(linkTo(methodOn(MembresiaController.class)
                    .cambiarPlan(m.getIdMembresia(), null, null))
                    .withRel("cambiar-plan"));

            model.add(linkTo(methodOn(MembresiaController.class)
                    .cancelarMembresia(m.getIdMembresia(), null))
                    .withRel("cancelar"));

            return model;
        }).toList();

        CollectionModel<EntityModel<Membresia>> collectionModel = CollectionModel.of(modelos);
        collectionModel.add(linkTo(methodOn(MembresiaController.class)
                .crearMembresia(null, request))
                .withRel("crear"));

        return ResponseEntity.ok(collectionModel);
    }


    @Operation(
            summary = "Crear una nueva membresía",
            description = "Este endpoint permite a un usuario con rol CLIENTE crear una membresía seleccionando un plan disponible.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto Membresia con el ID del plan seleccionado y fechas iniciales (el ID de usuario se extrae del encabezado)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Membresia.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Membresía creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntityModel.class)
                    )
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
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<?> crearMembresia(@RequestBody Membresia membresia, HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");

        try {
            Long idUsuario = Long.parseLong(request.getHeader("X-User-Id"));
            Membresia nuevaMembresia = membresiaService.crearMembresia(membresia, idUsuario);

            EntityModel<Membresia> model = EntityModel.of(nuevaMembresia);

            model.add(linkTo(methodOn(MembresiaController.class)
                    .renovarMembresia(nuevaMembresia.getIdMembresia(), idUsuario))
                    .withRel("renovar"));

            model.add(linkTo(methodOn(MembresiaController.class)
                    .cambiarPlan(nuevaMembresia.getIdMembresia(), null, null)) // null como placeholder
                    .withRel("cambiar-plan"));

            model.add(linkTo(methodOn(MembresiaController.class)
                    .cancelarMembresia(nuevaMembresia.getIdMembresia(), null))
                    .withRel("cancelar"));

            return ResponseEntity.status(HttpStatus.CREATED).body(model);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Error al crear la membresía. Verifica si el plan existe."));
        }
    }


    @Operation(
            summary = "Renovar una membresía",
            description = "Este endpoint permite a un cliente renovar una membresía existente. La renovación crea una nueva membresía con el mismo plan y fechas actualizadas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Membresía renovada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntityModel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "La membresía no pertenece al usuario o la solicitud es inválida",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Membresía no encontrada",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno al renovar la membresía",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @PutMapping("/renovar/{idMembresia}")
    public ResponseEntity<?> renovarMembresia(@PathVariable Long idMembresia, HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");

        try {
            Long idUsuario = Long.parseLong(request.getHeader("X-User-Id"));
            Membresia nuevaMembresia = membresiaService.renovarMembresia(idMembresia, idUsuario);

            EntityModel<Membresia> model = EntityModel.of(nuevaMembresia);

            // HATEOAS links
            model.add(linkTo(methodOn(MembresiaController.class)
                    .cambiarPlan(nuevaMembresia.getIdMembresia(), null, null))
                    .withRel("cambiar-plan"));

            model.add(linkTo(methodOn(MembresiaController.class)
                    .cancelarMembresia(nuevaMembresia.getIdMembresia(), null))
                    .withRel("cancelar"));

            return ResponseEntity.ok(model);

        } catch (RuntimeException e) {
            String mensaje = e.getMessage();

            if (mensaje.contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Membresía no encontrada"));
            }

            if (mensaje.contains("no pertenece")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "La membresía no pertenece al usuario"));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al renovar la membresía"));
        }
    }



    @Operation(
            summary = "Cambiar el plan de una membresía",
            description = "Este endpoint permite a un usuario con rol CLIENTE cambiar el plan de una membresía activa."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Plan de la membresía cambiado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntityModel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida o la membresía no pertenece al usuario",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Membresía o nuevo plan no encontrado",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno al cambiar el plan",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @PutMapping("/cambiar-plan")
    public ResponseEntity<?> cambiarPlan(
            @RequestParam Long idMembresia,
            @RequestParam Long nuevoIdPlan,
            HttpServletRequest request) {

        roleValidator.requireRole(request, "CLIENTE");

        try {
            Long idUsuario = Long.parseLong(request.getHeader("X-User-Id"));
            Membresia actualizada = membresiaService.cambiarPlanMembresia(idMembresia, idUsuario, nuevoIdPlan);

            EntityModel<Membresia> model = EntityModel.of(actualizada);

            // HATEOAS links
            model.add(linkTo(methodOn(MembresiaController.class)
                    .renovarMembresia(actualizada.getIdMembresia(), idUsuario))
                    .withRel("renovar"));

            model.add(linkTo(methodOn(MembresiaController.class)
                    .cancelarMembresia(actualizada.getIdMembresia(), null))
                    .withRel("cancelar"));

            return ResponseEntity.ok(model);

        } catch (RuntimeException e) {
            String mensaje = e.getMessage();

            if (mensaje.contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Membresía no encontrada"));
            }

            if (mensaje.contains("no pertenece")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "La membresía no pertenece al usuario"));
            }

            if (mensaje.contains("expirada")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "No se puede cambiar el plan de una membresía expirada"));
            }

            if (mensaje.contains("no existe")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "El nuevo plan no existe"));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al cambiar el plan"));
        }
    }


    @Operation(
            summary = "Cancelar una membresía",
            description = "Este endpoint permite a un usuario con rol CLIENTE cancelar una membresía si no han pasado más de 7 días desde su inicio."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Membresía cancelada exitosamente (eliminada)"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "La membresía no pertenece al usuario o ya no puede cancelarse",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Membresía no encontrada",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno al cancelar la membresía",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @DeleteMapping("/cancelar/{idMembresia}")
    public ResponseEntity<?> cancelarMembresia(@PathVariable Long idMembresia, HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE");

        try {
            Long idUsuario = Long.parseLong(request.getHeader("X-User-Id"));
            membresiaService.cancelarMembresia(idMembresia, idUsuario);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            String mensaje = e.getMessage();

            if (mensaje.contains("no encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Membresía no encontrada"));
            }

            if (mensaje.contains("no pertenece")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "La membresía no pertenece al usuario"));
            }

            if (mensaje.contains("cancelada") || mensaje.contains("7 días")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "La membresía ya no puede ser cancelada (más de 7 días desde su inicio)"));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno al cancelar la membresía"));
        }
    }



}

