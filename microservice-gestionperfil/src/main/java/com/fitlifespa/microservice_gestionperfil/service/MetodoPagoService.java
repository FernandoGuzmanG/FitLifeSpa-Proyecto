package com.fitlifespa.microservice_gestionperfil.service;

import com.fitlifespa.microservice_gestionperfil.model.MetodoPago;
import com.fitlifespa.microservice_gestionperfil.repository.MetodoPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    public List<MetodoPago> listarPorUsuario(Long idUsuario) {
        return metodoPagoRepository.findByIdUsuario(idUsuario);
    }

    public MetodoPago agregar(Long idUsuario, MetodoPago metodo) {
        metodo.setIdUsuario(idUsuario);
        return metodoPagoRepository.save(metodo);
    }

    public Optional<MetodoPago> actualizar(Long idUsuario, Long id, MetodoPago nuevo) {
        return metodoPagoRepository.findById(id)
                .filter(m -> m.getIdUsuario().equals(idUsuario))
                .map(mp -> {
                    mp.setTipo(nuevo.getTipo());
                    mp.setDetalle(nuevo.getDetalle());
                    return metodoPagoRepository.save(mp);
                });
    }

    public boolean eliminar(Long idUsuario, Long id) {
        return metodoPagoRepository.findById(id)
                .filter(m -> m.getIdUsuario().equals(idUsuario))
                .map(m -> {
                    metodoPagoRepository.delete(m);
                    return true;
                }).orElse(false);
    }
}

