package com.cine.ms_funciones.controller;

import com.cine.ms_funciones.dto.FuncionDTO;
import com.cine.ms_funciones.service.IFuncionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ============================================================
 * Pruebas unitarias para FuncionController
 * Microservicio: ms-funciones
 * Paquete: com.cine.ms_funciones.controller
 * ============================================================
 *
 * Estrategia de testing:
 *   @WebMvcTest  → levanta SOLO la capa web (sin MySQL, sin Eureka, sin Feign)
 *   @MockitoBean → simula IFuncionService (sin base de datos real)
 *   MockMvc      → simula peticiones HTTP (GET, POST, PUT, DELETE)
 *
 * Patrón en cada test:
 *   ARRANGE → preparar datos y mocks
 *   ACT     → ejecutar el endpoint con MockMvc
 *   ASSERT  → verificar el status HTTP y el body JSON
 *   VERIFY  → confirmar llamadas al mock
 *
 * Nota sobre GlobalExceptionHandler:
 *   - RuntimeException con mensaje que contiene "no existe" → HTTP 400
 *   - RuntimeException con cualquier otro mensaje          → HTTP 404
 * ============================================================
 */
@WebMvcTest(FuncionController.class)
class FuncionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IFuncionService service;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================================
    // TEST 1: GET /api/funciones → listar todas las funciones
    // =========================================================================

    @Test
    @DisplayName("TEST 1 — GET /api/funciones → HTTP 200 + lista de funciones")
    void testListar_debeRetornar200ConLista() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        FuncionDTO f1 = new FuncionDTO();
        f1.setId(1L);
        f1.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));
        f1.setPrecioBase(new BigDecimal("4500.00"));
        f1.setPeliculaId(10L);
        f1.setSalaId(3L);

        FuncionDTO f2 = new FuncionDTO();
        f2.setId(2L);
        f2.setFechaHora(LocalDateTime.of(2028, 12, 25, 21, 0));
        f2.setPrecioBase(new BigDecimal("5000.00"));
        f2.setPeliculaId(10L);
        f2.setSalaId(3L);

        when(service.listarTodas()).thenReturn(List.of(f1, f2));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/funciones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].precioBase").value(4500.00))
                .andExpect(jsonPath("$[0].peliculaId").value(10))
                .andExpect(jsonPath("$[0].salaId").value(3))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].precioBase").value(5000.00));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).listarTodas();
    }

    // =========================================================================
    // TEST 2a: GET /api/funciones/{id} → caso éxito (existe)
    // =========================================================================

    @Test
    @DisplayName("TEST 2a — GET /api/funciones/{id} → HTTP 200 cuando el ID existe")
    void testObtenerPorId_debeRetornar200CuandoExiste() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        FuncionDTO dto = new FuncionDTO();
        dto.setId(1L);
        dto.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));
        dto.setPrecioBase(new BigDecimal("4500.00"));
        dto.setPeliculaId(10L);
        dto.setSalaId(3L);

        when(service.buscarPorId(1L)).thenReturn(dto);

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/funciones/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.precioBase").value(4500.00))
                .andExpect(jsonPath("$.peliculaId").value(10))
                .andExpect(jsonPath("$.salaId").value(3));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorId(1L);
    }

    // =========================================================================
    // TEST 2b: GET /api/funciones/{id} → caso no encontrado (404)
    // =========================================================================

    @Test
    @DisplayName("TEST 2b — GET /api/funciones/{id} → HTTP 404 cuando el ID no existe")
    void testObtenerPorId_debeRetornar404CuandoNoExiste() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        when(service.buscarPorId(99L))
                .thenThrow(new RuntimeException("Función con id 99 no encontrada"));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/funciones/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Función con id 99 no encontrada"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorId(99L);
    }

    // =========================================================================
    // TEST 3: POST /api/funciones → caso éxito (creación, HTTP 201)
    // =========================================================================

    @Test
    @DisplayName("TEST 3 — POST /api/funciones → HTTP 201 + DTO creado")
    void testCrearFuncion_debeRetornar201CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        FuncionDTO requestDto = new FuncionDTO();
        requestDto.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0)); // Futuro
        requestDto.setPrecioBase(new BigDecimal("4500.00"));
        requestDto.setPeliculaId(10L);
        requestDto.setSalaId(3L);

        FuncionDTO savedDto = new FuncionDTO();
        savedDto.setId(1L);
        savedDto.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));
        savedDto.setPrecioBase(new BigDecimal("4500.00"));
        savedDto.setPeliculaId(10L);
        savedDto.setSalaId(3L);

        when(service.guardar(any(FuncionDTO.class))).thenReturn(savedDto);

        // ── ACT: enviar POST con cuerpo JSON ─────────────────────────────────
        mockMvc.perform(post("/api/funciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                // ── ASSERT ───────────────────────────────────────────────────
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.precioBase").value(4500.00))
                .andExpect(jsonPath("$.peliculaId").value(10))
                .andExpect(jsonPath("$.salaId").value(3));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).guardar(any(FuncionDTO.class));
    }

    // =========================================================================
    // TEST 4: PUT /api/funciones/{id} → actualizar existente (HTTP 200)
    // =========================================================================

    @Test
    @DisplayName("TEST 4 — PUT /api/funciones/{id} → HTTP 200 + DTO actualizado")
    void testActualizar_debeRetornar200CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        FuncionDTO requestDto = new FuncionDTO();
        requestDto.setFechaHora(LocalDateTime.of(2028, 12, 25, 20, 0));
        requestDto.setPrecioBase(new BigDecimal("4800.00"));
        requestDto.setPeliculaId(10L);
        requestDto.setSalaId(3L);

        FuncionDTO updatedDto = new FuncionDTO();
        updatedDto.setId(1L);
        updatedDto.setFechaHora(LocalDateTime.of(2028, 12, 25, 20, 0));
        updatedDto.setPrecioBase(new BigDecimal("4800.00"));
        updatedDto.setPeliculaId(10L);
        updatedDto.setSalaId(3L);

        when(service.actualizar(eq(1L), any(FuncionDTO.class))).thenReturn(updatedDto);

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(put("/api/funciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.precioBase").value(4800.00))
                .andExpect(jsonPath("$.peliculaId").value(10))
                .andExpect(jsonPath("$.salaId").value(3));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).actualizar(eq(1L), any(FuncionDTO.class));
    }

    // =========================================================================
    // TEST 5: DELETE /api/funciones/{id} → eliminar función (HTTP 204)
    // =========================================================================

    @Test
    @DisplayName("TEST 5 — DELETE /api/funciones/{id} → HTTP 204 sin cuerpo")
    void testEliminar_debeRetornar204CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        doNothing().when(service).eliminar(eq(1L));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(delete("/api/funciones/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).eliminar(eq(1L));
    }
}
