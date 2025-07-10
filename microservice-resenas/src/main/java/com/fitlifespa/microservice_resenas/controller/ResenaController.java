package com.fitlifespa.microservice_resenas.controller;

import com.fitlifespa.microservice_resenas.hateoas.ResenaModelAssembler;
import com.fitlifespa.microservice_resenas.hateoas.ResenaConPromedioModelAssembler;
import com.fitlifespa.microservice_resenas.model.Enum.EstadoResena;
import com.fitlifespa.microservice_resenas.model.Resena;
import com.fitlifespa.microservice_resenas.security.RoleValidator;
import com.fitlifespa.microservice_resenas.service.ResenaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.RepresentationModel;
import io.swagger.v3.oas.annotations.parameters.RequestBody;


import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Operaciones relacionadas con reseñas de servicios")
public class ResenaController {

    private final ResenaService resenaService;
    private final RoleValidator validator;
    private final ResenaModelAssembler resenaModelAssembler;
    private final ResenaConPromedioModelAssembler resenaConPromedioModelAssembler;

    @Operation(
            summary = "Crear una nueva reseña",
            description = "Este endpoint permite a un usuario con rol CLIENTE crear una reseña para un servicio determinado.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Objeto Resena con los datos de la nueva reseña",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Resena.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Reseña creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Resena.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error en la solicitud (datos inválidos o faltantes)",
                    content = @Content(mediaType = "application/json")
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
    @PostMapping
    public ResponseEntity<?> crearResena(
            @org.springframework.web.bind.annotation.RequestBody Resena resena,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        try {
            Resena nueva = resenaService.crear(
                    idUsuario,
                    resena.getIdServicio(),
                    resena.getComentario(),
                    resena.getCalificacion()
            );
            EntityModel<Resena> model = resenaModelAssembler.toModel(nueva);
            return ResponseEntity.status(HttpStatus.CREATED).body(model);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



    @Operation(
            summary = "Obtener reseñas del usuario autenticado",
            description = "Devuelve una lista de reseñas asociadas al usuario autenticado con rol CLIENTE. "
                    + "Si no hay reseñas, devuelve un mensaje con estado 204 (sin contenido)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de reseñas encontradas",
                    content = @Content(
                            mediaType = "application/json",
                            array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                                    schema = @Schema(implementation = Resena.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay reseñas registradas para este usuario",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol CLIENTE",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/mis")
    public ResponseEntity<?> misResenas(@Parameter(hidden = true) HttpServletRequest request) {
        validator.requireRole(request, "CLIENTE");
        Long idUsuario = validator.getUserId(request);
        List<Resena> resenas = resenaService.obtenerPorUsuario(idUsuario);

        if (resenas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("mensaje", "No tienes reseñas registradas."));
        }

        List<EntityModel<Resena>> modelos = resenas.stream()
                .map(resenaModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Resena>> modeloFinal = CollectionModel.of(modelos,
                linkTo(methodOn(ResenaController.class).misResenas(null)).withSelfRel(),
                linkTo(methodOn(ResenaController.class).crearResena(null, null)).withRel("crear")
        );

        return ResponseEntity.ok(modeloFinal);
    }




    @Operation(
            summary = "Obtener el promedio de calificaciones de un servicio",
            description = "Calcula y devuelve el promedio de calificaciones de todas las reseñas **no baneadas** de un servicio específico. "
                    + "Disponible para CLIENTE, ENTRENADOR, COORDINADOR y ADMINISTRADOR."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Promedio de calificaciones calculado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Object.class,
                                    example = "{\"promedio\": 4.2}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay calificaciones disponibles para este servicio",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el rol del usuario no está autorizado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/servicio/{idServicio}/promedio")
    public ResponseEntity<?> promedioPorServicio(
            @PathVariable Long idServicio,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        validator.requireRole(request, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");

        Double promedio = resenaService.obtenerPromedioCalificacionPorServicio(idServicio, EstadoResena.DESBANEADO);

        if (promedio == 0.0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("mensaje", "Este servicio aún no tiene calificaciones."));
        }

        Map<String, Object> datos = Map.of("promedio", promedio);

        RepresentationModel<?> model = new RepresentationModel<>()
                .add(linkTo(methodOn(ResenaController.class).promedioPorServicio(idServicio, null)).withSelfRel())
                .add(linkTo(methodOn(ResenaController.class).porServicio(idServicio, null)).withRel("ver_reseñas"));

        return ResponseEntity.ok(Map.of(
                "data", datos,
                "_links", model.getLinks()
        ));
    }




    @Operation(
            summary = "Obtener reseñas de un servicio",
            description = "Devuelve una lista de reseñas **no baneadas** asociadas a un servicio específico. "
                    + "Disponible para usuarios con rol ENTRENADOR, COORDINADOR, CLIENTE, SOPORTE o ADMINISTRADOR."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de reseñas encontradas",
                    content = @Content(
                            mediaType = "application/json",
                            array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                                    schema = @Schema(implementation = Resena.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay reseñas disponibles para este servicio",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el rol del usuario no está autorizado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/servicio/{idServicio}")
    public ResponseEntity<?> porServicio(
            @PathVariable Long idServicio,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        validator.requireRole(request, "ENTRENADOR", "COORDINADOR", "CLIENTE", "SOPORTE", "ADMINISTRADOR");

        List<Resena> resenas = resenaService.obtenerPorServicio(idServicio, EstadoResena.DESBANEADO);

        if (resenas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("mensaje", "Este servicio aún no tiene reseñas disponibles."));
        }

        List<EntityModel<Resena>> modelos = resenas.stream()
                .map(resenaConPromedioModelAssembler::toModel)
                .toList();

        return ResponseEntity.ok(modelos);
    }





    @Operation(
            summary = "Buscar una reseña por ID",
            description = "Devuelve una reseña específica por su ID **solo si no está baneada**. "
                    + "Disponible para usuarios con rol ENTRENADOR, COORDINADOR, CLIENTE, SOPORTE o ADMINISTRADOR."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseña encontrada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Resena.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "La reseña no existe o está baneada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Object.class,
                                    example = "{\"error\": \"Reseña no encontrada o se encuentra baneada.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene permisos para consultar reseñas",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarResena(
            @Parameter(
                    description = "ID de la reseña a buscar",
                    example = "2"
            )
            @PathVariable Long id,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        validator.requireRole(request, "ENTRENADOR", "COORDINADOR", "CLIENTE", "SOPORTE", "ADMINISTRADOR");
        try {
            Resena resena = resenaService.buscarPorEstadoId(EstadoResena.DESBANEADO, id);
            return ResponseEntity.ok(resenaConPromedioModelAssembler.toModel(resena));
        } catch (RuntimeException e) {
            String mensaje = "Reseña no encontrada o se encuentra baneada.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", mensaje));
        }
    }

    @Operation(
            summary = "Obtener reseñas baneadas",
            description = "Devuelve una lista de reseñas que han sido marcadas como baneadas. "
                    + "Disponible únicamente para usuarios con rol SOPORTE o ADMINISTRADOR."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseñas baneadas encontradas",
                    content = @Content(
                            mediaType = "application/json",
                            array = @io.swagger.v3.oas.annotations.media.ArraySchema(
                                    schema = @Schema(implementation = Resena.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay reseñas baneadas registradas",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene permisos suficientes",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/baneadas")
    public ResponseEntity<?> obtenerResenasBaneadas(
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        validator.requireRole(request, "SOPORTE", "ADMINISTRADOR");

        List<Resena> resenasBaneadas = resenaService.buscarPorEstado(EstadoResena.BANEADO);

        if (resenasBaneadas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(Map.of("mensaje", "No hay reseñas baneadas."));
        }

        List<EntityModel<Resena>> modelos = resenasBaneadas.stream()
                .map(resena -> EntityModel.of(resena,
                        linkTo(methodOn(ResenaController.class).desbanear(resena.getId(), null))
                                .withRel("desbanear")))
                .toList();

        CollectionModel<EntityModel<Resena>> modeloFinal = CollectionModel.of(modelos,
                linkTo(methodOn(ResenaController.class).obtenerResenasBaneadas(null)).withSelfRel()
        );

        return ResponseEntity.ok(modeloFinal);
    }




    @Operation(
            summary = "Banear una reseña",
            description = "Marca una reseña como baneada por parte de usuarios con rol SOPORTE o ADMINISTRADOR. "
                    + "Una reseña baneada ya no estará disponible para consultas públicas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseña baneada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Resena.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "La reseña no fue encontrada o ya estaba baneada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Object.class,
                                    example = "{\"error\": \"Reseña no encontrada o ya está baneada.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene permisos para banear reseñas",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @PutMapping("/banear/{id}")
    public ResponseEntity<?> banear(
            @Parameter(
                    description = "ID de la reseña que se desea banear",
                    example = "5"
            )
            @PathVariable Long id,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        validator.requireRole(request, "ADMINISTRADOR", "SOPORTE");
        try {
            Resena baneada = resenaService.banear(id);

            EntityModel<Resena> modelo = EntityModel.of(baneada,
                    linkTo(methodOn(ResenaController.class).desbanear(baneada.getId(), null))
                            .withRel("desbanear")
            );

            return ResponseEntity.ok(modelo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Reseña no encontrada o ya está baneada."));
        }
    }





    @Operation(
            summary = "Desbanear una reseña",
            description = "Cambia el estado de una reseña baneada a activa (desbaneada). "
                    + "Solo usuarios con rol SOPORTE o ADMINISTRADOR pueden ejecutar esta operación."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseña desbaneada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Resena.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "La reseña no fue encontrada o ya estaba desbaneada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Object.class,
                                    example = "{\"error\": \"Reseña no encontrada o ya está desbaneada.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene permisos para desbanear reseñas",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @PutMapping("/desbanear/{id}")
    public ResponseEntity<?> desbanear(
            @Parameter(
                    description = "ID de la reseña que se desea desbanear",
                    example = "6"
            )
            @PathVariable Long id,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        validator.requireRole(request, "ADMINISTRADOR", "SOPORTE");
        try {
            Resena desbaneada = resenaService.desbanear(id);
            EntityModel<Resena> modelo = EntityModel.of(desbaneada,
                    linkTo(methodOn(ResenaController.class).desbanear(desbaneada.getId(), null))
                            .withRel("banear")
            );

            return ResponseEntity.ok(modelo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Reseña no encontrada o ya está desbaneada."));
        }
    }



    @Operation(
            summary = "Modificar una reseña existente",
            description = "Permite a un usuario con rol CLIENTE modificar el comentario y calificación de una reseña que le pertenece.",
            requestBody = @RequestBody(
                    description = "Datos nuevos de la reseña (solo comentario y calificación)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Resena.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Reseña modificada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Resena.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reseña no encontrada o no pertenece al usuario",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Object.class,
                                    example = "{\"error\": \"Reseña no encontrada o no es suya.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol CLIENTE",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarResena(
            @Parameter(
                    description = "ID de la reseña que se desea modificar",
                    example = "3"
            )
            @PathVariable Long id,

            @org.springframework.web.bind.annotation.RequestBody
            Resena datosNuevos,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        validator.requireRole(request, "CLIENTE");
        try {
            Long idUsuario = validator.getUserId(request);
            Resena actualizada = resenaService.modificarResena(idUsuario, id, datosNuevos);
            EntityModel<Resena> model = resenaModelAssembler.toModel(actualizada);
            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Reseña no encontrada o no es suya."));
        }
    }



    @Operation(
            summary = "Eliminar una reseña propia",
            description = "Permite a un usuario con rol CLIENTE eliminar una reseña que le pertenece. "
                    + "Eliminación lógica o física según la implementación del servicio."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Reseña eliminada exitosamente (sin contenido)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Reseña no encontrada o no pertenece al usuario",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Object.class,
                                    example = "{\"error\": \"Reseña no encontrada o no es suya.\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado: el usuario no tiene el rol CLIENTE",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<?> eliminarResena(
            @Parameter(
                    description = "ID de la reseña que se desea eliminar",
                    example = "3"
            )
            @PathVariable Long id,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        validator.requireRole(request, "CLIENTE");
        try {
            Long idUsuario = validator.getUserId(request);
            resenaService.eliminarResenaUsuario(idUsuario, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Reseña no encontrada o no es suya."));
        }
    }


}

