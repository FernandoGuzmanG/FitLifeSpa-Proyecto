package com.fitlifespa.membresia.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlifespa.membresia.Model.Incluido;
import com.fitlifespa.membresia.Repository.IncluidoRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class IncluidoService {
    @Autowired
    private IncluidoRepository incluidoRepository;

    public List<Incluido> findByPlan(Long idPlan) {
    return incluidoRepository.findByPlanId(idPlan);
    }

    public List<Incluido> getAllIncluido(){
        return incluidoRepository.findAll();
    }
}