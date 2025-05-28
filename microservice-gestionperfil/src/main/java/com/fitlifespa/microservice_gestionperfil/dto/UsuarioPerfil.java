package com.fitlifespa.microservice_gestionperfil.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPerfil {
    private String rut;
    private String pnombre;
    private String snombre;
    private String appaterno;
    private String apmaterno;
    private String correo;
}
