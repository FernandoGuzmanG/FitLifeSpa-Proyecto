package com.fitlifespa.microservice_usuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PerfilCliente {
    String rut;
    String pnombre;
    String snombre;
    String appaterno;
    String apmaterno;
    String correo;
    String direccion;
}
