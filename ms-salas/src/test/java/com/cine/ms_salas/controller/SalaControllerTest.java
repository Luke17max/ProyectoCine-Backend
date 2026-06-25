package com.cine.ms_salas.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import com.cine.ms_salas.dto.SalaDTO;
import com.cine.ms_salas.service.ISalaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SalaController.class)
public class SalaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISalaService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testListarTodas_Success() throws Exception {
        SalaDTO dto1 = new SalaDTO();
        dto1.setId(1L);
        dto1.setNombre("Sala IMAX");
        dto1.setCapacidad(250);
        dto1.setSucursalId(10L);
        SalaDTO dto2 = new SalaDTO();
        dto2.setId(2L);
        dto2.setNombre("Sala Premium");
        dto2.setCapacidad(120);
        dto2.setSucursalId(10L);
        List<SalaDTO> list = Arrays.asList(dto1, dto2);
        when(service.listarTodas()).thenReturn(list);

        mockMvc.perform(get("/api/salas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre", is("Sala IMAX")))
                .andExpect(jsonPath("$[1].nombre", is("Sala Premium")));
    }

    @Test
    public void testObtenerPorId_Success() throws Exception {
        Long id = 1L;
        SalaDTO dto = new SalaDTO();
        dto.setId(id);
        dto.setNombre("Sala IMAX");
        dto.setCapacidad(250);
        dto.setSucursalId(10L);
        when(service.buscarPorId(id)).thenReturn(dto);

        mockMvc.perform(get("/api/salas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Sala IMAX")))
                .andExpect(jsonPath("$.capacidad", is(250)))
                .andExpect(jsonPath("$.sucursalId", is(10)));
    }

    @Test
    public void testListarPorSucursal_Success() throws Exception {
        Long sucId = 10L;
        SalaDTO dto = new SalaDTO();
        dto.setId(1L);
        dto.setNombre("Sala IMAX");
        dto.setCapacidad(250);
        dto.setSucursalId(sucId);
        when(service.buscarPorSucursal(sucId)).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/api/salas/sucursal/{sucursalId}", sucId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre", is("Sala IMAX")));
    }

    @Test
    public void testCrearSala_Success() throws Exception {
        SalaDTO input = new SalaDTO();
        input.setNombre("Sala Nueva");
        input.setCapacidad(150);
        input.setSucursalId(5L);
        SalaDTO saved = new SalaDTO();
        saved.setId(1L);
        saved.setNombre("Sala Nueva");
        saved.setCapacidad(150);
        saved.setSucursalId(5L);
        when(service.guardar(any(SalaDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/salas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Sala Nueva")));
    }

    @Test
    public void testActualizarSala_Success() throws Exception {
        Long id = 1L;
        SalaDTO input = new SalaDTO();
        input.setNombre("Sala Actualizada");
        input.setCapacidad(180);
        input.setSucursalId(6L);
        SalaDTO updated = new SalaDTO();
        updated.setId(id);
        updated.setNombre("Sala Actualizada");
        updated.setCapacidad(180);
        updated.setSucursalId(6L);
        when(service.actualizar(eq(id), any(SalaDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/salas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Sala Actualizada")))
                .andExpect(jsonPath("$.capacidad", is(180)))
                .andExpect(jsonPath("$.sucursalId", is(6)));
    }

    @Test
    public void testEliminarSala_Success() throws Exception {
        Long id = 1L;
        doNothing().when(service).eliminar(id);
        mockMvc.perform(delete("/api/salas/{id}", id))
                .andExpect(status().isNoContent());
        verify(service, times(1)).eliminar(id);
    }
}
