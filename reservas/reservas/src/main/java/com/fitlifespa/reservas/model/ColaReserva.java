package com.fitlifespa.reservas.model;

import jakarta.persistence.Column;
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
@Table(name="cola_reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColaReserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCola;
    @Column
    private String comentario;

    @ManyToOne
    @JoinColumn(name="id_reserva")
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name="id_estado")
    private Estado estado;

}
