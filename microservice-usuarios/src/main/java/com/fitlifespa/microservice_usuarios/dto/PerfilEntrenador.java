package com.fitlifespa.microservice_usuarios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PerfilEntrenador {
    String rut;
    String pnombre;
    String snombre;
    String appaterno;
    String apmaterno;
    String correo;
    String clave;
    String confirmarClave;
    String direccion;
}