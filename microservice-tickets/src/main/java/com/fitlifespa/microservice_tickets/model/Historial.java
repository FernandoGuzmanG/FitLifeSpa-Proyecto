package com.fitlifespa.microservice_tickets.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "historial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un registro histórico de acciones o mensajes asociados a un ticket")
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del historial", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, length = 100)
    @Schema(description = "Tipo de historial (por ejemplo: MENSAJE, CAMBIO_ESTADO)", example = "MENSAJE")
    private String tipo;

    @Column(nullable = false, length = 500)
    @Schema(description = "Contenido del mensaje o detalle del cambio", example = "El usuario adjuntó un nuevo archivo")
    private String mensaje;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora en que se registró el mensaje", example = "2025-06-24T15:45:00")
    private LocalDateTime fechaMensaje;

    @ManyToOne
    @JoinColumn(name = "id_ticket")
    @Schema(description = "Ticket asociado a este registro de historial")
    private Ticket ticket;
}


