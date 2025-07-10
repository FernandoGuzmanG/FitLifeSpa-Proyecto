package com.fitlifespa.microservice_clases.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_inscripcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa los posibles estados de una inscripción (por ejemplo: CONFIRMADA, CANCELADA).")
public class EstadoInscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del estado de la inscripción", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idEstado;

    @Column(nullable = false, unique = true, length = 30)
    @Schema(description = "Nombre del estado de la inscripción", example = "CONFIRMADA")
    private String nombre;
}



