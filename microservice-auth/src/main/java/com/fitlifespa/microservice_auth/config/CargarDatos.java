package com.fitlifespa.microservice_auth.config;

import com.fitlifespa.microservice_auth.model.EstadoUsuario;
import com.fitlifespa.microservice_auth.model.Enum.NombreRol;
import com.fitlifespa.microservice_auth.model.Rol;
import com.fitlifespa.microservice_auth.model.Usuario;
import com.fitlifespa.microservice_auth.repository.EstadoUsuarioRepository;
import com.fitlifespa.microservice_auth.repository.RolRepository;
import com.fitlifespa.microservice_auth.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class CargarDatos {

    private final PasswordEncoder encoder;

    @Bean
    public CommandLineRunner seedData(
            RolRepository rolRepo,
            EstadoUsuarioRepository estadoUsuarioRepo,
            UsuarioRepository usuarioRepo
    ) {
        return args -> {
            for (NombreRol nombreRol : NombreRol.values()) {
                rolRepo.findByNombreRol(nombreRol)
                        .or(() -> Optional.of(rolRepo.save(new Rol(null, nombreRol, null))));
            }

            List<String> estados = List.of("ACTIVO", "INACTIVO", "SUSPENDIDO");
            for (String estado : estados) {
                estadoUsuarioRepo.findByNombreEstado(estado)
                        .or(() -> Optional.of(estadoUsuarioRepo.save(new EstadoUsuario(null, estado, null))));
            }

            if (usuarioRepo.findByCorreo("admin@fitlife.com").isEmpty()) {
                usuarioRepo.save(new Usuario(null, "12345678-9", "Admin", "Sys", "Admin", "One",
                        "admin@fitlife.com", encoder.encode("admin123"),
                        rolRepo.findByNombreRol(NombreRol.ADMINISTRADOR).get(),
                        estadoUsuarioRepo.findByNombreEstado("ACTIVO").get()));
            }

            if (usuarioRepo.findByCorreo("cliente@fitlife.com").isEmpty()) {
                usuarioRepo.save(new Usuario(null, "98765432-1", "Cliente", "Ejemplo", "Perez", "Lopez",
                        "cliente@fitlife.com", encoder.encode("cliente123"),
                        rolRepo.findByNombreRol(NombreRol.CLIENTE).get(),
                        estadoUsuarioRepo.findByNombreEstado("ACTIVO").get()));
            }

            if (usuarioRepo.findByCorreo("soporte@fitlife.com").isEmpty()) {
                usuarioRepo.save(new Usuario(null, "11223344-5", "Soporte", "Ejemplo", "Ramirez", "Mena",
                        "soporte@fitlife.com", encoder.encode("soporte123"),
                        rolRepo.findByNombreRol(NombreRol.SOPORTE).get(),
                        estadoUsuarioRepo.findByNombreEstado("ACTIVO").get()));
            }

            if (usuarioRepo.findByCorreo("coordinador@fitlife.com").isEmpty()) {
                usuarioRepo.save(new Usuario(null, "99887766-5", "Coordinador", "Ejemplo", "Soto", "Gomez",
                        "coordinador@fitlife.com", encoder.encode("coord123"),
                        rolRepo.findByNombreRol(NombreRol.COORDINADOR).get(),
                        estadoUsuarioRepo.findByNombreEstado("ACTIVO").get()));
            }

            if (usuarioRepo.findByCorreo("entrenador@fitlife.com").isEmpty()) {
                usuarioRepo.save(new Usuario(null, "55667788-0", "Entrenador", "Ejemplo", "Diaz", "Vega",
                        "entrenador@fitlife.com", encoder.encode("entrenador123"),
                        rolRepo.findByNombreRol(NombreRol.ENTRENADOR).get(),
                        estadoUsuarioRepo.findByNombreEstado("ACTIVO").get()));
            }
        };
    }
}
