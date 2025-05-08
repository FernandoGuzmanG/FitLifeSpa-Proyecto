package com.fitlifespa.gestionusuarios.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(unique = true, length = 13)
    private String rut;

    @Column(length = 50, nullable = false)
    private String pnombre;

    @Column(length = 50, nullable = false)
    private String snombre;

    @Column(length = 50, nullable = false)
    private String apellidop;

    @Column(length = 50, nullable = false)
    private String apellidom;

    @Column(length = 30, nullable = false, unique = true)
    private String correo;

    @Column(length = 30, nullable = false, unique = true)
    private String nomUsuario;

    @Column(length = 30, nullable = false)
    private String contrasena;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    @JsonIgnoreProperties("usuario")
    private Rol rol;
}
