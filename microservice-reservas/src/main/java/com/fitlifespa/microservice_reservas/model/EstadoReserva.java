package com.fitlifespa.microservice_reservas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa los distintos estados que puede tener una reserva (por ejemplo: PENDIENTE, CONFIRMADA, CANCELADA).")
public class EstadoReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del estado de reserva", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Schema(description = "Nombre del estado de la reserva", example = "CONFIRMADA")
    private String nombre;
}



