package com.fitlifespa.microservice_membresias.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // PENDIENTE, EN PROGRESO, CERRADO
}

