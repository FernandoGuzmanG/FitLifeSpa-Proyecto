package com.fitlifespa.microservice_servicios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "servicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un servicio ofrecido por el sistema (ej. Masaje, Yoga, Spa)")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del servicio", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre del servicio", example = "Masaje")
    private String nombre;

    @Column(length = 500)
    @Schema(description = "Descripción del servicio", example = "Masaje relajante con aromaterapia")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    @Schema(description = "Estado actual del servicio (ej. ACTIVO, INACTIVO)")
    private EstadoServicio estado;
}