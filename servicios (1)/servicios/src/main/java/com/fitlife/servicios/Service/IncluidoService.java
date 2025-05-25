package com.fitlife.servicios.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlife.servicios.Model.Incluido;
import com.fitlife.servicios.Repository.IncluidoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class IncluidoService {
    @Autowired
    private IncluidoRepository incluidoRepository;

    public List<Incluido> findAll(){
        return incluidoRepository.findAll();
    }

    public Incluido findById(Long id){
        return incluidoRepository.findById(id).orElseThrow(()->new RuntimeException("No se ha encontrado un servicio incluido con ID:"+ id));
    }

    public void deleteById(Long id){
        incluidoRepository.deleteById(id);
    }







}
