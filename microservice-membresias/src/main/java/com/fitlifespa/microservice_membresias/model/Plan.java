package com.fitlifespa.microservice_membresias.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un plan de suscripción que puede adquirir un usuario")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del plan", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idPlan;

    @Schema(description = "Nombre del plan", example = "Plan Premium")
    private String nombre;

    @Schema(description = "Descripción del plan", example = "Acceso completo a todos los servicios")
    private String descripcion;

    @Schema(description = "Costo del plan", example = "29990.0")
    private Double costo;

    @Schema(description = "Duración del plan en meses", example = "1")
    private Integer duracion;

    @JsonIgnore
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    @Schema(description = "Lista de servicios incluidos en el plan", hidden = true)
    private List<Incluido> incluidos;
}

