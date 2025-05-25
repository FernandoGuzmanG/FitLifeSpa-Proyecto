package com.fitlifespa.microservice_usuarios.service;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "direccion_cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionDeEnvio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDireccion;

    private Long idCliente;
    private String comuna;
    private String calle;
    private String region;
    private String codigoPostal;

    public String ToString(){
        return calle + ", " + comuna + ", " + region + ", " + codigoPostal;
    }

}
