package com.fitlifespa.microservice_clases.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "clase")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClase;

    private String nombres;
    private String descripcion;

    private Long idServicio;

    private Long idUsuario; // id del entrenador

    private LocalDate fClase;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    private EstadoClase estado;
}

