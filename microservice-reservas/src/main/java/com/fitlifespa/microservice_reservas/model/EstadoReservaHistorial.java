package com.fitlifespa.microservice_reservas.model;

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
public class EstadoReservaHistorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstadoRH;

    private LocalDateTime fechaCambio;

    private String comentario;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoReserva estado;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;
}

