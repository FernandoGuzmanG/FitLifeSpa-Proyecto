package com.fitlifespa.microservice_membresias.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "membresia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una membresía adquirida por un usuario")
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la membresía", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idMembresia;

    @Column
    @Schema(description = "Fecha de inicio de la membresía", example = "2025-06-01")
    private LocalDate fechaInicio;

    @Column
    @Schema(description = "Fecha de término de la membresía", example = "2025-06-30")
    private LocalDate fechaTermino;

    @Column(nullable = false)
    @Schema(description = "ID del usuario que posee la membresía", example = "2")
    private Long idUsuario;

    @Column
    @Schema(description = "Costo total pagado por la membresía", example = "29990")
    private double CostoTotal;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    @JsonIgnoreProperties("membresias")
    @Schema(description = "Plan asociado a la membresía")
    private Plan plan;
}

