package com.fitlifespa.microservice_tickets.service;

import com.fitlifespa.microservice_tickets.model.Historial;
import com.fitlifespa.microservice_tickets.model.Ticket;
import com.fitlifespa.microservice_tickets.repository.HistorialRepository;
import com.fitlifespa.microservice_tickets.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorialServiceTest {

    @Mock
    private TicketRepository ticketRepo;

    @Mock
    private HistorialRepository historialRepo;

    @InjectMocks
    private HistorialService historialService;

    @Test
    void testAgregarEntrada() {
        Long idTicket = 1L;
        Ticket mockTicket = new Ticket(); // con setters si es necesario
        when(ticketRepo.findById(idTicket)).thenReturn(Optional.of(mockTicket));

        Historial guardado = new Historial();
        when(historialRepo.save(any(Historial.class))).thenReturn(guardado);

        Historial resultado = historialService.agregarEntrada(idTicket, "INFO", "mensaje test");

        assertNotNull(resultado);
        verify(ticketRepo).findById(idTicket);
        verify(historialRepo).save(any(Historial.class));
    }
    @Test
    void listarPorTicket_deberiaRetornarListaOrdenada() {
        Long idTicket = 1L;
        Historial h1 = new Historial();
        h1.setFechaMensaje(LocalDateTime.now().minusHours(2));
        Historial h2 = new Historial();
        h2.setFechaMensaje(LocalDateTime.now());

        when(historialRepo.findByTicketIdOrderByFechaMensajeAsc(idTicket))
                .thenReturn(List.of(h1, h2));

        List<Historial> resultado = historialService.listarPorTicket(idTicket);

        assertEquals(2, resultado.size());
        assertTrue(resultado.get(0).getFechaMensaje().isBefore(resultado.get(1).getFechaMensaje()));
        verify(historialRepo).findByTicketIdOrderByFechaMensajeAsc(idTicket);
    }
}
