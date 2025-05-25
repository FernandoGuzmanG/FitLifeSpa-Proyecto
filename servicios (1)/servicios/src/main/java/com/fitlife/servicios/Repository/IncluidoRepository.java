package com.fitlife.servicios.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlife.servicios.Model.Incluido;

@Repository
public interface IncluidoRepository extends JpaRepository<Incluido, Long> {

}
