package com.fitlifespa.microservice_membresias.service;

import com.fitlifespa.microservice_membresias.model.Membresia;
import com.fitlifespa.microservice_membresias.repository.MembresiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MembresiaService {
    @Autowired
    private MembresiaRepository membresiaRepository;

    public List<Membresia> findAll(){
        return membresiaRepository.findAll();
    }

    public List<Membresia> findAllByIdUser(Long idUsuario) {
        return membresiaRepository.findByIdUsuarioOrderByFechaInicioDesc(idUsuario);
    }

    public Optional<Membresia> findById(Long id) {
        return membresiaRepository.findById(id);
    }

    public Optional<Membresia> obtenerMembresiaActual(Long idUsuario) {
        return membresiaRepository.findTopByIdUsuarioOrderByFechaInicioDesc(idUsuario);
    }

    public Membresia save(Membresia membresia) {
        return membresiaRepository.save(membresia);
    }

    public void deleteById(Long id) {
        membresiaRepository.deleteById(id);
    }

    public Membresia renovarMembresia(Long idUsuario, Long idMembresia) {

        Membresia membresia = membresiaRepository.findById(idMembresia)
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada con ID: " + idMembresia));

        if (membresia.getIdUsuario() == idUsuario) {
            membresia.setFechaTermino(membresia.getFechaTermino().plusDays(30));
            membresia.setCostoTotal(membresia.getCostoTotal() + membresia.getPlan().getCosto());
            return membresiaRepository.save(membresia);
        }

        throw new RuntimeException("La membresía no pertenece al usuario con ID: " + idUsuario);
    }


}
