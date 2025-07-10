package com.fitlifespa.microservice_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos necesarios para registrar un nuevo usuario")
public record RegisterRequest(

        @Schema(description = "RUT del usuario", example = "49293143-9")
        String rut,

        @Schema(description = "Correo electrónico del usuario", example = "cliente@correo.com")
        String correo,

        @Schema(description = "Contraseña para la cuenta del usuario", example = "claveSegura123")
        String clave,

        @Schema(description = "Primer nombre del usuario", example = "Juan")
        String pnombre,

        @Schema(description = "Segundo nombre del usuario", example = "Carlos")
        String snombre,

        @Schema(description = "Apellido paterno del usuario", example = "González")
        String appaterno,

        @Schema(description = "Apellido materno del usuario", example = "Pérez")
        String apmaterno
) {}
