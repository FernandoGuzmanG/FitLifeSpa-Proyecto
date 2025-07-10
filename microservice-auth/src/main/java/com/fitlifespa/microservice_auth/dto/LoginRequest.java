package com.fitlifespa.microservice_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos necesarios para iniciar sesión")
public record LoginRequest(
        @Schema(description = "Correo electrónico del usuario", example = "cliente@fitlife.com")
        String correo,

        @Schema(description = "Contraseña del usuario", example = "cliente123")
        String clave
) {}
