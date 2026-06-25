package com.cine.ms_reservas.controller;

import com.cine.ms_reservas.dto.ReservaDTO;
import com.cine.ms_reservas.service.IReservaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaController.class)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IReservaService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("GET /api/reservas - listar todas las reservas")
    void listar_DeberiaRetornarListaDeReservas() throws Exception {
        // ARRANGE
        ReservaDTO r1 = new ReservaDTO();
        r1.setId(1L);
        r1.setUsuarioId(10L);
        r1.setFuncionId(5L);
        r1.setCantidadAsientos(2);
        r1.setEstado("PENDIENTE");
        ReservaDTO r2 = new ReservaDTO();
        r2.setId(2L);
        r2.setUsuarioId(11L);
        r2.setFuncionId(6L);
        r2.setCantidadAsientos(4);
        r2.setEstado("PAGADA");
        List<ReservaDTO> lista = Arrays.asList(r1, r2);
        when(service.listarTodas()).thenReturn(lista);

        // ACT & ASSERT
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservas")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].estado").value("PAGADA"));
        // VERIFY
        Mockito.verify(service).listarTodas();
    }

    @Test
    @DisplayName("GET /api/reservas/{id} - obtener reserva por id")
    void obtenerPorId_DeberiaRetornarReserva() throws Exception {
        // ARRANGE
        ReservaDTO reserva = new ReservaDTO();
        reserva.setId(1L);
        reserva.setUsuarioId(10L);
        reserva.setFuncionId(5L);
        reserva.setCantidadAsientos(2);
        reserva.setEstado("PENDIENTE");
        when(service.buscarPorId(1L)).thenReturn(reserva);

        // ACT & ASSERT
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservas/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
        // VERIFY
        Mockito.verify(service).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/reservas/usuario/{usuarioId} - listar reservas por usuario")
    void obtenerPorUsuario_DeberiaRetornarLista() throws Exception {
        // ARRANGE
        ReservaDTO r1 = new ReservaDTO();
        r1.setId(1L);
        r1.setUsuarioId(10L);
        r1.setFuncionId(5L);
        r1.setCantidadAsientos(2);
        r1.setEstado("PENDIENTE");
        List<ReservaDTO> lista = Arrays.asList(r1);
        when(service.buscarPorUsuario(10L)).thenReturn(lista);

        // ACT & ASSERT
        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservas/usuario/10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(10));
        // VERIFY
        Mockito.verify(service).buscarPorUsuario(10L);
    }

    @Test
    @DisplayName("POST /api/reservas - crear nueva reserva")
    void crear_DeberiaRetornarReservaCreada() throws Exception {
        // ARRANGE
        ReservaDTO request = new ReservaDTO();
        request.setUsuarioId(10L);
        request.setFuncionId(5L);
        request.setCantidadAsientos(2);
        request.setEstado("PENDIENTE");
        ReservaDTO saved = new ReservaDTO();
        saved.setId(1L);
        saved.setUsuarioId(10L);
        saved.setFuncionId(5L);
        saved.setCantidadAsientos(2);
        saved.setEstado("PENDIENTE");
        when(service.guardar(Mockito.any(ReservaDTO.class))).thenReturn(saved);

        // ACT & ASSERT
        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
        // VERIFY
        Mockito.verify(service).guardar(Mockito.any(ReservaDTO.class));
    }

    @Test
    @DisplayName("PATCH /api/reservas/{id}/estado - cambiar estado de reserva")
    void cambiarEstado_DeberiaActualizarEstado() throws Exception {
        // ARRANGE
        ReservaDTO updated = new ReservaDTO();
        updated.setId(1L);
        updated.setEstado("PAGADA");
        when(service.actualizarEstado(1L, "PAGADA")).thenReturn(updated);

        // ACT & ASSERT
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/reservas/1/estado")
                .param("estado", "PAGADA")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("PAGADA"));
        // VERIFY
        Mockito.verify(service).actualizarEstado(1L, "PAGADA");
    }

    @Test
    @DisplayName("DELETE /api/reservas/{id} - eliminar reserva")
    void eliminar_DeberiaEliminarReserva() throws Exception {
        // ARRANGE
        Mockito.doNothing().when(service).eliminar(1L);

        // ACT & ASSERT
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservas/1"))
                .andExpect(status().isNoContent());
        // VERIFY
        Mockito.verify(service).eliminar(1L);
    }
}
