package com.fitlifespa.microservice_resenas.controller;

import com.fitlifespa.microservice_resenas.hateoas.ResenaModelAssembler;
import com.fitlifespa.microservice_resenas.model.Enum.EstadoResena;
import com.fitlifespa.microservice_resenas.model.Resena;
import com.fitlifespa.microservice_resenas.security.RoleValidator;
import com.fitlifespa.microservice_resenas.service.ResenaService;
import com.fitlifespa.microservice_resenas.hateoas.ResenaConPromedioModelAssembler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ResenaController.class)
class ResenaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResenaService resenaService;

    @MockBean
    private RoleValidator validator;

    @MockBean
    private ResenaModelAssembler resenaModelAssembler;

    @MockBean
    private ResenaConPromedioModelAssembler resenaConPromedioModelAssembler;

    @Test
    void testCrearResenaDebeRetornarOk() throws Exception {
        Long idUsuario = 42L;
        Resena entrada = new Resena();
        entrada.setIdServicio(101L);
        entrada.setComentario("Muy buen servicio");
        entrada.setEstado(EstadoResena.DESBANEADO);
        entrada.setCalificacion(4.5);

        Resena creada = new Resena();
        creada.setId(1L);
        creada.setIdUsuario(idUsuario);
        creada.setIdServicio(101L);
        creada.setComentario("Muy buen servicio");
        creada.setCalificacion(4.5);
        creada.setEstado(EstadoResena.DESBANEADO);
        creada.setFechaResena(LocalDate.now());

        EntityModel<Resena> model = EntityModel.of(creada);

        when(validator.getUserId(any())).thenReturn(idUsuario);
        doNothing().when(validator).requireRole(any(), eq("CLIENTE"));
        when(resenaService.crear(eq(idUsuario), eq(101L), eq("Muy buen servicio"), eq(4.5)))
                .thenReturn(creada);
        when(resenaModelAssembler.toModel(creada)).thenReturn(model);

        mockMvc.perform(post("/api/resenas")
                        .contentType("application/json")
                        .content("""
                                    {
                                      "idServicio": 101,
                                      "comentario": "Muy buen servicio",
                                      "calificacion": 4.5
                                    }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1));
    }

    @Test
    void testCrearResenaDebeRetornarBadRequestSiError() throws Exception {
        Long idUsuario = 42L;

        when(validator.getUserId(any())).thenReturn(idUsuario);
        doNothing().when(validator).requireRole(any(), eq("CLIENTE"));
        when(resenaService.crear(eq(idUsuario), eq(101L), anyString(), eq(4.5)))
                .thenThrow(new IllegalArgumentException("Servicio no válido"));

        mockMvc.perform(post("/api/resenas")
                        .contentType("application/json")
                        .content("""
                            {
                              "idServicio": 101,
                              "comentario": "Muy buen servicio",
                              "calificacion": 4.5
                            }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Servicio no válido"));
    }

    @Test
    void testMisResenasDebeRetornarOkConDatos() throws Exception {
        Long idUsuario = 42L;

        Resena resena = new Resena();
        resena.setId(1L);
        resena.setIdUsuario(idUsuario);
        resena.setIdServicio(101L);
        resena.setComentario("Comentario 1");
        resena.setCalificacion(4.5);
        resena.setEstado(EstadoResena.DESBANEADO);
        resena.setFechaResena(LocalDate.now());

        doNothing().when(validator).requireRole(any(HttpServletRequest.class), eq("CLIENTE"));
        when(validator.getUserId(any(HttpServletRequest.class))).thenReturn(idUsuario);
        when(resenaService.obtenerPorUsuario(eq(idUsuario))).thenReturn(List.of(resena));
        when(resenaModelAssembler.toModel(any())).thenReturn(EntityModel.of(resena));

        mockMvc.perform(get("/api/resenas/mis"))
                .andExpect(status().isOk());
    }

    @Test
    void testMisResenasDebeRetornarNoContentSiNoHayDatos() throws Exception {
        Long idUsuario = 42L;

        doNothing().when(validator).requireRole(any(HttpServletRequest.class), eq("CLIENTE"));
        when(validator.getUserId(any(HttpServletRequest.class))).thenReturn(idUsuario);
        when(resenaService.obtenerPorUsuario(eq(idUsuario))).thenReturn(List.of());

        mockMvc.perform(get("/api/resenas/mis"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testPromedioPorServicioDebeRetornarOk() throws Exception {
        Long idServicio = 101L;
        double promedio = 4.3;

        doNothing().when(validator).requireRole(any(), eq("CLIENTE"), eq("ENTRENADOR"), eq("COORDINADOR"), eq("ADMINISTRADOR"));
        when(resenaService.obtenerPromedioCalificacionPorServicio(idServicio, EstadoResena.DESBANEADO)).thenReturn(promedio);

        mockMvc.perform(get("/api/resenas/servicio/{idServicio}/promedio", idServicio))
                .andExpect(status().isOk());

    }

    @Test
    void testPromedioPorServicioDebeRetornarNoContentSiPromedioCero() throws Exception {
        Long idServicio = 101L;

        doNothing().when(validator).requireRole(any(), eq("CLIENTE"), eq("ENTRENADOR"), eq("COORDINADOR"), eq("ADMINISTRADOR"));
        when(resenaService.obtenerPromedioCalificacionPorServicio(idServicio, EstadoResena.DESBANEADO)).thenReturn(0.0);

        mockMvc.perform(get("/api/resenas/servicio/{idServicio}/promedio", idServicio))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.mensaje").value("Este servicio aún no tiene calificaciones."));
    }

    @Test
    void testBuscarResenaDebeRetornarOk() throws Exception {
        Long idResena = 2L;

        Resena resena = new Resena();
        resena.setId(idResena);
        resena.setEstado(EstadoResena.DESBANEADO);
        resena.setComentario("Excelente servicio");
        resena.setCalificacion(5.0);

        EntityModel<Resena> model = EntityModel.of(resena);

        doNothing().when(validator).requireRole(any(), eq("ENTRENADOR"), eq("COORDINADOR"), eq("CLIENTE"), eq("SOPORTE"), eq("ADMINISTRADOR"));
        when(resenaService.buscarPorEstadoId(EstadoResena.DESBANEADO, idResena)).thenReturn(resena);
        when(resenaConPromedioModelAssembler.toModel(resena)).thenReturn(model);

        mockMvc.perform(get("/api/resenas/{id}", idResena))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(idResena));
    }
    @Test
    void testBuscarResenaDebeRetornarNotFoundSiNoExiste() throws Exception {
        Long idResena = 2L;

        doNothing().when(validator).requireRole(any(), eq("ENTRENADOR"), eq("COORDINADOR"), eq("CLIENTE"), eq("SOPORTE"), eq("ADMINISTRADOR"));
        when(resenaService.buscarPorEstadoId(EstadoResena.DESBANEADO, idResena))
                .thenThrow(new RuntimeException("No encontrada o baneada"));

        mockMvc.perform(get("/api/resenas/{id}", idResena))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").value("Reseña no encontrada o se encuentra baneada."));
    }

    @Test
    void testObtenerResenasBaneadasDebeRetornarOk() throws Exception {
        Resena resenaBaneada = new Resena();
        resenaBaneada.setId(1L);
        resenaBaneada.setComentario("Contenido inapropiado");
        resenaBaneada.setEstado(EstadoResena.BANEADO);
        resenaBaneada.setIdServicio(101L);
        resenaBaneada.setIdUsuario(42L);
        resenaBaneada.setFechaResena(LocalDate.now());
        resenaBaneada.setCalificacion(1.0);

        List<Resena> lista = List.of(resenaBaneada);

        when(validator.getUserId(any())).thenReturn(99L);
        doNothing().when(validator).requireRole(any(), eq("SOPORTE"), eq("ADMINISTRADOR"));
        when(resenaService.buscarPorEstado(EstadoResena.BANEADO)).thenReturn(lista);

        mockMvc.perform(get("/api/resenas/baneadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.resenaList[0].id").value(1))
                .andExpect(jsonPath("_embedded.resenaList[0].comentario").value("Contenido inapropiado"));
    }

    @Test
    void testObtenerResenasBaneadasDebeRetornarNoContentSiVacio() throws Exception {
        when(validator.getUserId(any())).thenReturn(99L);
        doNothing().when(validator).requireRole(any(), eq("SOPORTE"), eq("ADMINISTRADOR"));
        when(resenaService.buscarPorEstado(EstadoResena.BANEADO)).thenReturn(List.of());

        mockMvc.perform(get("/api/resenas/baneadas"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.mensaje").value("No hay reseñas baneadas."));
    }

    @Test
    void testBanearDebeRetornarOk() throws Exception {
        Long idResena = 5L;

        Resena resenaBaneada = new Resena();
        resenaBaneada.setId(idResena);
        resenaBaneada.setComentario("Comentario inapropiado");
        resenaBaneada.setEstado(EstadoResena.BANEADO);
        resenaBaneada.setIdServicio(101L);
        resenaBaneada.setIdUsuario(42L);
        resenaBaneada.setFechaResena(LocalDate.now());
        resenaBaneada.setCalificacion(1.0);

        EntityModel<Resena> model = EntityModel.of(resenaBaneada);
        model.add(linkTo(methodOn(ResenaController.class).desbanear(idResena, null)).withRel("desbanear"));

        doNothing().when(validator).requireRole(any(), eq("ADMINISTRADOR"), eq("SOPORTE"));
        when(resenaService.banear(idResena)).thenReturn(resenaBaneada);

        mockMvc.perform(put("/api/resenas/banear/{id}", idResena))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(idResena));
    }

    @Test
    void testBanearDebeRetornarNotFoundSiError() throws Exception {
        Long idResena = 99L;

        doNothing().when(validator).requireRole(any(), eq("ADMINISTRADOR"), eq("SOPORTE"));
        when(resenaService.banear(idResena)).thenThrow(new RuntimeException("Reseña no encontrada o ya está baneada."));

        mockMvc.perform(put("/api/resenas/banear/{id}", idResena))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").value("Reseña no encontrada o ya está baneada."));
    }

    @Test
    void testDesbanearDebeRetornarOk() throws Exception {
        Long idResena = 6L;

        Resena desbaneada = new Resena();
        desbaneada.setId(idResena);
        desbaneada.setComentario("Comentario rehabilitado");
        desbaneada.setEstado(EstadoResena.DESBANEADO);
        desbaneada.setIdServicio(101L);
        desbaneada.setIdUsuario(42L);
        desbaneada.setFechaResena(LocalDate.now());
        desbaneada.setCalificacion(4.0);

        EntityModel<Resena> model = EntityModel.of(desbaneada);
        model.add(linkTo(methodOn(ResenaController.class).desbanear(idResena, null)).withRel("banear"));

        doNothing().when(validator).requireRole(any(), eq("ADMINISTRADOR"), eq("SOPORTE"));
        when(resenaService.desbanear(idResena)).thenReturn(desbaneada);

        mockMvc.perform(put("/api/resenas/desbanear/{id}", idResena))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(idResena));
    }

    @Test
    void testModificarResenaDebeRetornarOk() throws Exception {
        Long idUsuario = 42L;
        Long idResena = 3L;

        Resena entrada = new Resena();
        entrada.setComentario("Actualizado");
        entrada.setCalificacion(4.8);

        Resena modificada = new Resena();
        modificada.setId(idResena);
        modificada.setIdUsuario(idUsuario);
        modificada.setIdServicio(101L);
        modificada.setComentario("Actualizado");
        modificada.setCalificacion(4.8);
        modificada.setFechaResena(LocalDate.now());
        modificada.setEstado(EstadoResena.DESBANEADO);

        EntityModel<Resena> model = EntityModel.of(modificada);

        doNothing().when(validator).requireRole(any(), eq("CLIENTE"));
        when(validator.getUserId(any())).thenReturn(idUsuario);
        when(resenaService.modificarResena(eq(idUsuario), eq(idResena), any(Resena.class)))
                .thenReturn(modificada);
        when(resenaModelAssembler.toModel(modificada)).thenReturn(model);

        mockMvc.perform(put("/api/resenas/modificar/{id}", idResena)
                        .contentType("application/json")
                        .content("""
                            {
                                "comentario": "Actualizado",
                                "calificacion": 4.8
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(idResena));
    }

    @Test
    void testModificarResenaDebeRetornarNotFoundSiNoExiste() throws Exception {
        Long idUsuario = 42L;
        Long idResena = 3L;

        doNothing().when(validator).requireRole(any(), eq("CLIENTE"));
        when(validator.getUserId(any())).thenReturn(idUsuario);
        when(resenaService.modificarResena(eq(idUsuario), eq(idResena), any(Resena.class)))
                .thenThrow(new RuntimeException("Reseña no encontrada o no es suya."));

        mockMvc.perform(put("/api/resenas/modificar/{id}", idResena)
                        .contentType("application/json")
                        .content("""
                            {
                                "comentario": "Intento inválido",
                                "calificacion": 3.0
                            }
                            """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").value("Reseña no encontrada o no es suya."));
    }

    @Test
    void testEliminarResenaDebeRetornarNoContent() throws Exception {
        Long idUsuario = 42L;
        Long idResena = 3L;

        doNothing().when(validator).requireRole(any(), eq("CLIENTE"));
        when(validator.getUserId(any())).thenReturn(idUsuario);
        doNothing().when(resenaService).eliminarResenaUsuario(idUsuario, idResena);

        mockMvc.perform(delete("/api/resenas/eliminar/{id}", idResena))
                .andExpect(status().isNoContent());
    }



}




