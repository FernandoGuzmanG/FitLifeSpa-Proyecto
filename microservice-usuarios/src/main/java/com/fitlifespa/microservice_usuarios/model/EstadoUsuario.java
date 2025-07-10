package com.fitlifespa.microservice_usuarios.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "estado_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa el estado actual de un usuario (por ejemplo: ACTIVO, INACTIVO)")
public class EstadoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del estado", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idEstado;

    @Column(nullable = false, length = 30, unique = true)
    @Schema(description = "Nombre del estado del usuario", example = "ACTIVO")
    private String nombreEstado;

    @OneToMany(mappedBy = "estado", cascade = CascadeType.ALL)
    @JsonIgnore
    @Schema(description = "Lista de usuarios con este estado", hidden = true)
    private List<Usuario> usuarios;
}