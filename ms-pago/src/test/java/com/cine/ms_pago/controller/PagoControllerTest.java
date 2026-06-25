package com.cine.ms_pago.controller;

import com.cine.ms_pago.dto.PagoDTO;
import com.cine.ms_pago.service.IPagoService;
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
 * Pruebas unitarias para PagoController
 * Microservicio: ms-pago
 * Paquete: com.cine.ms_pago.controller
 * ============================================================
 *
 * Estrategia de testing:
 *   @WebMvcTest  → levanta SOLO la capa web (sin MySQL, sin Eureka, sin Feign)
 *   @MockitoBean → simula IPagoService (sin base de datos real)
 *   MockMvc      → simula peticiones HTTP (GET, POST, PUT, DELETE)
 *
 * Patrón en cada test:
 *   ARRANGE → preparar datos y mocks
 *   ACT     → ejecutar el endpoint con MockMvc
 *   ASSERT  → verificar el status HTTP y el body JSON
 *   VERIFY  → confirmar llamadas al mock
 *
 * Nota sobre GlobalExceptionHandler:
 *   - RuntimeException con mensaje "no existe" → HTTP 400 (Bad Request)
 *   - RuntimeException con mensaje "Ya existe un pago" → HTTP 409 (Conflict)
 *   - RuntimeException con cualquier otro mensaje → HTTP 404 (Not Found)
 * ============================================================
 */
@WebMvcTest(PagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPagoService service;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================================
    // TEST 1: GET /api/pagos → listar todos los pagos
    // =========================================================================

    @Test
    @DisplayName("TEST 1 — GET /api/pagos → HTTP 200 + lista de pagos")
    void testListar_debeRetornar200ConLista() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        PagoDTO p1 = new PagoDTO();
        p1.setId(1L);
        p1.setReservaId(10L);
        p1.setMontoTotal(new BigDecimal("15000.00"));
        p1.setMetodoPago("TARJETA");
        p1.setEstado("COMPLETADO");
        p1.setFechaPago(LocalDateTime.of(2025, 6, 1, 12, 0));

        PagoDTO p2 = new PagoDTO();
        p2.setId(2L);
        p2.setReservaId(11L);
        p2.setMontoTotal(new BigDecimal("8000.00"));
        p2.setMetodoPago("EFECTIVO");
        p2.setEstado("PENDIENTE");
        p2.setFechaPago(LocalDateTime.of(2025, 6, 2, 16, 0));

        when(service.listarTodos()).thenReturn(List.of(p1, p2));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].reservaId").value(10))
                .andExpect(jsonPath("$[0].montoTotal").value(15000.00))
                .andExpect(jsonPath("$[0].estado").value("COMPLETADO"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].metodoPago").value("EFECTIVO"))
                .andExpect(jsonPath("$[1].estado").value("PENDIENTE"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).listarTodos();
    }

    // =========================================================================
    // TEST 2a: GET /api/pagos/{id} → caso éxito (existe)
    // =========================================================================

    @Test
    @DisplayName("TEST 2a — GET /api/pagos/{id} → HTTP 200 cuando el ID existe")
    void testObtenerPorId_debeRetornar200CuandoExiste() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        PagoDTO dto = new PagoDTO();
        dto.setId(1L);
        dto.setReservaId(10L);
        dto.setMontoTotal(new BigDecimal("15000.00"));
        dto.setMetodoPago("TARJETA");
        dto.setEstado("COMPLETADO");
        dto.setFechaPago(LocalDateTime.of(2025, 6, 1, 12, 0));

        when(service.buscarPorId(1L)).thenReturn(dto);

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservaId").value(10))
                .andExpect(jsonPath("$.montoTotal").value(15000.00))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorId(1L);
    }

    // =========================================================================
    // TEST 2b: GET /api/pagos/{id} → caso no encontrado (404)
    // =========================================================================

    @Test
    @DisplayName("TEST 2b — GET /api/pagos/{id} → HTTP 404 cuando el ID no existe")
    void testObtenerPorId_debeRetornar404CuandoNoExiste() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        when(service.buscarPorId(99L))
                .thenThrow(new RuntimeException("Pago con id 99 no encontrado"));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/pagos/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Pago con id 99 no encontrado"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorId(99L);
    }

    // =========================================================================
    // TEST 3a: GET /api/pagos/reserva/{reservaId} → caso éxito (existe)
    // =========================================================================

    @Test
    @DisplayName("TEST 3a — GET /api/pagos/reserva/{reservaId} → HTTP 200 + DTO")
    void testObtenerPorReserva_debeRetornar200CuandoExiste() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        PagoDTO dto = new PagoDTO();
        dto.setId(1L);
        dto.setReservaId(100L);
        dto.setMontoTotal(new BigDecimal("12000.00"));
        dto.setMetodoPago("NEQUI");
        dto.setEstado("COMPLETADO");
        dto.setFechaPago(LocalDateTime.of(2025, 6, 1, 12, 0));

        when(service.buscarPorReserva(100L)).thenReturn(dto);

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/pagos/reserva/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservaId").value(100))
                .andExpect(jsonPath("$.montoTotal").value(12000.00))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorReserva(100L);
    }

    // =========================================================================
    // TEST 3b: GET /api/pagos/reserva/{reservaId} → caso no existe reserva (400)
    // =========================================================================

    @Test
    @DisplayName("TEST 3b — GET /api/pagos/reserva/{reservaId} → HTTP 400 cuando la reserva no existe")
    void testObtenerPorReserva_debeRetornar400CuandoReservaNoExiste() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        when(service.buscarPorReserva(999L))
                .thenThrow(new RuntimeException("La reserva no existe"));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/pagos/reserva/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("La reserva no existe"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorReserva(999L);
    }

    // =========================================================================
    // TEST 4: POST /api/pagos → caso éxito (creación, HTTP 201)
    // =========================================================================

    @Test
    @DisplayName("TEST 4 — POST /api/pagos → HTTP 201 + DTO creado")
    void testCrearPago_debeRetornar201CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        PagoDTO requestDto = new PagoDTO();
        requestDto.setReservaId(10L);
        requestDto.setMontoTotal(new BigDecimal("15000.00"));
        requestDto.setMetodoPago("TARJETA");
        requestDto.setEstado("COMPLETADO");

        PagoDTO savedDto = new PagoDTO();
        savedDto.setId(1L);
        savedDto.setReservaId(10L);
        savedDto.setMontoTotal(new BigDecimal("15000.00"));
        savedDto.setMetodoPago("TARJETA");
        savedDto.setEstado("COMPLETADO");
        savedDto.setFechaPago(LocalDateTime.of(2025, 6, 1, 12, 0));

        when(service.guardar(any(PagoDTO.class))).thenReturn(savedDto);

        // ── ACT: enviar POST con cuerpo JSON ─────────────────────────────────
        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                // ── ASSERT ───────────────────────────────────────────────────
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservaId").value(10))
                .andExpect(jsonPath("$.montoTotal").value(15000.00))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).guardar(any(PagoDTO.class));
    }

    // =========================================================================
    // TEST 5: PUT /api/pagos/{id} → actualizar existente (HTTP 200)
    // =========================================================================

    @Test
    @DisplayName("TEST 5 — PUT /api/pagos/{id} → HTTP 200 + DTO actualizado")
    void testActualizar_debeRetornar200CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        PagoDTO requestDto = new PagoDTO();
        requestDto.setReservaId(10L);
        requestDto.setMontoTotal(new BigDecimal("16000.00"));
        requestDto.setMetodoPago("EFECTIVO");
        requestDto.setEstado("COMPLETADO");

        PagoDTO updatedDto = new PagoDTO();
        updatedDto.setId(1L);
        updatedDto.setReservaId(10L);
        updatedDto.setMontoTotal(new BigDecimal("16000.00"));
        updatedDto.setMetodoPago("EFECTIVO");
        updatedDto.setEstado("COMPLETADO");
        updatedDto.setFechaPago(LocalDateTime.of(2025, 6, 1, 12, 0));

        when(service.actualizar(eq(1L), any(PagoDTO.class))).thenReturn(updatedDto);

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(put("/api/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.montoTotal").value(16000.00))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).actualizar(eq(1L), any(PagoDTO.class));
    }

    // =========================================================================
    // TEST 6: DELETE /api/pagos/{id} → eliminar pago (HTTP 204)
    // =========================================================================

    @Test
    @DisplayName("TEST 6 — DELETE /api/pagos/{id} → HTTP 204 sin cuerpo")
    void testEliminar_debeRetornar204CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        doNothing().when(service).eliminar(eq(1L));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(delete("/api/pagos/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).eliminar(eq(1L));
    }
}
