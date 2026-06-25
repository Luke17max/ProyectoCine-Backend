package com.cine.ms_notificaciones.controller;

import com.cine.ms_notificaciones.dto.NotificacionDTO;
import com.cine.ms_notificaciones.service.INotificacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ============================================================
 * Pruebas unitarias para NotificacionController
 * Microservicio: ms-notificaciones
 * Paquete: com.cine.ms_notificaciones.controller
 * ============================================================
 *
 * Estrategia de testing:
 *   @WebMvcTest  → levanta SOLO la capa web (sin MySQL, sin Eureka, sin Feign)
 *   @MockitoBean → simula INotificacionService (sin base de datos real)
 *   MockMvc      → simula peticiones HTTP (GET, POST, DELETE)
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
@WebMvcTest(NotificacionController.class)
class NotificacionControllerTest {

    // MockMvc inyectado automáticamente por @WebMvcTest
    @Autowired
    private MockMvc mockMvc;

    // Service simulado: no hay base de datos real ni Feign activo
    @MockitoBean
    private INotificacionService service;

    // Para serializar objetos a JSON en tests de escritura (POST)
    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================================
    // TEST 1: GET /api/notificaciones → listar todas las notificaciones
    // =========================================================================

    @Test
    @DisplayName("TEST 1 — GET /api/notificaciones → HTTP 200 + lista de notificaciones")
    void testListar_debeRetornar200ConLista() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        NotificacionDTO n1 = new NotificacionDTO();
        n1.setId(1L);
        n1.setReservaId(100L);
        n1.setPagoId(200L);
        n1.setTipo("CONFIRMACION");
        n1.setMensaje("Tu reserva ha sido confirmada.");
        n1.setFechaCreacion(LocalDateTime.of(2025, 6, 1, 9, 0));

        NotificacionDTO n2 = new NotificacionDTO();
        n2.setId(2L);
        n2.setReservaId(101L);
        n2.setPagoId(null);
        n2.setTipo("RECORDATORIO");
        n2.setMensaje("Recuerda que tu función es mañana.");
        n2.setFechaCreacion(LocalDateTime.of(2025, 6, 2, 14, 30));

        when(service.listarTodas()).thenReturn(List.of(n1, n2));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())                              // HTTP 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].reservaId").value(100))
                .andExpect(jsonPath("$[0].tipo").value("CONFIRMACION"))
                .andExpect(jsonPath("$[0].mensaje").value("Tu reserva ha sido confirmada."))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].reservaId").value(101))
                .andExpect(jsonPath("$[1].tipo").value("RECORDATORIO"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).listarTodas();

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <200> but was: <500>"
         * Causa: El service lanza excepción no controlada en listarTodas()
         * QA reporta:
         *   Endpoint  → GET /api/notificaciones
         *   Esperado  → HTTP 200 + JSON array con 2 notificaciones
         *   Obtenido  → HTTP 500
         *   Revisar   → método listarTodas() en NotificacionServiceImpl
         */
    }

    // =========================================================================
    // TEST 2a: GET /api/notificaciones/{id} → caso éxito (existe)
    // =========================================================================

    @Test
    @DisplayName("TEST 2a — GET /api/notificaciones/{id} → HTTP 200 cuando el ID existe")
    void testObtenerPorId_debeRetornar200CuandoExiste() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(1L);
        dto.setReservaId(100L);
        dto.setPagoId(200L);
        dto.setTipo("CONFIRMACION");
        dto.setMensaje("Tu reserva ha sido confirmada.");
        dto.setFechaCreacion(LocalDateTime.of(2025, 6, 1, 9, 0));

        when(service.buscarPorId(1L)).thenReturn(dto);

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/notificaciones/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservaId").value(100))
                .andExpect(jsonPath("$.pagoId").value(200))
                .andExpect(jsonPath("$.tipo").value("CONFIRMACION"))
                .andExpect(jsonPath("$.mensaje").value("Tu reserva ha sido confirmada."));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorId(1L);
    }

    // =========================================================================
    // TEST 2b: GET /api/notificaciones/{id} → caso no encontrado (404)
    // =========================================================================

    @Test
    @DisplayName("TEST 2b — GET /api/notificaciones/{id} → HTTP 404 cuando el ID no existe")
    void testObtenerPorId_debeRetornar404CuandoNoExiste() throws Exception {

        // ── ARRANGE: GlobalExceptionHandler → mensaje sin "no existe" → HTTP 404
        when(service.buscarPorId(99L))
                .thenThrow(new RuntimeException("Notificación con id 99 no encontrada"));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/notificaciones/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())                         // HTTP 404
                .andExpect(jsonPath("$.mensaje")
                        .value("Notificación con id 99 no encontrada"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorId(99L);

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <404> but was: <200>"
         * Causa: El service devuelve null en vez de lanzar excepción
         * QA reporta:
         *   Endpoint  → GET /api/notificaciones/99
         *   Esperado  → HTTP 404 + { "mensaje": "..." }
         *   Obtenido  → HTTP 200 con body null
         *   Revisar   → método buscarPorId() en NotificacionServiceImpl
         */
    }

    // =========================================================================
    // TEST 3: GET /api/notificaciones/reserva/{reservaId} → por reserva
    // =========================================================================

    @Test
    @DisplayName("TEST 3 — GET /api/notificaciones/reserva/{reservaId} → HTTP 200 + lista")
    void testObtenerPorReserva_debeRetornar200ConNotificacionesDeLaReserva() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        NotificacionDTO n1 = new NotificacionDTO();
        n1.setId(1L);
        n1.setReservaId(100L);
        n1.setPagoId(200L);
        n1.setTipo("CONFIRMACION");
        n1.setMensaje("Tu reserva ha sido confirmada.");
        n1.setFechaCreacion(LocalDateTime.of(2025, 6, 1, 9, 0));

        NotificacionDTO n2 = new NotificacionDTO();
        n2.setId(3L);
        n2.setReservaId(100L);
        n2.setPagoId(null);
        n2.setTipo("CANCELACION");
        n2.setMensaje("Tu reserva ha sido cancelada.");
        n2.setFechaCreacion(LocalDateTime.of(2025, 6, 3, 11, 0));

        when(service.buscarPorReserva(100L)).thenReturn(List.of(n1, n2));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/notificaciones/reserva/100")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].reservaId").value(100))
                .andExpect(jsonPath("$[0].tipo").value("CONFIRMACION"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].tipo").value("CANCELACION"))
                .andExpect(jsonPath("$[1].mensaje").value("Tu reserva ha sido cancelada."));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorReserva(100L);

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <200> but was: <400>"
         * Causa: El service valida que la reserva exista vía Feign y lanza
         *        RuntimeException("reserva no existe") → GlobalExceptionHandler
         *        detecta "no existe" → HTTP 400
         * QA reporta:
         *   Endpoint  → GET /api/notificaciones/reserva/100
         *   Esperado  → HTTP 200 + lista de notificaciones
         *   Obtenido  → HTTP 400 + { "mensaje": "reserva no existe" }
         *   Revisar   → validación de reservaId en NotificacionServiceImpl
         */
    }

    // =========================================================================
    // TEST 4: POST /api/notificaciones → crear notificación (HTTP 201)
    // =========================================================================

    @Test
    @DisplayName("TEST 4 — POST /api/notificaciones → HTTP 201 + DTO creado")
    void testCrearNotificacion_debeRetornar201CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        NotificacionDTO requestDto = new NotificacionDTO();
        requestDto.setReservaId(100L);
        requestDto.setPagoId(200L);
        requestDto.setTipo("CONFIRMACION");
        requestDto.setMensaje("Tu reserva ha sido confirmada.");

        NotificacionDTO savedDto = new NotificacionDTO();
        savedDto.setId(1L);
        savedDto.setReservaId(100L);
        savedDto.setPagoId(200L);
        savedDto.setTipo("CONFIRMACION");
        savedDto.setMensaje("Tu reserva ha sido confirmada.");
        savedDto.setFechaCreacion(LocalDateTime.of(2025, 6, 1, 9, 0));

        when(service.guardar(any(NotificacionDTO.class))).thenReturn(savedDto);

        // ── ACT: enviar POST con cuerpo JSON ─────────────────────────────────
        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                // ── ASSERT ───────────────────────────────────────────────────
                .andExpect(status().isCreated())                          // HTTP 201 Created
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reservaId").value(100))
                .andExpect(jsonPath("$.pagoId").value(200))
                .andExpect(jsonPath("$.tipo").value("CONFIRMACION"))
                .andExpect(jsonPath("$.mensaje").value("Tu reserva ha sido confirmada."));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).guardar(any(NotificacionDTO.class));

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <201> but was: <400>"
         * Causa: Bean Validation rechaza el body porque "tipo" o "mensaje" están en blanco
         * QA reporta:
         *   Endpoint  → POST /api/notificaciones
         *   Body enviado → { "reservaId": 100, "tipo": "", "mensaje": "" }
         *   Esperado  → HTTP 201
         *   Obtenido  → HTTP 400 + { "tipo": "El tipo de notificación es obligatorio",
         *                            "mensaje": "El mensaje de la notificación es obligatorio" }
         *   Revisar   → campos @NotBlank en NotificacionDTO
         */
    }

    // =========================================================================
    // TEST 5: DELETE /api/notificaciones/{id} → eliminar notificación (HTTP 204)
    // =========================================================================

    @Test
    @DisplayName("TEST 5 — DELETE /api/notificaciones/{id} → HTTP 204 sin cuerpo")
    void testEliminar_debeRetornar204CuandoExitoso() throws Exception {

        // ── ARRANGE: eliminar() devuelve void → doNothing()
        doNothing().when(service).eliminar(eq(1L));

        // ── ACT: DELETE /api/notificaciones/1 ────────────────────────────────
        mockMvc.perform(delete("/api/notificaciones/1"))

                // ── ASSERT ───────────────────────────────────────────────────
                .andExpect(status().isNoContent())                        // HTTP 204 No Content
                .andExpect(content().string(""));                         // Sin cuerpo

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).eliminar(eq(1L));

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <204> but was: <404>"
         * Causa: El service lanza RuntimeException porque la notificación no existe
         * QA reporta:
         *   Endpoint  → DELETE /api/notificaciones/1
         *   Esperado  → HTTP 204 sin body
         *   Obtenido  → HTTP 404 + { "mensaje": "..." }
         *   Revisar   → método eliminar() en NotificacionServiceImpl
         */
    }
}
