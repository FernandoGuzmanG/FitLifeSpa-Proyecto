package com.fitlifespa.membresia.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlifespa.membresia.Model.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

}
