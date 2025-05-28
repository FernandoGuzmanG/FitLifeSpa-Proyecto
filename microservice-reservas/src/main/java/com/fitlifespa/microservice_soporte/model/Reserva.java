package com.fitlifespa.microservice_membresias.model;

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
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    private LocalDateTime fechaReserva;

    private String descripcion;

    private Long idUsuario;

    private Long idServicio;

    private Long idEntrenador;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoReserva estado;
}

