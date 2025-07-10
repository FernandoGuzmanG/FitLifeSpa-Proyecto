package com.fitlifespa.microservice_auth.controller;

import com.fitlifespa.microservice_auth.dto.LoginRequest;
import com.fitlifespa.microservice_auth.dto.LoginResponse;
import com.fitlifespa.microservice_auth.dto.RegisterRequest;
import com.fitlifespa.microservice_auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthService authService;

    @Operation(
        summary = "Autenticar usuario",
        description = "Este endpoint permite a un usuario autenticarse mediante su correo y contraseña. "
        + "Si las credenciales son válidas, se devuelve un token JWT."
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Autenticación exitosa. Token JWT devuelto.",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = LoginResponse.class,
                                example = "{\"token\": \"eyJhbGciOiJIUzI1NiJ9...\"}"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Credenciales inválidas o usuario no encontrado",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = String.class,
                                example = "Error: Credenciales inválidas"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Usuario inactivo",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = String.class,
                                example = "Error: Usuario inactivo"
                        )
                )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);

            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: " + e.getMessage());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error: " + e.getMessage());
        } catch (DisabledException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Error: " + e.getMessage());
        }
    }



    @Operation(
        summary = "Registrar un nuevo usuario",
        description = "Este endpoint permite a un nuevo usuario registrarse en la plataforma. "
                + "El rol asignado por defecto será CLIENTE y el estado será ACTIVO.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Objeto con los datos necesarios para crear un nuevo usuario",
                required = true,
                content = @Content(schema = @Schema(implementation = RegisterRequest.class))
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Usuario registrado exitosamente",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = String.class,
                                example = "¡Registro exitóso!"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Error en la solicitud (correo ya registrado u otro dato inválido)",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(
                                implementation = String.class,
                                example = "Error: Correo ya registrado"
                        )
                )
        )
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            authService.register(request);
            return ResponseEntity.ok("¡Registro exitóso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
    }
}
