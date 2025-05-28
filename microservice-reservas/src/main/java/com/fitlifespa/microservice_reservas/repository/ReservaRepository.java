package com.fitlifespa.microservice_reservas.repository;

import com.fitlifespa.microservice_reservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByIdUsuarioOrderByFechaDesc(Long idUsuario);
    List<Reserva> findByIdEntrenadorOrderByFechaDesc(Long idEntrenador);

    @Query("SELECT r.idServicio, COUNT(r) FROM Reserva r GROUP BY r.idServicio")
    List<Object[]> countReservasByServicio();

    @Query("SELECT r.idServicio, COUNT(r) FROM Reserva r WHERE r.fecha = :fecha GROUP BY r.idServicio")
    List<Object[]> countReservasByServicioAndFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT r.idServicio, COUNT(r) as cantidad FROM Reserva r GROUP BY r.idServicio ORDER BY cantidad DESC")
    List<Object[]> topServicios();
}

