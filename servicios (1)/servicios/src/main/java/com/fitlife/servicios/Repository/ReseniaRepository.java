package com.fitlife.servicios.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlife.servicios.Model.Resenia;

@Repository
public interface ReseniaRepository extends JpaRepository<Resenia, Long> {

}
