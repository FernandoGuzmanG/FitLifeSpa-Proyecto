package com.fitlifespa.microservice_gestionperfil.service;

import com.fitlifespa.microservice_gestionperfil.dto.UsuarioPerfil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioClient {

    private final WebClient.Builder webClientBuilder;

    private final String BASE_URL = "http://localhost:8082/api/usuarios";

    public UsuarioPerfil verPerfil(Long idUsuario) {
        return webClientBuilder.build()
                .get()
                .uri(BASE_URL + "/perfil")
                .header("X-User-Id", idUsuario.toString())
                .retrieve()
                .bodyToMono(UsuarioPerfil.class)
                .block();
    }

    public UsuarioPerfil actualizarPerfil(Long idUsuario, UsuarioPerfil nuevosDatos) {
        return webClientBuilder.build()
                .put()
                .uri(BASE_URL + "/perfil")
                .header("X-User-Id", idUsuario.toString())
                .bodyValue(nuevosDatos)
                .retrieve()
                .bodyToMono(UsuarioPerfil.class)
                .block();
    }

    public void cambiarClave(Long idUsuario, String nueva, String confirmar) {
        Map<String, String> body = new HashMap<>();
        body.put("nuevaContrasena", nueva);
        body.put("confirmarContrasena", confirmar);

        webClientBuilder.build()
                .put()
                .uri(BASE_URL + "/" + idUsuario + "/cambiar-password")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}

