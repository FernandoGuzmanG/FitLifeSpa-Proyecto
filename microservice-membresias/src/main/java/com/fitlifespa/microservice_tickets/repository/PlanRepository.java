package com.fitlifespa.microservice_tickets.repository;

import com.fitlifespa.microservice_tickets.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
}
