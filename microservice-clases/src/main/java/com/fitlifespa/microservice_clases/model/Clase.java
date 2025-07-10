package com.fitlifespa.microservice_clases.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "clase")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una clase ofrecida por un entrenador.")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la clase", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idClase;

    @Schema(description = "Nombre de la clase", example = "Yoga Avanzado")
    private String nombres;

    @Schema(description = "Descripción detallada de la clase", example = "Clase de yoga enfocada en posturas avanzadas y respiración.")
    private String descripcion;

    @Schema(description = "ID del servicio relacionado con la clase", example = "2")
    private Long idServicio;

    @Schema(description = "ID del entrenador que imparte la clase", example = "5")
    private Long idUsuario;

    @Schema(description = "Fecha programada para la clase", example = "2025-07-15")
    private LocalDate fClase;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    @Schema(description = "Estado actual de la clase (por ejemplo: Programada, Cancelada, Finalizada)")
    private EstadoClase estado;
}


