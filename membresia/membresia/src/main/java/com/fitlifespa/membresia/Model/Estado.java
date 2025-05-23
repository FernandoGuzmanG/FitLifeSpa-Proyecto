package com.fitlifespa.membresia.Model;

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
@Table(name= "estado")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Estado {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long IdEstado;
    private String nombre;

    @OneToMany(mappedBy = "estado", cascade = CascadeType.ALL)
    private List<Membresia> membresias;
    

}
