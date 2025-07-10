package com.fitlifespa.microservice_reservas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "edo_reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que registra el historial de cambios de estado en una reserva.")
public class EstadoReservaHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del historial de estado", example = "101", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idEstadoRH;

    @Schema(description = "Fecha y hora en la que se registró el cambio de estado", example = "2025-06-24T10:30:00")
    private LocalDateTime fechaCambio;

    @Schema(description = "Comentario opcional asociado al cambio de estado", example = "Cambio solicitado por el usuario")
    private String comentario;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    @Schema(description = "Estado al que se cambió la reserva")
    private EstadoReserva estado;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    @Schema(description = "Reserva a la que pertenece este cambio de estado")
    private Reserva reserva;
}


