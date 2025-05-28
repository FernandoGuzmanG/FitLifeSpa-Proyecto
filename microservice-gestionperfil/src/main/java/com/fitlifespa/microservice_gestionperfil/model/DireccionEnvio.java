package com.fitlifespa.microservice_gestionperfil.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "direccion_envio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String calle;

    private String ciudad;

    private String region;

    private String codigoPostal;

    private String pais;

    private Long idUsuario;
}

