package com.cine.ms_confiteria.controller;

import com.cine.ms_confiteria.dto.ConfiteriaDTO;
import com.cine.ms_confiteria.service.IConfiteriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link ConfiteriaController}.
 *
 * Strategy:
 *   • @WebMvcTest – loads only the web layer.
 *   • @MockitoBean – mocks {@link IConfiteriaService} (no DB, no Eureka).
 *   • MockMvc – simulates HTTP calls.
 *   • AAA pattern (Arrange, Act, Assert) + VERIFY.
 */
@WebMvcTest(ConfiteriaController.class)
class ConfiteriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IConfiteriaService service;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------------------------------------------------------------------
    // TEST 1 – GET /api/confiteria : listar todos
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/confiteria → HTTP 200 + lista de confitería")
    void testListar_debeRetornar200ConLista() throws Exception {
        // ARRANGE
        ConfiteriaDTO item1 = new ConfiteriaDTO();
        item1.setId(1L);
        item1.setNombre("Palomitas de Mantequilla");
        item1.setPrecio(new BigDecimal("3500.00"));
        item1.setStock(50);
        item1.setCategoria("SNACK");

        ConfiteriaDTO item2 = new ConfiteriaDTO();
        item2.setId(2L);
        item2.setNombre("Bebida Cola Grande");
        item2.setPrecio(new BigDecimal("2500.00"));
        item2.setStock(100);
        item2.setCategoria("BEBIDA");

        when(service.listarTodos()).thenReturn(List.of(item1, item2));

        // ACT & ASSERT
        mockMvc.perform(get("/api/confiteria")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Palomitas de Mantequilla"))
                .andExpect(jsonPath("$[0].precio").value(3500.00))
                .andExpect(jsonPath("$[0].stock").value(50))
                .andExpect(jsonPath("$[0].categoria").value("SNACK"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nombre").value("Bebida Cola Grande"))
                .andExpect(jsonPath("$[1].categoria").value("BEBIDA"));

        verify(service, times(1)).listarTodos();
    }

    // ---------------------------------------------------------------------
    // TEST 2a – GET /api/confiteria/{id} : caso éxito
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/confiteria/{id} → 200 + DTO cuando el ID existe")
    void testObtenerPorId_debeRetornar200CuandoExiste() throws Exception {
        // ARRANGE
        ConfiteriaDTO dto = new ConfiteriaDTO();
        dto.setId(1L);
        dto.setNombre("Palomitas de Mantequilla");
        dto.setPrecio(new BigDecimal("3500.00"));
        dto.setStock(50);
        dto.setCategoria("SNACK");

        when(service.buscarPorId(1L)).thenReturn(dto);

        // ACT & ASSERT
        mockMvc.perform(get("/api/confiteria/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Palomitas de Mantequilla"))
                .andExpect(jsonPath("$.precio").value(3500.00))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.categoria").value("SNACK"));

        verify(service, times(1)).buscarPorId(1L);
    }

    // ---------------------------------------------------------------------
    // TEST 2b – GET /api/confiteria/{id} : caso no encontrado (404)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/confiteria/{id} → 404 cuando el ID no existe")
    void testObtenerPorId_debeRetornar404CuandoNoExiste() throws Exception {
        // ARRANGE – el service lanza RuntimeException; GlobalExceptionHandler la traduce a 404
        when(service.buscarPorId(99L))
                .thenThrow(new RuntimeException("Confitería con id 99 no encontrada"));

        // ACT & ASSERT
        mockMvc.perform(get("/api/confiteria/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Confitería con id 99 no encontrada"));

        verify(service, times(1)).buscarPorId(99L);
    }

    // ---------------------------------------------------------------------
    // TEST 3 – GET /api/confiteria/categoria/{categoria} : caso éxito
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/confiteria/categoria/{categoria} → 200 + lista cuando existen items")
    void testBuscarPorCategoria_debeRetornar200CuandoExiste() throws Exception {
        // ARRANGE
        ConfiteriaDTO dto = new ConfiteriaDTO();
        dto.setId(1L);
        dto.setNombre("Palomitas de Mantequilla");
        dto.setPrecio(new BigDecimal("3500.00"));
        dto.setStock(50);
        dto.setCategoria("SNACK");

        when(service.buscarPorCategoria("SNACK")).thenReturn(List.of(dto));

        // ACT & ASSERT
        mockMvc.perform(get("/api/confiteria/categoria/SNACK")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Palomitas de Mantequilla"))
                .andExpect(jsonPath("$[0].precio").value(3500.00))
                .andExpect(jsonPath("$[0].stock").value(50))
                .andExpect(jsonPath("$[0].categoria").value("SNACK"));

        verify(service, times(1)).buscarPorCategoria("SNACK");
    }
    // ---------------------------------------------------------------------
    // TEST 4 – POST /api/confiteria : caso éxito (creación)
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("POST /api/confiteria → 201 + DTO creado")
    void testCrearConfiteria_debeRetornar201CuandoExitoso() throws Exception {
        // ARRANGE – preparar DTO de entrada y el DTO que el service devuelve
        ConfiteriaDTO requestDto = new ConfiteriaDTO();
        requestDto.setNombre("Nachos");
        requestDto.setPrecio(new BigDecimal("4000.00"));
        requestDto.setStock(30);
        requestDto.setCategoria("SNACK");

        ConfiteriaDTO savedDto = new ConfiteriaDTO();
        savedDto.setId(3L);
        savedDto.setNombre("Nachos");
        savedDto.setPrecio(new BigDecimal("4000.00"));
        savedDto.setStock(30);
        savedDto.setCategoria("SNACK");

        when(service.guardar(any(ConfiteriaDTO.class))).thenReturn(savedDto);

        // ACT – enviar POST con cuerpo JSON
        mockMvc.perform(post("/api/confiteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nombre").value("Nachos"))
                .andExpect(jsonPath("$.precio").value(4000.00))
                .andExpect(jsonPath("$.stock").value(30))
                .andExpect(jsonPath("$.categoria").value("SNACK"));

        verify(service, times(1)).guardar(any(ConfiteriaDTO.class));
    }

    }

