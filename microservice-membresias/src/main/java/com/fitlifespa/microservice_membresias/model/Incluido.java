package com.fitlifespa.microservice_membresias.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incluido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa los servicios incluidos dentro de un plan")
public class Incluido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del servicio incluido", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idIncluido;

    @Schema(description = "ID del servicio incluido en el plan", example = "10")
    private Long idServicio;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    @JsonIgnoreProperties("incluido")
    @Schema(description = "Plan al que pertenece este servicio")
    private Plan plan;
}


