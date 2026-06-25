package com.cine.ms_pago_confiteria.controller;

import com.cine.ms_pago_confiteria.dto.PagoConfiteriaDTO;
import com.cine.ms_pago_confiteria.service.IPagoConfiteriaService;
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
 * Pruebas unitarias para PagoConfiteriaController
 * Microservicio: ms-pago_confiteria
 * Paquete: com.cine.ms_pago_confiteria.controller
 * ============================================================
 *
 * Estrategia de testing:
 *   @WebMvcTest  → levanta SOLO la capa web (sin MySQL, sin Eureka, sin Feign)
 *   @MockitoBean → simula IPagoConfiteriaService (sin base de datos real)
 *   MockMvc      → simula peticiones HTTP (GET, POST, PUT, DELETE)
 *
 * Patrón en cada test:
 *   ARRANGE → preparar datos y mocks
 *   ACT     → ejecutar el endpoint con MockMvc
 *   ASSERT  → verificar el status HTTP y el body JSON
 *   VERIFY  → confirmar llamadas al mock
 *
 * Nota sobre GlobalExeptionHandler:
 *   - RuntimeException con mensaje que contiene "no existe" → HTTP 400
 *   - RuntimeException con cualquier otro mensaje          → HTTP 404
 * ============================================================
 */
@WebMvcTest(PagoConfiteriaController.class)
class PagoConfiteriaControllerTest {

    // MockMvc inyectado automáticamente por @WebMvcTest
    @Autowired
    private MockMvc mockMvc;

    // Service simulado: no hay base de datos real
    @MockitoBean
    private IPagoConfiteriaService service;

    // Para serializar objetos a JSON en tests de escritura (POST/PUT)
    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================================
    // TEST 1: GET /api/pagos-confiteria → listar todos los pagos
    // =========================================================================

    @Test
    @DisplayName("TEST 1 — GET /api/pagos-confiteria → HTTP 200 + lista de pagos")
    void testListar_debeRetornar200ConLista() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        PagoConfiteriaDTO pago1 = new PagoConfiteriaDTO();
        pago1.setId(1L);
        pago1.setUsuarioId(10L);
        pago1.setProductoId(5L);
        pago1.setCantidad(2);
        pago1.setTotalPagado(new BigDecimal("7000.00"));
        pago1.setFechaCompra(LocalDateTime.of(2025, 6, 1, 10, 30));

        PagoConfiteriaDTO pago2 = new PagoConfiteriaDTO();
        pago2.setId(2L);
        pago2.setUsuarioId(11L);
        pago2.setProductoId(3L);
        pago2.setCantidad(1);
        pago2.setTotalPagado(new BigDecimal("3500.00"));
        pago2.setFechaCompra(LocalDateTime.of(2025, 6, 2, 15, 0));

        when(service.listarTodos()).thenReturn(List.of(pago1, pago2));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/pagos-confiteria")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())                             // HTTP 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))             // 2 pagos en la lista
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(10))
                .andExpect(jsonPath("$[0].productoId").value(5))
                .andExpect(jsonPath("$[0].cantidad").value(2))
                .andExpect(jsonPath("$[0].totalPagado").value(7000.00))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].usuarioId").value(11))
                .andExpect(jsonPath("$[1].totalPagado").value(3500.00));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).listarTodos();

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <200> but was: <500>"
         * Causa: El service lanza excepción no controlada
         * QA reporta:
         *   Endpoint  → GET /api/pagos-confiteria
         *   Esperado  → HTTP 200 + JSON array con 2 pagos
         *   Obtenido  → HTTP 500
         *   Revisar   → método listarTodos() en PagoConfiteriaServiceImpl
         */
    }

    // =========================================================================
    // TEST 2a: GET /api/pagos-confiteria/{id} → caso éxito (existe)
    // =========================================================================

    @Test
    @DisplayName("TEST 2a — GET /api/pagos-confiteria/{id} → HTTP 200 cuando el ID existe")
    void testObtenerPorId_debeRetornar200CuandoExiste() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        PagoConfiteriaDTO dto = new PagoConfiteriaDTO();
        dto.setId(1L);
        dto.setUsuarioId(10L);
        dto.setProductoId(5L);
        dto.setCantidad(2);
        dto.setTotalPagado(new BigDecimal("7000.00"));
        dto.setFechaCompra(LocalDateTime.of(2025, 6, 1, 10, 30));

        when(service.buscarPorId(1L)).thenReturn(dto);

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/pagos-confiteria/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(10))
                .andExpect(jsonPath("$.productoId").value(5))
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$.totalPagado").value(7000.00));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorId(1L);
    }

    // =========================================================================
    // TEST 2b: GET /api/pagos-confiteria/{id} → caso no encontrado (404)
    // =========================================================================

    @Test
    @DisplayName("TEST 2b — GET /api/pagos-confiteria/{id} → HTTP 404 cuando el ID no existe")
    void testObtenerPorId_debeRetornar404CuandoNoExiste() throws Exception {

        // ── ARRANGE: GlobalExeptionHandler → RuntimeException sin "no existe" → 404
        when(service.buscarPorId(99L))
                .thenThrow(new RuntimeException("Pago con id 99 no encontrado"));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/pagos-confiteria/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())                        // HTTP 404
                .andExpect(jsonPath("$.mensaje").value("Pago con id 99 no encontrado"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorId(99L);
    }

    // =========================================================================
    // TEST 3: GET /api/pagos-confiteria/usuario/{usuarioId} → pagos por usuario
    // =========================================================================

    @Test
    @DisplayName("TEST 3 — GET /api/pagos-confiteria/usuario/{usuarioId} → HTTP 200 + lista")
    void testObtenerPorUsuario_debeRetornar200ConPagosDelUsuario() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        PagoConfiteriaDTO pago = new PagoConfiteriaDTO();
        pago.setId(1L);
        pago.setUsuarioId(10L);
        pago.setProductoId(5L);
        pago.setCantidad(2);
        pago.setTotalPagado(new BigDecimal("7000.00"));
        pago.setFechaCompra(LocalDateTime.of(2025, 6, 1, 10, 30));

        when(service.buscarPorUsuario(10L)).thenReturn(List.of(pago));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/pagos-confiteria/usuario/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(10))
                .andExpect(jsonPath("$[0].productoId").value(5))
                .andExpect(jsonPath("$[0].cantidad").value(2))
                .andExpect(jsonPath("$[0].totalPagado").value(7000.00));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorUsuario(10L);

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <200> but was: <400>"
         * Causa: El service lanza RuntimeException con mensaje "usuario no existe"
         *         → GlobalExeptionHandler detecta "no existe" y responde HTTP 400
         * QA reporta:
         *   Endpoint  → GET /api/pagos-confiteria/usuario/10
         *   Esperado  → HTTP 200 + lista de pagos
         *   Obtenido  → HTTP 400
         *   Revisar   → validación del usuarioId en PagoConfiteriaServiceImpl
         */
    }

    // =========================================================================
    // TEST 4: POST /api/pagos-confiteria → crear pago (HTTP 201)
    // =========================================================================

    @Test
    @DisplayName("TEST 4 — POST /api/pagos-confiteria → HTTP 201 + DTO creado")
    void testCrearPago_debeRetornar201CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        PagoConfiteriaDTO requestDto = new PagoConfiteriaDTO();
        requestDto.setUsuarioId(10L);
        requestDto.setProductoId(5L);
        requestDto.setCantidad(2);
        requestDto.setTotalPagado(new BigDecimal("7000.00"));

        PagoConfiteriaDTO savedDto = new PagoConfiteriaDTO();
        savedDto.setId(1L);
        savedDto.setUsuarioId(10L);
        savedDto.setProductoId(5L);
        savedDto.setCantidad(2);
        savedDto.setTotalPagado(new BigDecimal("7000.00"));
        savedDto.setFechaCompra(LocalDateTime.of(2025, 6, 1, 10, 30));

        when(service.guardar(any(PagoConfiteriaDTO.class))).thenReturn(savedDto);

        // ── ACT: enviar POST con cuerpo JSON ─────────────────────────────────
        mockMvc.perform(post("/api/pagos-confiteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                // ── ASSERT ───────────────────────────────────────────────────
                .andExpect(status().isCreated())                         // HTTP 201 Created
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(10))
                .andExpect(jsonPath("$.productoId").value(5))
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$.totalPagado").value(7000.00));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).guardar(any(PagoConfiteriaDTO.class));

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <201> but was: <400>"
         * Causa: Bean Validation rechaza el body porque algún campo @NotNull está nulo
         * QA reporta:
         *   Endpoint  → POST /api/pagos-confiteria
         *   Body enviado → { "usuarioId": null, "productoId": 5, "cantidad": 2, "totalPagado": 7000 }
         *   Esperado  → HTTP 201
         *   Obtenido  → HTTP 400 + { "usuarioId": "El ID del usuario es obligatorio" }
         *   Revisar   → campo @NotNull usuarioId en PagoConfiteriaDTO
         */
    }

    // =========================================================================
    // TEST 5: PUT /api/pagos-confiteria/{id} → actualizar pago (HTTP 200)
    // =========================================================================

    @Test
    @DisplayName("TEST 5 — PUT /api/pagos-confiteria/{id} → HTTP 200 + DTO actualizado")
    void testActualizar_debeRetornar200CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        PagoConfiteriaDTO requestDto = new PagoConfiteriaDTO();
        requestDto.setUsuarioId(10L);
        requestDto.setProductoId(5L);
        requestDto.setCantidad(3);
        requestDto.setTotalPagado(new BigDecimal("10500.00"));

        PagoConfiteriaDTO updatedDto = new PagoConfiteriaDTO();
        updatedDto.setId(1L);
        updatedDto.setUsuarioId(10L);
        updatedDto.setProductoId(5L);
        updatedDto.setCantidad(3);
        updatedDto.setTotalPagado(new BigDecimal("10500.00"));
        updatedDto.setFechaCompra(LocalDateTime.of(2025, 6, 1, 10, 30));

        when(service.actualizar(eq(1L), any(PagoConfiteriaDTO.class))).thenReturn(updatedDto);

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(put("/api/pagos-confiteria/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())                              // HTTP 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usuarioId").value(10))
                .andExpect(jsonPath("$.productoId").value(5))
                .andExpect(jsonPath("$.cantidad").value(3))
                .andExpect(jsonPath("$.totalPagado").value(10500.00));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).actualizar(eq(1L), any(PagoConfiteriaDTO.class));

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <200> but was: <404>"
         * Causa: El service no encuentra el pago con id=1
         * QA reporta:
         *   Endpoint  → PUT /api/pagos-confiteria/1
         *   Esperado  → HTTP 200 + DTO actualizado
         *   Obtenido  → HTTP 404
         *   Revisar   → método actualizar() en PagoConfiteriaServiceImpl
         */
    }

    // =========================================================================
    // TEST 6: DELETE /api/pagos-confiteria/{id} → eliminar pago (HTTP 204)
    // =========================================================================

    @Test
    @DisplayName("TEST 6 — DELETE /api/pagos-confiteria/{id} → HTTP 204 sin cuerpo")
    void testEliminar_debeRetornar204CuandoExitoso() throws Exception {

        // ── ARRANGE: eliminar() devuelve void → doNothing()
        doNothing().when(service).eliminar(eq(1L));

        // ── ACT: DELETE /api/pagos-confiteria/1 ──────────────────────────────
        mockMvc.perform(delete("/api/pagos-confiteria/1"))

                // ── ASSERT ───────────────────────────────────────────────────
                .andExpect(status().isNoContent())                       // HTTP 204 No Content
                .andExpect(content().string(""));                        // Sin cuerpo

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).eliminar(eq(1L));

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <204> but was: <404>"
         * Causa: El service lanza RuntimeException porque el pago no existe
         * QA reporta:
         *   Endpoint  → DELETE /api/pagos-confiteria/1
         *   Esperado  → HTTP 204 sin body
         *   Obtenido  → HTTP 404
         *   Revisar   → método eliminar() en PagoConfiteriaServiceImpl
         */
    }
}
