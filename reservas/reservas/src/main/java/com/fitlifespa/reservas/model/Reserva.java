package com.fitlifespa.reservas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name= "reserva")
@NoArgsConstructor
@AllArgsConstructor

public class Reserva {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idReserva;
    private String fechaReserva;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name= "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name= "id_servicio")
    private Servicio servicio;
}
