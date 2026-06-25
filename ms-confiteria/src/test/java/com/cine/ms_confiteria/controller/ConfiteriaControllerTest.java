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
 * ============================================================
 * Pruebas unitarias para ConfiteriaController
 * Microservicio: ms-confiteria
 * Paquete: com.cine.ms_confiteria.controller
 * ============================================================
 *
 * Estrategia de testing:
 *   @WebMvcTest  → levanta SOLO la capa web (sin MySQL, sin Eureka, sin contexto completo)
 *   @MockitoBean → simula IConfiteriaService (sin base de datos real)
 *   MockMvc      → simula las peticiones HTTP (GET, POST, PUT, PATCH, DELETE)
 *
 * Patrón en cada test:
 *   ARRANGE → preparar datos y mocks
 *   ACT     → ejecutar el endpoint con MockMvc
 *   ASSERT  → verificar el status HTTP y el body JSON
 *   VERIFY  → confirmar llamadas al mock
 * ============================================================
 */
@WebMvcTest(ConfiteriaController.class)
class ConfiteriaControllerTest {

    // MockMvc inyectado automáticamente por @WebMvcTest
    @Autowired
    private MockMvc mockMvc;

    // Service simulado: no hay base de datos real
    @MockitoBean
    private IConfiteriaService service;

    // Para serializar objetos a JSON en los tests de escritura (POST/PUT)
    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================================
    // TEST 1: GET /api/confiteria → listar todos los ítems de confitería
    // =========================================================================

    @Test
    @DisplayName("TEST 1 — GET /api/confiteria → HTTP 200 + lista de confitería")
    void testListar_debeRetornar200ConLista() throws Exception {

        // ── ARRANGE: preparar datos de prueba y configurar el mock ────────────
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

        // El service simulado retorna la lista cuando se llame a listarTodos()
        when(service.listarTodos()).thenReturn(List.of(item1, item2));

        // ── ACT: ejecutar la petición GET /api/confiteria ─────────────────────
        mockMvc.perform(get("/api/confiteria")
                        .contentType(MediaType.APPLICATION_JSON))

                // ── ASSERT: verificar HTTP 200 y contenido del JSON ───────────
                .andExpect(status().isOk())                            // HTTP 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))            // 2 ítems en la lista
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Palomitas de Mantequilla"))
                .andExpect(jsonPath("$[0].precio").value(3500.00))
                .andExpect(jsonPath("$[0].stock").value(50))
                .andExpect(jsonPath("$[0].categoria").value("SNACK"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nombre").value("Bebida Cola Grande"))
                .andExpect(jsonPath("$[1].categoria").value("BEBIDA"));

        // ── VERIFY: confirmar que listarTodos() fue llamado exactamente 1 vez ─
        verify(service, times(1)).listarTodos();
    }

    // =========================================================================
    // TEST 2a: GET /api/confiteria/{id} → caso éxito (existe)
    // =========================================================================

    @Test
    @DisplayName("TEST 2a — GET /api/confiteria/{id} → HTTP 200 cuando el ID existe")
    void testObtenerPorId_debeRetornar200CuandoExiste() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        ConfiteriaDTO dto = new ConfiteriaDTO();
        dto.setId(1L);
        dto.setNombre("Palomitas de Mantequilla");
        dto.setPrecio(new BigDecimal("3500.00"));
        dto.setStock(50);
        dto.setCategoria("SNACK");

        when(service.buscarPorId(1L)).thenReturn(dto);

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/confiteria/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Palomitas de Mantequilla"))
                .andExpect(jsonPath("$.precio").value(3500.00))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.categoria").value("SNACK"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorId(1L);
    }

    // =========================================================================
    // TEST 2b: GET /api/confiteria/{id} → caso no encontrado (404)
    // =========================================================================

    @Test
    @DisplayName("TEST 2b — GET /api/confiteria/{id} → HTTP 404 cuando el ID no existe")
    void testObtenerPorId_debeRetornar404CuandoNoExiste() throws Exception {

        // ── ARRANGE: el service lanza RuntimeException → GlobalExceptionHandler la mapea a 404
        when(service.buscarPorId(99L))
                .thenThrow(new RuntimeException("Confitería con id 99 no encontrada"));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
        mockMvc.perform(get("/api/confiteria/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Confitería con id 99 no encontrada"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorId(99L);
    }

    // =========================================================================
    // TEST 3: GET /api/confiteria/categoria/{categoria} → caso éxito
    // =========================================================================

    @Test
    @DisplayName("TEST 3 — GET /api/confiteria/categoria/{categoria} → HTTP 200 + lista")
    void testBuscarPorCategoria_debeRetornar200CuandoExiste() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        ConfiteriaDTO dto = new ConfiteriaDTO();
        dto.setId(1L);
        dto.setNombre("Palomitas de Mantequilla");
        dto.setPrecio(new BigDecimal("3500.00"));
        dto.setStock(50);
        dto.setCategoria("SNACK");

        when(service.buscarPorCategoria("SNACK")).thenReturn(List.of(dto));

        // ── ACT & ASSERT ──────────────────────────────────────────────────────
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

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).buscarPorCategoria("SNACK");
    }

    // =========================================================================
    // TEST 4: POST /api/confiteria → caso éxito (creación, HTTP 201)
    // =========================================================================

    @Test
    @DisplayName("TEST 4 — POST /api/confiteria → HTTP 201 + DTO creado")
    void testCrearConfiteria_debeRetornar201CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
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

        // ── ACT: enviar POST con cuerpo JSON ─────────────────────────────────
        mockMvc.perform(post("/api/confiteria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                // ── ASSERT ───────────────────────────────────────────────────
                .andExpect(status().isCreated())                        // HTTP 201 Created
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nombre").value("Nachos"))
                .andExpect(jsonPath("$.precio").value(4000.00))
                .andExpect(jsonPath("$.stock").value(30))
                .andExpect(jsonPath("$.categoria").value("SNACK"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).guardar(any(ConfiteriaDTO.class));
    }

    // =========================================================================
    // TEST 5: PUT /api/confiteria/{id} → actualizar ítem existente (HTTP 200)
    // =========================================================================

    @Test
    @DisplayName("TEST 5 — PUT /api/confiteria/{id} → HTTP 200 + DTO actualizado")
    void testActualizar_debeRetornar200CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        // DTO con los nuevos valores que envía el cliente en el body
        ConfiteriaDTO requestDto = new ConfiteriaDTO();
        requestDto.setNombre("Palomitas Caramelo");
        requestDto.setPrecio(new BigDecimal("4200.00"));
        requestDto.setStock(45);
        requestDto.setCategoria("SNACK");

        // DTO que el service devuelve tras actualizar (incluye el id)
        ConfiteriaDTO updatedDto = new ConfiteriaDTO();
        updatedDto.setId(1L);
        updatedDto.setNombre("Palomitas Caramelo");
        updatedDto.setPrecio(new BigDecimal("4200.00"));
        updatedDto.setStock(45);
        updatedDto.setCategoria("SNACK");

        // Cuando se llame actualizar(1L, cualquier DTO) → retorna updatedDto
        when(service.actualizar(eq(1L), any(ConfiteriaDTO.class))).thenReturn(updatedDto);

        // ── ACT: enviar PUT /api/confiteria/1 con el body ────────────────────
        mockMvc.perform(put("/api/confiteria/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))

                // ── ASSERT ───────────────────────────────────────────────────
                .andExpect(status().isOk())                             // HTTP 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Palomitas Caramelo"))
                .andExpect(jsonPath("$.precio").value(4200.00))
                .andExpect(jsonPath("$.stock").value(45))
                .andExpect(jsonPath("$.categoria").value("SNACK"));

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).actualizar(eq(1L), any(ConfiteriaDTO.class));

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <200> but was: <404>"
         * Causa: El service lanza RuntimeException porque el id no existe
         * QA reporta:
         *   Endpoint  → PUT /api/confiteria/1
         *   Esperado  → HTTP 200 + DTO con nombre "Palomitas Caramelo"
         *   Obtenido  → HTTP 404
         *   Revisar   → método actualizar() en ConfiteriaServiceImpl
         */
    }

    // =========================================================================
    // TEST 6: PATCH /api/confiteria/{id}/stock → modificar stock (HTTP 204)
    // =========================================================================

    @Test
    @DisplayName("TEST 6 — PATCH /api/confiteria/{id}/stock → HTTP 204 sin cuerpo")
    void testModificarStock_debeRetornar204CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        // El endpoint es: PATCH /api/confiteria/{id}/stock?cantidad=XX
        // actualizarStock devuelve void → doNothing() es la configuración correcta
        doNothing().when(service).actualizarStock(eq(1L), eq(-5));

        // ── ACT: PATCH /api/confiteria/1/stock?cantidad=-5
        //    (negativo = descontar stock tras una venta)
        mockMvc.perform(patch("/api/confiteria/1/stock")
                        .param("cantidad", "-5"))

                // ── ASSERT ───────────────────────────────────────────────────
                .andExpect(status().isNoContent())                      // HTTP 204 No Content
                .andExpect(content().string(""));                       // Cuerpo vacío

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).actualizarStock(eq(1L), eq(-5));

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <204> but was: <400>"
         * Causa: El parámetro "cantidad" no llega al controller (typo en @RequestParam)
         * QA reporta:
         *   Endpoint  → PATCH /api/confiteria/1/stock?cantidad=-5
         *   Esperado  → HTTP 204 sin body
         *   Obtenido  → HTTP 400 Bad Request
         *   Revisar   → @RequestParam en ConfiteriaController.modificarStock()
         */
    }

    // =========================================================================
    // TEST 7: DELETE /api/confiteria/{id} → eliminar ítem (HTTP 204)
    // =========================================================================

    @Test
    @DisplayName("TEST 7 — DELETE /api/confiteria/{id} → HTTP 204 sin cuerpo")
    void testEliminar_debeRetornar204CuandoExitoso() throws Exception {

        // ── ARRANGE ──────────────────────────────────────────────────────────
        // eliminar() devuelve void → doNothing() es la configuración correcta para mocks void
        doNothing().when(service).eliminar(eq(1L));

        // ── ACT: DELETE /api/confiteria/1 ────────────────────────────────────
        mockMvc.perform(delete("/api/confiteria/1"))

                // ── ASSERT ───────────────────────────────────────────────────
                .andExpect(status().isNoContent())                      // HTTP 204 No Content
                .andExpect(content().string(""));                       // Sin cuerpo en la respuesta

        // ── VERIFY ───────────────────────────────────────────────────────────
        verify(service, times(1)).eliminar(eq(1L));

        /*
         * ── CASO HIPOTÉTICO DE FALLA (para QA) ───────────────────────────────
         *
         * Falla: "Status expected: <204> but was: <404>"
         * Causa: El service lanza RuntimeException porque el id no existe
         * QA reporta:
         *   Endpoint  → DELETE /api/confiteria/1
         *   Esperado  → HTTP 204 sin body
         *   Obtenido  → HTTP 404
         *   Revisar   → método eliminar() en ConfiteriaServiceImpl
         */
    }
}
