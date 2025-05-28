package com.fitlifespa.microservice_clases.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_inscripcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoInscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstado;

    @Column(nullable = false, unique = true, length = 30)
    private String nombre;
}


