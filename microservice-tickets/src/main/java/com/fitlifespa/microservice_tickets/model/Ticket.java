package com.fitlifespa.microservice_tickets.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un ticket generado por un usuario solicitando soporte")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del ticket", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Fecha y hora en que se generó el ticket", example = "2025-06-24T14:30:00")
    private LocalDateTime fecha;

    @Column(length = 500)
    @Schema(description = "Descripción del problema reportado por el usuario", example = "El sistema no me permite iniciar sesión")
    private String descripcion;

    @Column(nullable = false)
    @Schema(description = "ID del usuario que genera el ticket", example = "101")
    private Long idUsuario;

    @Column()
    @Schema(description = "ID del personal de soporte asignado al ticket", example = "205", nullable = true)
    private Long idSoporte;

    @ManyToOne
    @JoinColumn(name = "id_motivo")
    @Schema(description = "Motivo asociado al ticket")
    private Motivo motivo;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    @Schema(description = "Estado actual del ticket (por ejemplo: ABIERTO, EN PROCESO, CERRADO)")
    private EstadoTicket estado;
}
