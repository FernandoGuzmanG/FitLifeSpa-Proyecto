package com.fitlifespa.microservice_membresias.repository;

import com.fitlifespa.microservice_membresias.model.Incluido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncluidoRepository extends JpaRepository<Incluido, Long> {

    List<Incluido> findByPlanIdPlan(Long idPlan);
}
