package com.fitlifespa.microservice_auth.hateoas;

import com.fitlifespa.microservice_auth.dto.LoginResponse;
import com.fitlifespa.microservice_auth.model.Usuario;

import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

@Component
public class LoginResponseAssembler {

    public LoginResponse toModel(String token, Usuario usuario) {
        LoginResponse response = new LoginResponse(token);

        String rol = usuario.getRol().getNombreRol().name();

        switch (rol) {
            case "CLIENTE" -> {
                response.add(Link.of("http://localhost:8080/api/usuarios/perfil").withRel("ver_perfil"));
                response.add(Link.of("http://localhost:8080/api/usuarios/perfil").withRel("actualizar_perfil"));
                response.add(Link.of("http://localhost:8080/api/usuarios/cambiar-clave").withRel("cambiar_clave"));

            }
            case "ENTRENADOR" -> {
                response.add(Link.of("http://localhost:8080/api/usuarios/perfil").withRel("ver_perfil"));
                response.add(Link.of("http://localhost:8080/api/usuarios/perfil").withRel("actualizar_perfil"));
                response.add(Link.of("http://localhost:8080/api/usuarios/cambiar-clave").withRel("cambiar_clave"));
            }
            case "COORDINADOR"-> {
                response.add(Link.of("http://localhost:8080/api/usuarios/perfil").withRel("ver_perfil"));
                response.add(Link.of("http://localhost:8080/api/usuarios/perfil").withRel("actualizar_perfil"));
                response.add(Link.of("http://localhost:8080/api/usuarios/cambiar-clave").withRel("cambiar_clave"));
            }
            case "SOPORTE" -> {
                response.add(Link.of("http://localhost:8080/api/usuarios/perfil").withRel("ver_perfil"));
                response.add(Link.of("http://localhost:8080/api/usuarios/perfil").withRel("actualizar_perfil"));
                response.add(Link.of("http://localhost:8080/api/usuarios/cambiar-clave").withRel("cambiar_clave"));

            }
            case "ADMINISTRADOR" -> {
                response.add(Link.of("http://localhost:8080/api/usuarios/perfil").withRel("ver_perfil"));
                response.add(Link.of("http://localhost:8080/api/usuarios/perfil").withRel("actualizar_perfil"));
                response.add(Link.of("http://localhost:8080/api/usuarios/cambiar-clave").withRel("cambiar_clave"));
            }
        }
        return response;
    }
}