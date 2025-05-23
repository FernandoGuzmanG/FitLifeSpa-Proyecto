package com.fitlifespa.membresia.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fitlifespa.membresia.Model.Plan;
import com.fitlifespa.membresia.Repository.PlanRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PlanService {
    @Autowired
    private PlanRepository planRepository;

    public Plan findById(Long id){
        return planRepository.findById(id).orElseThrow(()-> new RuntimeException("plan no encontrado con id: "+ id));
    }

    public List<Plan> findAll(){
        return planRepository.findAll();
    }

    public Plan updatePlan(Plan plan){
        return planRepository.save(plan);
    }

    public Plan save(Plan plan) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

}
