package com.fitlifespa.microservice_auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {
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

    @Column(nullable = false)
    private String clave;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    @JsonIgnoreProperties("usuario")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "id_estado")
    @JsonIgnoreProperties("usuario")
    private EstadoUsuario estado;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + rol.getNombreRol().name());
    }

    @Override
    public String getPassword() {
        return clave;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Cambia esto si tienes lógica para cuentas expiradas
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Cambia esto si manejas bloqueo de cuentas
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Cambia esto si manejas vencimiento de credenciales
    }

    @Override
    public boolean isEnabled() {
        return estado != null && estado.getNombreEstado().equalsIgnoreCase("ACTIVO");
    }

}