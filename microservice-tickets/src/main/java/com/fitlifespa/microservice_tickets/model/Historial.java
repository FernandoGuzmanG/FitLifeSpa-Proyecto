package com.fitlifespa.microservice_tickets.model;

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
public class Historial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;

    private String mensaje;

    private LocalDateTime fechaMensaje;

    @ManyToOne
    @JoinColumn(name = "id_ticket")
    private Ticket ticket;
}

