package com.fitlifespa.membresia.Model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="plan")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, length = 13)
    private Long idPlan;
    @Column(length = 20, nullable = false)
    private String nombre;
    @Column(length = 50, nullable = false)
    private String descripcion;
    private double costo;
    private String duracion;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<Incluido> incluidos;





}
