package com.fitlifespa.microservice_usuarios.service;

import com.fitlifespa.microservice_usuarios.model.Enum.NombreRol;
import com.fitlifespa.microservice_usuarios.model.Rol;
import com.fitlifespa.microservice_usuarios.repository.RolRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    @Test
    void testFindAllDebeRetornarListaDeRoles() {
        Rol rol1 = new Rol(1L, NombreRol.CLIENTE, List.of());
        Rol rol2 = new Rol(2L, NombreRol.ADMINISTRADOR, List.of());
        List<Rol> listaEsperada = List.of(rol1, rol2);

        when(rolRepository.findAll()).thenReturn(listaEsperada);

        List<Rol> resultado = rolService.findAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(NombreRol.CLIENTE, resultado.get(0).getNombreRol());
        verify(rolRepository, times(1)).findAll();
    }
    @Test
    void testFindAllDebeRetornarListaVacia() {
        when(rolRepository.findAll()).thenReturn(Collections.emptyList());

        List<Rol> resultado = rolService.findAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(rolRepository).findAll();
    }

    @Test
    void testFindByIdDebeRetornarRolCuandoExiste() {
        Long id = 1L;
        Rol rol = new Rol(id, NombreRol.CLIENTE, List.of());
        when(rolRepository.findById(id)).thenReturn(Optional.of(rol));

        Rol resultado = rolService.findById(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getIdRol());
        assertEquals(NombreRol.CLIENTE, resultado.getNombreRol());
    }

    @Test
    void testFindByIdDebeLanzarExcepcionSiNoExiste() {
        Long id = 99L;
        when(rolRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> rolService.findById(id));
        assertEquals("Rol no encontrado con ID: " + id, ex.getMessage());
    }

    @Test
    void testFindByNombreDebeRetornarRol() {
        String nombreRol = "CLIENTE";
        Rol rol = new Rol(1L, NombreRol.CLIENTE, List.of());

        when(rolRepository.findByNombre(nombreRol)).thenReturn(rol);

        Rol resultado = rolService.findByNombre(nombreRol);

        assertNotNull(resultado);
        assertEquals(NombreRol.CLIENTE, resultado.getNombreRol());
        verify(rolRepository).findByNombre(nombreRol);
    }

    @Test
    void testFindByNombreDebeRetornarNullSiNoExiste() {
        String nombreRol = "NO_EXISTE";
        when(rolRepository.findByNombre(nombreRol)).thenReturn(null);

        Rol resultado = rolService.findByNombre(nombreRol);

        assertNull(resultado);
        verify(rolRepository).findByNombre(nombreRol);
    }
}


