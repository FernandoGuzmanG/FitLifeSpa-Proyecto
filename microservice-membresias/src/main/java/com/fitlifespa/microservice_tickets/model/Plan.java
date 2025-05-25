package com.fitlifespa.microservice_tickets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPlan;

    private String nombre;
    private String descripcion;
    private Double costo;
    private Integer duracion;

    @JsonIgnore
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Incluido> incluidos;
}
