package com.fitlife.servicios.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fitlife.servicios.Model.Resenia;
import com.fitlife.servicios.Repository.ReseniaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReseniaService {
    @Autowired
    private ReseniaRepository reseniaRepository;

    public Resenia save(Resenia resenia) {
        return reseniaRepository.save(resenia);
    }



}
