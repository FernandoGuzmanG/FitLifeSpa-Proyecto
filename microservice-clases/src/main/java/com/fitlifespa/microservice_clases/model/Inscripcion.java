package com.fitlifespa.microservice_clases.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "inscripcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa la inscripción de un usuario a una clase específica.")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la inscripción", example = "2", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idInscripcion;

    @Schema(description = "Fecha en que se realizó la inscripción", example = "2025-06-20")
    private LocalDate fInscripcion;

    @Schema(description = "ID del usuario inscrito", example = "2")
    private Long idUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clase")
    @Schema(description = "Clase a la que se encuentra inscrito el usuario")
    private Clase clase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado")
    @Schema(description = "Estado actual de la inscripción")
    private EstadoInscripcion estado;
}
