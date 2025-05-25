package com.fitlifespa.membresia.Model;

import java.util.List;

import jakarta.persistence.Column;
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
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name= "usuario")
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
    private String appaterno;

    @Column(length = 50, nullable = false)
    private String apmaterno;

    @Column(length = 30, nullable = false, unique = true)
    private String correo;

    @Column(length = 30, nullable = false, unique = true)
    private String nomUsuario;

    @Column(length = 30, nullable = false)
    private String contrasena;

    @ManyToOne
    @JoinColumn(name ="id_rol")
    private Rol rol;

    @OneToMany(mappedBy ="usuario" )
    private List<Membresia> membresias;




}
