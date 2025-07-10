package com.fitlifespa.microservice_auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fitlifespa.microservice_auth.model.Enum.NombreRol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;


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

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(nombreRol.name()));
    }
}
