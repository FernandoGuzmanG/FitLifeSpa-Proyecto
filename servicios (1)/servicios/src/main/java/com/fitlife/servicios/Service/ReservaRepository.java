package com.fitlife.servicios.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitlife.servicios.Model.Reserva;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReservaRepository {
    @Autowired
    private ReservaRepository reservaRepository;

    public Reserva save(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }
  




}
