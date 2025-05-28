package com.fitlifespa.microservice_clases.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CrearClaseRequest {
    private String nombre;
    private String descripcion;
    private Long idServicio;
    private Long idEntrenador;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;
}

