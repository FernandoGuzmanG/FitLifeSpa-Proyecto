package com.fitlifespa.microservice_usuarios.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitlifespa.microservice_usuarios.model.Enum.NombreRol;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidad que representa el rol asignado a un usuario (por ejemplo: CLIENTE, ADMINISTRADOR)")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del rol", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idRol;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Nombre del rol", example = "CLIENTE")
    private NombreRol nombreRol;

    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    @JsonIgnore
    @Schema(description = "Lista de usuarios que tienen este rol", hidden = true)
    private List<Usuario> usuarios;

}