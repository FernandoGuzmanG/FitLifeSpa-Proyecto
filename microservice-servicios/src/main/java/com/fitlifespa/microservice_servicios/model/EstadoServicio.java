package com.fitlifespa.microservice_servicios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_servicio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre; // Ej: ACTIVO, INACTIVO
}

