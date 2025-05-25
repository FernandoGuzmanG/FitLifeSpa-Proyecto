package com.fitlifespa.membresia.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.fitlifespa.membresia.Model.Membresia;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface MembresiaRepository extends JpaRepository<Membresia, Long>{

}
