package com.fitlifespa.reservas.model;

import java.util.List;

import jakarta.persistence.CascadeType;
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
@Table(name="estado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstado;
    private String nombre;

    @OneToMany(mappedBy = "estado", cascade = CascadeType.ALL)
    private List<ColaReserva> colaReservas;

    @OneToMany(mappedBy = "estado", cascade = CascadeType.ALL)
    private List<Servicio> servicios;
}
