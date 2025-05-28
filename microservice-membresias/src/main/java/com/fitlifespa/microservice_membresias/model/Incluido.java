package com.fitlifespa.microservice_membresias.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "incluido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Incluido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIncluido;

    private Long idServicio;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    @JsonIgnoreProperties("incluido")
    private Plan plan;
}

