package com.fitlifespa.microservice_servicios.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fitlifespa.microservice_servicios.model.Servicio;
import com.fitlifespa.microservice_servicios.security.RoleValidator;
import com.fitlifespa.microservice_servicios.service.ServicioService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final ServicioService servicioService;
    private final RoleValidator validator;

    @Operation(
            summary = "Crear un nuevo servicio",
            description = "Solo ADMINISTRADOR puede crear servicios en estado ACTIVO."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Servicio creado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"mensaje\": \"Servicio creado correctamente\", \"servicio\": { ... } }")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Estado ACTIVO no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"error\": \"Estado ACTIVO no encontrado\" }")
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (sin rol ADMINISTRADOR)"),
            @ApiResponse(responseCode = "401", description = "Token no válido")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nombre y descripción del nuevo servicio",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = Servicio.class,
                            example = "{ \"nombre\": \"Masaje\", \"descripcion\": \"Relajante con aceites\" }"
                    )
            )
    )
    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody Servicio servicio,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        try {
            validator.requireRole(request, "ADMINISTRADOR");

            Servicio nuevoServicio = servicioService.crear(servicio.getNombre(), servicio.getDescripcion());

            EntityModel<Servicio> recurso = EntityModel.of(nuevoServicio);
            Long id = nuevoServicio.getId();

            recurso.add(linkTo(methodOn(ServicioController.class).obtener(id, request)).withSelfRel());
            recurso.add(linkTo(methodOn(ServicioController.class).actualizar(id, null, request)).withRel("actualizar"));
            recurso.add(linkTo(methodOn(ServicioController.class).desactivarServicio(id, request)).withRel("desactivar"));
            recurso.add(linkTo(methodOn(ServicioController.class).activarServicio(id, request)).withRel("activar"));

            Map<String, Object> body = new HashMap<>();
            body.put("mensaje", "Servicio creado correctamente");
            body.put("servicio", recurso);

            return ResponseEntity.status(HttpStatus.CREATED).body(body);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }



    @Operation(
            summary = "Listar todos los servicios",
            description = "Solo ADMINISTRADOR puede obtener la lista completa de servicios."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Servicios listados correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"mensaje\": \"Listado de servicios obtenido correctamente\", \"servicios\": [ ... ] }")
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acceso denegado (sin rol ADMINISTRADOR)"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token no válido"
            )
    })
    @GetMapping
    public ResponseEntity<?> listar(@Parameter(hidden = true) HttpServletRequest request) {
        try {
            validator.requireRole(request, "ADMINISTRADOR");

            List<Servicio> servicios = servicioService.listarTodos();

            List<EntityModel<Servicio>> recursos = servicios.stream()
                    .map(servicio -> {
                        Long id = servicio.getId();
                        EntityModel<Servicio> recurso = EntityModel.of(servicio);
                        recurso.add(linkTo(methodOn(ServicioController.class).obtener(id, request)).withSelfRel());
                        recurso.add(linkTo(methodOn(ServicioController.class).actualizar(id, null, request)).withRel("actualizar"));
                        recurso.add(linkTo(methodOn(ServicioController.class).desactivarServicio(id, request)).withRel("desactivar"));
                        recurso.add(linkTo(methodOn(ServicioController.class).activarServicio(id, request)).withRel("activar"));
                        return recurso;
                    })
                    .toList();

            Map<String, Object> body = new HashMap<>();
            body.put("mensaje", "Listado de servicios obtenido correctamente");
            body.put("servicios", recursos);

            return ResponseEntity.ok(body);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }
    }



    @Operation(
            summary = "Listar servicios activos",
            description = "Disponible para CLIENTE, ENTRENADOR, COORDINADOR o ADMINISTRADOR."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Servicios activos listados correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"servicios\": [ ... ] }")
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "401", description = "Token no válido")
    })
    @GetMapping("/activos")
    public ResponseEntity<?> listarActivos(@Parameter(hidden = true) HttpServletRequest request) {
        try {
            validator.requireRole(request, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");

            List<Servicio> activos = servicioService.listarActivos();

            // HATEOAS por servicio
            List<EntityModel<Servicio>> recursos = activos.stream()
                    .map(servicio -> {
                        Long id = servicio.getId();
                        return EntityModel.of(servicio)
                                .add(linkTo(methodOn(ServicioController.class).obtener(id, request)).withSelfRel())
                                .add(linkTo(methodOn(ServicioController.class).actualizar(id, null, request)).withRel("actualizar"))
                                .add(linkTo(methodOn(ServicioController.class).desactivarServicio(id, request)).withRel("desactivar"))
                                .add(linkTo(methodOn(ServicioController.class).activarServicio(id, request)).withRel("activar"));
                    })
                    .toList();

            return ResponseEntity.ok(Map.of("servicios", recursos));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }


    @Operation(
            summary = "Actualizar un servicio",
            description = "Solo ADMINISTRADOR puede actualizar nombre y descripción de un servicio."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Servicio actualizado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "{ \"mensaje\": \"Servicio actualizado\", \"servicio\": { ... } }")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "401", description = "Token no válido")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nombre y descripción del servicio",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = Servicio.class,
                            example = "{ \"nombre\": \"Yoga\", \"descripcion\": \"Clase de estiramiento\" }"
                    )
            )
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Servicio servicio,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        try {
            validator.requireRole(request, "ADMINISTRADOR");

            Servicio actualizado = servicioService.actualizar(id, servicio.getNombre(), servicio.getDescripcion());

            EntityModel<Servicio> recurso = EntityModel.of(actualizado)
                    .add(linkTo(methodOn(ServicioController.class).obtener(id, request)).withSelfRel())
                    .add(linkTo(methodOn(ServicioController.class).desactivarServicio(id, request)).withRel("desactivar"))
                    .add(linkTo(methodOn(ServicioController.class).activarServicio(id, request)).withRel("activar"));

            Map<String, Object> body = new HashMap<>();
            body.put("mensaje", "Servicio actualizado");
            body.put("servicio", recurso);

            return ResponseEntity.ok(body);

        } catch (NoSuchElementException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Servicio no encontrado"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }


    @Operation(
            summary = "Desactivar un servicio",
            description = "Solo ADMINISTRADOR puede desactivar un servicio, cambiando su estado a INACTIVO."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Servicio desactivado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Servicio.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Servicio o estado INACTIVO no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "401", description = "Token no válido")
    })
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Servicio> desactivarServicio(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        try{
            return ResponseEntity.ok(servicioService.desactivarServicio(id));

        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(
            summary = "Activar un servicio",
            description = "Solo ADMINISTRADOR puede activar un servicio, cambiando su estado a ACTIVO."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Servicio activado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Servicio.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Servicio o estado ACTIVO no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "401", description = "Token no válido")
    })
    @PutMapping("/{id}/activar")
    public ResponseEntity<Servicio> activarServicio(@PathVariable Long id, HttpServletRequest request) {
        validator.requireRole(request, "ADMINISTRADOR");
        try{
            return ResponseEntity.ok(servicioService.activarServicio(id));
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Obtener un servicio por ID",
            description = "Solo ADMINISTRADOR puede consultar un servicio específico por su ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Servicio encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Servicio.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "401", description = "Token no válido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(
            @PathVariable Long id,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        try {
            validator.requireRole(request, "ADMINISTRADOR");

            return servicioService.obtenerPorId(id)
                    .map(servicio -> {
                        EntityModel<Servicio> recurso = EntityModel.of(servicio);
                        recurso.add(linkTo(methodOn(ServicioController.class).obtener(id, request)).withSelfRel());
                        recurso.add(linkTo(methodOn(ServicioController.class).actualizar(id, null, request)).withRel("actualizar"));
                        recurso.add(linkTo(methodOn(ServicioController.class).desactivarServicio(id, request)).withRel("desactivar"));
                        recurso.add(linkTo(methodOn(ServicioController.class).activarServicio(id, request)).withRel("activar"));
                        return ResponseEntity.ok(recurso);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }
}

