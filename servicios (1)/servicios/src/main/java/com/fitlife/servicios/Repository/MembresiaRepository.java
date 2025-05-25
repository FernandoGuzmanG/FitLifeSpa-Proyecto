package com.fitlife.servicios.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlife.servicios.Model.Membresia;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long > {

}
