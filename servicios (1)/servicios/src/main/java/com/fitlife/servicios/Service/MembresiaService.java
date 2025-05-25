package com.fitlife.servicios.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlife.servicios.Model.Membresia;
import com.fitlife.servicios.Repository.MembresiaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MembresiaService {
    @Autowired
    private MembresiaRepository membresiaRepository;

    public List<Membresia> findAll(){
        return membresiaRepository.findAll();
    }

    public Membresia findById(Long id){
        return membresiaRepository.findById(id).orElseThrow(()-> new RuntimeException("Membresia no encontrado con ID: "+ id));
    }

    public Membresia save(Membresia membresia) {
        return membresiaRepository.save(membresia);
    }

    public void deleteById(Long id) {
        membresiaRepository.deleteById(id);
    }

}
