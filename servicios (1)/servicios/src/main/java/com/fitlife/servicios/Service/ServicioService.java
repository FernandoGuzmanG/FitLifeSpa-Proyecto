package com.fitlife.servicios.Service;

import org.springframework.beans.factory.annotation.Autowired;

import com.fitlife.servicios.Model.Servicio;
import com.fitlife.servicios.Repository.ServicioRepository;

public class ServicioService {
    @Autowired
    private ServicioRepository servicioRepository;

    public Servicio save(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    public void deleteById(Long id){
        servicioRepository.deleteById(id);
    }


    
    

}
