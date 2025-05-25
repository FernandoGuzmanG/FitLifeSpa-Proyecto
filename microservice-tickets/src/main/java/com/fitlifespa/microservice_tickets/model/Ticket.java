package com.fitlifespa.microservice_tickets.model;

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
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    @Column(length = 500)
    private String descripcion;

    private Long idUsuario;
    private Long idUsuarioRol;

    @ManyToOne
    @JoinColumn(name = "id_motivo")
    private Motivo motivo;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoTicket estado;
}