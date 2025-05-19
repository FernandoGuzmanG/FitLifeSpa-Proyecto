package com.fitlifespa.gestionusuarios.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitlifespa.gestionusuarios.model.Enum.NombreRol;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private NombreRol nombreRol;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Usuario> usuarios;
    
}
