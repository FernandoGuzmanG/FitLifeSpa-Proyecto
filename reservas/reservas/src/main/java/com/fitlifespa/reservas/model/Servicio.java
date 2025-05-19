package com.fitlifespa.reservas.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name= "servicio")
@NoArgsConstructor
@AllArgsConstructor

public class Servicio {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long idServicio;
    private String nombre;
    private String descripcion;

    @OneToMany(mappedBy="servicio", cascade=CascadeType.ALL)
    private List<Reserva> reservas;

    @OneToMany(mappedBy="servicio", cascade=CascadeType.ALL)
    private List<Incluido> incluidos;

    @ManyToOne 
    @JoinColumn(name="id_estado")
    private Estado estado;






}
