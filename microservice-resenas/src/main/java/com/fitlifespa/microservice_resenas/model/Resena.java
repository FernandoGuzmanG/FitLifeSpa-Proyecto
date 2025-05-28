package com.fitlifespa.microservice_resenas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "resena")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaResena;

    @Column(length = 500)
    private String comentario;

    private Long idUsuario;

    private Long idServicio;

    @Column(nullable = false)
    private Double calificacion;
}

