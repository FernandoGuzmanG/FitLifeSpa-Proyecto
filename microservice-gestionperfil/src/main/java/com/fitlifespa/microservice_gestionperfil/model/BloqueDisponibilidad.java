package com.fitlifespa.microservice_gestionperfil.model;

import com.fitlifespa.microservice_gestionperfil.model.Enum.DiaSemana;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "bloque_disponibilidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloqueDisponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DiaSemana dia; // LUNES, MARTES, ...

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private Long idUsuario; // FK del entrenador
}



