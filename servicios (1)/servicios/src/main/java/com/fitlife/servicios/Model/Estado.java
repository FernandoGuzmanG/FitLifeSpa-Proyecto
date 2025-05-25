package com.fitlife.servicios.Model;

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
@Table(name= "estado")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Estado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstado;

    @Column(nullable = false, length = 30, unique = true)
    private String nombreEstado;

    @OneToMany(mappedBy = "estado", cascade = CascadeType.ALL)
    private List<Membresia> membresias;

    @OneToMany(mappedBy = "estado", cascade = CascadeType.ALL)
    private List<Servicio> servicios;
    
    

}
