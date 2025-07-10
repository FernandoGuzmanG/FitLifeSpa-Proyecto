package com.fitlifespa.microservice_usuarios.controller;

import com.fitlifespa.microservice_usuarios.model.Rol;
import com.fitlifespa.microservice_usuarios.security.RoleValidator;
import com.fitlifespa.microservice_usuarios.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
@Controller
@RequestMapping("/api/roles")
public class RolesController {
    @Autowired
    private RoleValidator roleValidator;
    @Autowired
    private RolService rolService;

    @Operation(
            summary = "Listar todos los roles",
            description = "Este endpoint permite a un usuario con rol ADMINISTRADOR obtener una lista de todos los roles disponibles en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de roles obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Rol.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay roles registrados",
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
    @GetMapping
    public ResponseEntity<List<Rol>> mostrarRoles(
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        List<Rol> roles = rolService.findAll();
        if (roles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles);
    }


    @Operation(
            summary = "Obtener rol por ID",
            description = "Permite al ADMINISTRADOR obtener la información de un rol específico mediante su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Rol.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rol no encontrado",
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
    @GetMapping("/id/{id}")
    public ResponseEntity<?> mostrarRol(
            @Parameter(
                    description = "ID del rol a buscar",
                    example = "2"
            )
            @PathVariable Long id,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Rol rol = rolService.findById(id);
            return ResponseEntity.ok(rol);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(
            summary = "Buscar rol por nombre",
            description = "Permite al ADMINISTRADOR buscar un rol específico utilizando su nombre. "
                    + "El nombre del rol no distingue entre mayúsculas y minúsculas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Rol.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rol no encontrado",
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
    @GetMapping("/nombre/{nombreRol}")
    public ResponseEntity<?> buscarRolNombre(
            @Parameter(
                    description = "Nombre del rol a buscar (por ejemplo: CLIENTE, ADMINISTRADOR)",
                    example = "CLIENTE"
            )
            @PathVariable String nombreRol,

            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            Rol rol = rolService.findByNombre(nombreRol.toUpperCase());
            return ResponseEntity.ok(rol);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
