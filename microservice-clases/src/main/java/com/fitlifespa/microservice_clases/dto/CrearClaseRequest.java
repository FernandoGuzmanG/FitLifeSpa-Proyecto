package com.fitlifespa.microservice_clases.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "DTO para crear una nueva clase impartida por un entrenador.")
public class CrearClaseRequest {

    @Schema(description = "Nombre de la clase", example = "Yoga Avanzado")
    private String nombre;

    @Schema(description = "Descripción detallada de la clase", example = "Clase de yoga enfocada en posturas avanzadas y respiración.")
    private String descripcion;

    @Schema(description = "ID del servicio relacionado con la clase", example = "2")
    private Long idServicio;

    @Schema(description = "ID del entrenador que impartirá la clase", example = "5")
    private Long idEntrenador;

    @Schema(description = "Fecha programada para la clase", example = "2025-07-15")
    private LocalDate fecha;
}


