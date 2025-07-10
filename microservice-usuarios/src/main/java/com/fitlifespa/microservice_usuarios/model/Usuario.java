package com.fitlifespa.microservice_usuarios.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidad que representa a un usuario del sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del usuario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idUsuario;

    @Column(unique = true, length = 13)
    @Schema(description = "RUT del usuario (único)", example = "12.345.678-9")
    private String rut;

    @Column(length = 50, nullable = false)
    @Schema(description = "Primer nombre del usuario", example = "Juan")
    private String pnombre;

    @Column(length = 50, nullable = false)
    @Schema(description = "Segundo nombre del usuario", example = "Carlos")
    private String snombre;

    @Column(length = 50, nullable = false)
    @Schema(description = "Apellido paterno del usuario", example = "Pérez")
    private String appaterno;

    @Column(length = 50, nullable = false)
    @Schema(description = "Apellido materno del usuario", example = "González")
    private String apmaterno;

    @Column(length = 30, nullable = false, unique = true)
    @Schema(description = "Correo electrónico del usuario", example = "juan@example.com")
    private String correo;

    @Column(nullable = false)
    @Schema(description = "Contraseña encriptada del usuario", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String clave;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    @JsonIgnoreProperties("usuarios")
    @Schema(description = "Rol asignado al usuario")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    @JsonIgnoreProperties("usuarios")
    @Schema(description = "Estado actual del usuario (activo, inactivo, etc.)")
    private EstadoUsuario estado;
}