package com.fitlifespa.reservas.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fitlifespa.reservas.model.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository <Reserva, Long> {

}
