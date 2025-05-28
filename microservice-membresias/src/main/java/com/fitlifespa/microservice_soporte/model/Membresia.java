package com.fitlifespa.microservice_membresias.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "membresia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Membresia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMembresia;

    @Column
    private LocalDate fechaInicio;

    @Column
    private LocalDate fechaTermino;

    @Column(nullable = false)
    private Long idUsuario;

    @Column
    private double CostoTotal;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    @JsonIgnoreProperties("membresias")
    private Plan plan;
}
