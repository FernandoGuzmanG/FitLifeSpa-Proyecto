package com.fitlifespa.microservice_gestionperfil.service;

import com.fitlifespa.microservice_gestionperfil.model.DireccionEnvio;
import com.fitlifespa.microservice_gestionperfil.repository.DireccionEnvioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DireccionEnvioService {

    private final DireccionEnvioRepository repo;

    public List<DireccionEnvio> listarPorUsuario(Long idUsuario) {
        return repo.findByIdUsuario(idUsuario);
    }

    public DireccionEnvio agregar(Long idUsuario, DireccionEnvio direccion) {
        direccion.setIdUsuario(idUsuario);
        return repo.save(direccion);
    }

    public Optional<DireccionEnvio> actualizar(Long idUsuario, Long id, DireccionEnvio nueva) {
        return repo.findById(id)
                .filter(d -> d.getIdUsuario().equals(idUsuario))
                .map(dir -> {
                    dir.setCalle(nueva.getCalle());
                    dir.setCiudad(nueva.getCiudad());
                    dir.setRegion(nueva.getRegion());
                    dir.setCodigoPostal(nueva.getCodigoPostal());
                    dir.setPais(nueva.getPais());
                    return repo.save(dir);
                });
    }

    public boolean eliminar(Long idUsuario, Long id) {
        return repo.findById(id)
                .filter(d -> d.getIdUsuario().equals(idUsuario))
                .map(d -> {
                    repo.delete(d);
                    return true;
                }).orElse(false);
    }
}
