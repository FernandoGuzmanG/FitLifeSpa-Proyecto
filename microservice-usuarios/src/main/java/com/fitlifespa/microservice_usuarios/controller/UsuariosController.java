package com.fitlifespa.microservice_usuarios.controller;

import java.util.List;
import java.util.Map;

import com.fitlifespa.microservice_usuarios.dto.UsuarioPerfil;
import com.fitlifespa.microservice_usuarios.hateoas.UsuarioModelAssembler;
import com.fitlifespa.microservice_usuarios.security.RoleValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fitlifespa.microservice_usuarios.model.Usuario;
import com.fitlifespa.microservice_usuarios.service.UsuarioService;

@Controller
@RequestMapping("/api/usuarios")
public class UsuariosController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private RoleValidator roleValidator;
    @Autowired
    private UsuarioModelAssembler usuarioModelAssembler;

    @Operation(
            summary = "Obtener todos los usuarios",
            description = "Este endpoint permite al ADMINISTRADOR obtener una lista de todos los usuarios registrados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Usuario.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "No hay usuarios registrados",
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
    public ResponseEntity<?> mostrarUsuarios(@Parameter(hidden = true)HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        List<Usuario> usuarios = usuarioService.findAll();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var usuariosConLinks = usuarios.stream().map(usuarioModelAssembler::toModel).toList();
        return ResponseEntity.ok(usuariosConLinks);
    }


    @GetMapping("/id/{id}")
    @Operation(
            summary = "Obtener un usuario por ID",
            description = "Este endpoint permite al ADMINISTRADOR obtener la información de un usuario específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: el usuario no tiene el rol requerido"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    public ResponseEntity<?> mostrarUsuario(
            @Parameter(
                    description = "ID único del usuario",
                    example = "2"
            )
            @PathVariable Long id,
            @Parameter(hidden = true)
            HttpServletRequest request) {
        roleValidator.requireRole(request, "ADMINISTRADOR");

        try {
            Usuario usuario = usuarioService.findById(id);
            EntityModel<Usuario> model = usuarioModelAssembler.toModel(usuario);
            return ResponseEntity.ok(model);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(
            summary = "Buscar usuario por correo",
            description = "Permite al ADMINISTRADOR buscar un usuario usando su correo electrónico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: el usuario no tiene el rol requerido"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/correo/{correo}")
    public ResponseEntity<?> buscarPorCorreoUsuario(
            @Parameter(
                    description = "Correo único del usuario",
                    example = "cliente@fitlife.com"
            )
            @PathVariable String correo,
            @Parameter(hidden = true)
            HttpServletRequest request) {

        roleValidator.requireRole(request, "ADMINISTRADOR");

        try {
            Usuario usuario = usuarioService.findByCorreo(correo);
            EntityModel<Usuario> model = usuarioModelAssembler.toModel(usuario);
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(
            summary = "Buscar usuario por RUT",
            description = "Permite al ADMINISTRADOR buscar un usuario mediante su RUT (Rol Único Tributario)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: el usuario no tiene el rol requerido"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acceso denegado: token no válido",
                    content = @Content
            )
    })
    @GetMapping("/rut/{rut}")
    public ResponseEntity<?> buscarPorRut(
            @Parameter(
                    description = "Rut único del usuario",
                    example = "98765432-1"
            )
            @PathVariable String rut,
            @Parameter(hidden = true)
            HttpServletRequest request) {

        roleValidator.requireRole(request, "ADMINISTRADOR");

        try {
            Usuario usuario = usuarioService.findByRut(rut);
            EntityModel<Usuario> model = usuarioModelAssembler.toModel(usuario);
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }



    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Permite al ADMINISTRADOR registrar un nuevo usuario en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o error de validación",
                    content = @Content(mediaType = "application/json")
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
    @PostMapping
    public ResponseEntity<?> registrarUsuario(
            @RequestBody Usuario usuario,
            @Parameter(hidden = true)
            HttpServletRequest request) {

        roleValidator.requireRole(request, "ADMINISTRADOR");

        try {
            Usuario nuevoUsuario = usuarioService.save(usuario);
            EntityModel<Usuario> model = usuarioModelAssembler.toModel(nuevoUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(model);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: No se pudo registrar el usuario. Verifica los datos.");
        }
    }



    @Operation(
            summary = "Actualizar datos de un usuario",
            description = "Permite al ADMINISTRADOR actualizar los datos de un usuario existente identificado por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
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
    @PutMapping("/id/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @RequestBody Usuario usuario,
            @Parameter(
                    description = "ID del usuario a actualizar",
                    example = "2"
            )@PathVariable Long id,
            @Parameter(hidden = true)
            HttpServletRequest request) {

        roleValidator.requireRole(request, "ADMINISTRADOR");

        try {
            Usuario actualizado = usuarioService.actualizarUsuario(usuario, id);
            EntityModel<Usuario> model = usuarioModelAssembler.toModel(actualizado);
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }


    @Operation(
            summary = "Activar un usuario",
            description = "Permite al ADMINISTRADOR activar un usuario existente cambiando su estado a ACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario activado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
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
    @PutMapping("/id/{id}/activar")
    public ResponseEntity<?> activarUsuario(
            @Parameter(
                    description = "ID del usuario a activar",
                    example = "2"
            )@PathVariable Long id,
            @Parameter(hidden = true)
            HttpServletRequest request) {

        roleValidator.requireRole(request, "ADMINISTRADOR");

        try {
            Usuario usuario = usuarioService.activarUsuario(id);
            EntityModel<Usuario> model = usuarioModelAssembler.toModel(usuario);
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
    }


    @Operation(
            summary = "Desactivar un usuario",
            description = "Permite al ADMINISTRADOR desactivar un usuario existente cambiando su estado a INACTIVO."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario desactivado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
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
    @PutMapping("/id/{id}/desactivar")
    public ResponseEntity<?> desactivarUsuario(
            @Parameter(
                    description = "ID del usuario a desactivar",
                    example = "2"
            )@PathVariable Long id,
            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "ADMINISTRADOR");

        try {
            Usuario usuario = usuarioService.desactivarUsuario(id);
            EntityModel<Usuario> model = usuarioModelAssembler.toModel(usuario);
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
    }


    @Operation(
            summary = "Cambiar el rol de un usuario",
            description = "Permite al ADMINISTRADOR asignar un nuevo rol a un usuario existente. El nombre del rol debe coincidir con un rol válido (por ejemplo: CLIENTE, ENTRENADOR, ADMINISTRADOR)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol del usuario actualizado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Usuario.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario o rol no encontrado",
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
    @PutMapping("/id/{id}/{nombreRol}")
    public ResponseEntity<?> cambiarRolUsuario(
            @Parameter(
                    description = "ID del usuario a cambiar rol",
                    example = "5"
            )@PathVariable Long id,
            @Parameter(
                    description = "Nombre del rol nuevo a asignar",
                    example = "CLIENTE"
            )@PathVariable String nombreRol,
            @Parameter(hidden = true)HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "ADMINISTRADOR");

        try {
            Usuario usuario = usuarioService.cambiarRol(id, nombreRol);
            EntityModel<Usuario> model = usuarioModelAssembler.toModel(usuario);
            return ResponseEntity.ok(model);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario o rol no encontrado"));
        }
    }


    @Operation(
            summary = "Eliminar un usuario",
            description = "Permite al ADMINISTRADOR eliminar un usuario por su ID. Esta acción es irreversible."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado exitosamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = Map.class,
                                    example = "{\"error\": \"Usuario no encontrado\"}"
                            )
                    )
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
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> eliminarUsuario(
            @Parameter(
                    description = "ID del usuario a eliminar",
                    example = "6"
            )@PathVariable Long id,
            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "ADMINISTRADOR");
        try {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
    }

    @Operation(
            summary = "Cambiar contraseña de usuario",
            description = "Permite a un usuario autenticado con rol CLIENTE, ENTRENADOR, COORDINADOR o ADMINISTRADOR cambiar su contraseña actual por una nueva."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Contraseña actualizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "Contraseña actualizada exitosamente.")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error en la solicitud: contraseñas no coinciden o datos inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "Las contraseñas no coinciden.")
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = "Usuario no encontrado.")
                    )
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
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Mapa con las claves nuevaContrasena y confirmarContrasena",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = Map.class,
                            example = "{\"nuevaContrasena\": \"nueva123\", \"confirmarContrasena\": \"nueva123\"}"
                    )
            )
    )
    @PutMapping("/cambiar-clave")
    public ResponseEntity<String> cambiarClave(
            @RequestBody Map<String, String> datos,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        roleValidator.requireRole(request, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");
        String nueva = datos.get("nuevaContrasena");
        String confirmar = datos.get("confirmarContrasena");
        Long idUsuario = roleValidator.getUserId(request);
        try {
            usuarioService.cambiarClave(idUsuario, nueva, confirmar);
            return ResponseEntity.ok("Contraseña actualizada exitosamente.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfil> verPerfil(HttpServletRequest request) {
        roleValidator.requireRole(request, "CLIENTE", "ENTRENADOR", "COORDINADOR", "ADMINISTRADOR");
        Long idUsuario = roleValidator.getUserId(request);
        Usuario usuario = usuarioService.findById(idUsuario);

        UsuarioPerfil dto = new UsuarioPerfil(
                usuario.getRut(),
                usuario.getPnombre(),
                usuario.getSnombre(),
                usuario.getAppaterno(),
                usuario.getApmaterno(),
                usuario.getCorreo()
        );

        return ResponseEntity.ok(dto);
    }


    @PutMapping("/perfil")
    public ResponseEntity<UsuarioPerfil> actualizarPerfil(@RequestBody UsuarioPerfil nuevosDatos,
                                                             HttpServletRequest request) {
        Long idUsuario = roleValidator.getUserId(request);
        Usuario usuario = usuarioService.findById(idUsuario);

        usuario.setRut(nuevosDatos.getRut());
        usuario.setPnombre(nuevosDatos.getPnombre());
        usuario.setSnombre(nuevosDatos.getSnombre());
        usuario.setAppaterno(nuevosDatos.getAppaterno());
        usuario.setApmaterno(nuevosDatos.getApmaterno());
        usuario.setCorreo(nuevosDatos.getCorreo());

        usuarioService.save(usuario);

        UsuarioPerfil dto = new UsuarioPerfil(
                usuario.getRut(),
                usuario.getPnombre(),
                usuario.getSnombre(),
                usuario.getAppaterno(),
                usuario.getApmaterno(),
                usuario.getCorreo()
        );

        return ResponseEntity.ok(dto);
    }
}
