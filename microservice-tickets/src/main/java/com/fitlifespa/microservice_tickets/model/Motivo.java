package com.fitlifespa.microservice_tickets.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "motivo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa el motivo por el cual se genera un ticket")
public class Motivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del motivo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 200)
    @Schema(description = "Descripción del motivo", example = "Problema con el inicio de sesión")
    private String descripcion;
}

