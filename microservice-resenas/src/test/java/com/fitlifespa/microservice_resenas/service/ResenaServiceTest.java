package com.fitlifespa.microservice_resenas.service;

import com.fitlifespa.microservice_resenas.model.Enum.EstadoResena;
import com.fitlifespa.microservice_resenas.model.Resena;
import com.fitlifespa.microservice_resenas.repository.ResenaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @InjectMocks
    private ResenaService resenaService;

    @Test
    void testCrearResenaValida() {
        Long idUsuario = 1L;
        Long idServicio = 2L;
        String comentario = "Muy buen servicio";
        Double calificacion = 9.0;

        Resena resenaGuardada = new Resena();
        resenaGuardada.setId(1L);
        resenaGuardada.setIdUsuario(idUsuario);
        resenaGuardada.setIdServicio(idServicio);
        resenaGuardada.setComentario(comentario);
        resenaGuardada.setCalificacion(calificacion);
        resenaGuardada.setEstado(EstadoResena.DESBANEADO);
        resenaGuardada.setFechaResena(LocalDate.now());

        when(resenaRepository.save(any(Resena.class))).thenReturn(resenaGuardada);

        Resena result = resenaService.crear(idUsuario, idServicio, comentario, calificacion);

        assertNotNull(result);
        assertEquals(idUsuario, result.getIdUsuario());
        assertEquals(idServicio, result.getIdServicio());
        assertEquals(comentario, result.getComentario());
        assertEquals(calificacion, result.getCalificacion());
        assertEquals(EstadoResena.DESBANEADO, result.getEstado());
        verify(resenaRepository).save(any(Resena.class));
    }

    @Test
    void testCrearResenaCalificacionInvalida() {
        Double calificacion = 12.0;

        assertThrows(IllegalArgumentException.class, () ->
                resenaService.crear(1L, 1L, "Comentario", calificacion)
        );
    }

    @Test
    void testCrearResenaComentarioMuyLargo() {
        String comentario = "a".repeat(501);

        assertThrows(IllegalArgumentException.class, () ->
                resenaService.crear(1L, 1L, comentario, 8.0)
        );
    }

    @Test
    void testObtenerPromedioConResenas() {
        Long idServicio = 1L;
        EstadoResena estado = EstadoResena.DESBANEADO;

        Resena r1 = new Resena();
        r1.setCalificacion(8.0);
        Resena r2 = new Resena();
        r2.setCalificacion(6.0);

        when(resenaRepository.findByIdServicioAndEstado(idServicio, estado))
                .thenReturn(List.of(r1, r2));

        Double promedio = resenaService.obtenerPromedioCalificacionPorServicio(idServicio, estado);

        assertEquals(7.0, promedio);
    }

    @Test
    void testObtenerPromedioSinResenas() {
        Long idServicio = 2L;
        EstadoResena estado = EstadoResena.DESBANEADO;

        when(resenaRepository.findByIdServicioAndEstado(idServicio, estado))
                .thenReturn(List.of());

        Double promedio = resenaService.obtenerPromedioCalificacionPorServicio(idServicio, estado);

        assertEquals(0.0, promedio);
    }

    @Test
    void testBanearResena() {
        Long idResena = 1L;
        Resena resena = new Resena();
        resena.setId(idResena);
        resena.setEstado(EstadoResena.DESBANEADO);

        when(resenaRepository.findByEstadoAndId(EstadoResena.DESBANEADO, idResena))
                .thenReturn(resena);
        when(resenaRepository.save(any(Resena.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Resena result = resenaService.banear(idResena);

        assertNotNull(result);
        assertEquals(EstadoResena.BANEADO, result.getEstado());
        verify(resenaRepository).findByEstadoAndId(EstadoResena.DESBANEADO, idResena);
        verify(resenaRepository).save(resena);
    }

    @Test
    void testDesbanearResena() {
        Long idResena = 2L;
        Resena resena = new Resena();
        resena.setId(idResena);
        resena.setEstado(EstadoResena.BANEADO);

        when(resenaRepository.findByEstadoAndId(EstadoResena.BANEADO, idResena))
                .thenReturn(resena);
        when(resenaRepository.save(any(Resena.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Resena result = resenaService.desbanear(idResena);

        assertNotNull(result);
        assertEquals(EstadoResena.DESBANEADO, result.getEstado());
        verify(resenaRepository).findByEstadoAndId(EstadoResena.BANEADO, idResena);
        verify(resenaRepository).save(resena);
    }

    @Test
    void testModificarResenaValida() {
        // Arrange
        Long idUsuario = 1L;
        Long idResena = 10L;

        Resena original = new Resena();
        original.setId(idResena);
        original.setIdUsuario(idUsuario);
        original.setComentario("Original");
        original.setCalificacion(5.0);

        Resena nuevosDatos = new Resena();
        nuevosDatos.setComentario("Comentario actualizado");
        nuevosDatos.setCalificacion(8.0);

        when(resenaRepository.findByIdUsuarioAndId(idUsuario, idResena))
                .thenReturn(original);
        when(resenaRepository.save(any(Resena.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Resena actualizada = resenaService.modificarResena(idUsuario, idResena, nuevosDatos);

        // Assert
        assertNotNull(actualizada);
        assertEquals("Comentario actualizado", actualizada.getComentario());
        assertEquals(8.0, actualizada.getCalificacion());
        verify(resenaRepository).save(original);
    }

    @Test
    void testModificarResenaComentarioMuyLargo() {
        // Arrange
        Long idUsuario = 1L;
        Long idResena = 10L;

        Resena original = new Resena();
        original.setId(idResena);
        original.setIdUsuario(idUsuario);

        Resena nuevosDatos = new Resena();
        nuevosDatos.setComentario("a".repeat(501));
        nuevosDatos.setCalificacion(5.0);

        when(resenaRepository.findByIdUsuarioAndId(idUsuario, idResena))
                .thenReturn(original);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                resenaService.modificarResena(idUsuario, idResena, nuevosDatos)
        );
    }

    @Test
    void testModificarResenaCalificacionInvalida() {
        // Arrange
        Long idUsuario = 1L;
        Long idResena = 10L;

        Resena original = new Resena();
        original.setId(idResena);
        original.setIdUsuario(idUsuario);

        Resena nuevosDatos = new Resena();
        nuevosDatos.setComentario("Comentario válido");
        nuevosDatos.setCalificacion(11.0); // fuera de rango

        when(resenaRepository.findByIdUsuarioAndId(idUsuario, idResena))
                .thenReturn(original);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                resenaService.modificarResena(idUsuario, idResena, nuevosDatos)
        );
    }

    @Test
    void testEliminarResenaUsuario() {
        // Arrange
        Long idUsuario = 1L;
        Long idResena = 5L;

        Resena resena = new Resena();
        resena.setId(idResena);
        resena.setIdUsuario(idUsuario);

        when(resenaRepository.findByIdUsuarioAndId(idUsuario, idResena))
                .thenReturn(resena);

        // Act
        resenaService.eliminarResenaUsuario(idUsuario, idResena);

        // Assert
        verify(resenaRepository).findByIdUsuarioAndId(idUsuario, idResena);
        verify(resenaRepository).deleteById(idResena);
    }


}