package com.fitlifespa.microservice_tickets.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa el estado actual de un ticket (por ejemplo: ABIERTO, EN PROCESO, CERRADO)")
public class EstadoTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del estado del ticket", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "Nombre del estado del ticket", example = "ABIERTO")
    private String nombre;
}


