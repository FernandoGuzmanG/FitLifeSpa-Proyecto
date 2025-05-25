package com.fitlifespa.membresia.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlifespa.membresia.Model.Estado;
import com.fitlifespa.membresia.Repository.EstadoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class EstadoService {
    @Autowired
    private EstadoRepository estadoRepository;

    public Estado findById(Long id){
        return estadoRepository.findById(id).orElseThrow(()-> new RuntimeException("Estado no encontrado con ID: "+ id));
    }

    public Estado findByNombre(String nombreEstado){
        return estadoRepository.findByNombre(nombreEstado);
    }
}