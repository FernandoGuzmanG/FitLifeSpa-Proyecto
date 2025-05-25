package com.fitlifespa.membresia.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlifespa.membresia.Model.Incluido;

@Repository
public interface IncluidoRepository extends JpaRepository<Incluido, Long>{
    List<Incluido> findByPlanId(Long idPlan);

    List<Incluido> obtenerIncluidos();


}
