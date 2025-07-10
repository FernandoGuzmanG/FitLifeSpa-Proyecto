package com.fitlifespa.microservice_clases.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "estado_clase")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa los posibles estados de una clase.")
public class EstadoClase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del estado de la clase", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del estado de la clase", example = "PROGRAMADA")
    private String nombre;
}


