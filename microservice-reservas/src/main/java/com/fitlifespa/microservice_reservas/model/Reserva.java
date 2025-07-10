package com.fitlifespa.microservice_reservas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una reserva de servicio realizada por un usuario.")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la reserva", example = "5001", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Fecha del servicio reservado", example = "2025-07-01")
    private LocalDate fecha;

    @Schema(description = "Fecha y hora en que se realizó la reserva", example = "2025-06-20T15:45:00")
    private LocalDateTime fechaReserva;

    @Schema(description = "Descripción de la reserva", example = "Sesión de fisioterapia con seguimiento")
    private String descripcion;

    @Schema(description = "ID del usuario que realiza la reserva", example = "45")
    private Long idUsuario;

    @Schema(description = "ID del servicio reservado", example = "3")
    private Long idServicio;

    @Schema(description = "ID del entrenador asignado a la reserva", example = "12")
    private Long idEntrenador;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    @Schema(description = "Estado actual de la reserva")
    private EstadoReserva estado;
}


