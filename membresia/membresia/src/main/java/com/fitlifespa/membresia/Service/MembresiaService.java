package com.fitlifespa.membresia.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fitlifespa.membresia.Model.Estado;
import com.fitlifespa.membresia.Model.Membresia;
import com.fitlifespa.membresia.Repository.EstadoRepository;
import com.fitlifespa.membresia.Repository.MembresiaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MembresiaService {
    @Autowired
    private MembresiaRepository membresiaRepository;
    private EstadoRepository estadoRepository;

    public List<Membresia> findAll(){
        return membresiaRepository.findAll();
    }


    public Membresia createMembresia(Membresia membresia){
        return  membresiaRepository.save(membresia);
    }


    public List<Membresia> getMembresiaPorUsuario(Long usuarioId) {
        return membresiaRepository.findByUsuarioId(usuarioId);
    }


    public Membresia desactivarMembresia(Long id) {
        Membresia membresia = membresiaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Membresía no encontrada"));

        Estado estadoInactivo = estadoRepository.findByNombre("Inactivo")
            .orElseThrow(() -> new RuntimeException("Estado 'Inactivo' no encontrado"));

        membresia.setEstado(estadoInactivo);
        return membresiaRepository.save(membresia);

    } 

    
    public Membresia updatMembresia(Membresia membresia){
        return membresiaRepository.save(membresia);
    }

    public Membresia findById(Long id) {
    return membresiaRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Membresía no encontrada con ID: " + id));
    }
}





