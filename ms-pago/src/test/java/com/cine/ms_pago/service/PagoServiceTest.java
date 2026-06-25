package com.cine.ms_pago.service;

import com.cine.ms_pago.client.ReservaClient;
import com.cine.ms_pago.dto.PagoDTO;
import com.cine.ms_pago.model.Pago;
import com.cine.ms_pago.repository.PagoRepository;
import com.cine.ms_pago.service.impl.PagoServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * ============================================================
 * Pruebas unitarias para PagoServiceImpl (Capa de Servicio)
 * Microservicio: ms-pago
 * Paquete: com.cine.ms_pago.service
 * ============================================================
 *
 * Estrategia de testing:
 *   @ExtendWith(MockitoExtension.class) → Inicia Mockito sin levantar contexto completo.
 *   @Mock        → Simula PagoRepository y ReservaClient.
 *   @InjectMocks → Inyecta los mocks en la implementación de servicio PagoServiceImpl.
 *
 * Patrón en cada test:
 *   ARRANGE → preparar mocks.
 *   ACT     → ejecutar servicio.
 *   ASSERT  → verificar valores y excepciones.
 *   VERIFY  → verificar llamadas.
 * ============================================================
 */
@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository repository;

    @Mock
    private ReservaClient reservaClient;

    @InjectMocks
    private PagoServiceImpl service;

    // =========================================================================
    // 1. listarTodos()
    // =========================================================================

    @Test
    @DisplayName("listarTodos() → debe mapear y retornar todos los pagos")
    void testListarTodos_debeRetornarListaDeDTOs() {
        // ARRANGE
        Pago p1 = new Pago();
        p1.setId(1L);
        p1.setReservaId(10L);
        p1.setMontoTotal(new BigDecimal("15000.00"));
        p1.setMetodoPago("TARJETA");
        p1.setEstado("COMPLETADO");
        p1.setFechaPago(LocalDateTime.of(2025, 6, 1, 12, 0));

        Pago p2 = new Pago();
        p2.setId(2L);
        p2.setReservaId(11L);
        p2.setMontoTotal(new BigDecimal("8000.00"));
        p2.setMetodoPago("EFECTIVO");
        p2.setEstado("PENDIENTE");
        p2.setFechaPago(LocalDateTime.of(2025, 6, 2, 12, 0));

        when(repository.findAll()).thenReturn(List.of(p1, p2));

        // ACT
        List<PagoDTO> resultado = service.listarTodos();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(new BigDecimal("15000.00"), resultado.get(0).getMontoTotal());
        assertEquals(2L, resultado.get(1).getId());
        assertEquals("PENDIENTE", resultado.get(1).getEstado());
        verify(repository, times(1)).findAll();
    }

    // =========================================================================
    // 2. buscarPorId()
    // =========================================================================

    @Test
    @DisplayName("buscarPorId() → debe retornar DTO cuando el ID existe")
    void testBuscarPorId_cuandoExiste_debeRetornarDTO() {
        // ARRANGE
        Pago p = new Pago();
        p.setId(1L);
        p.setReservaId(10L);
        p.setMontoTotal(new BigDecimal("15000.00"));
        p.setMetodoPago("TARJETA");
        p.setEstado("COMPLETADO");

        when(repository.findById(1L)).thenReturn(Optional.of(p));

        // ACT
        PagoDTO resultado = service.buscarPorId(1L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getReservaId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId() → debe lanzar excepción si el ID no existe")
    void testBuscarPorId_cuandoNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.buscarPorId(99L);
        });

        assertEquals("Pago no encontrado", ex.getMessage());
        verify(repository, times(1)).findById(99L);
    }

    // =========================================================================
    // 3. buscarPorReserva()
    // =========================================================================

    @Test
    @DisplayName("buscarPorReserva() → debe retornar DTO cuando existe pago para la reserva")
    void testBuscarPorReserva_cuandoExiste_debeRetornarDTO() {
        // ARRANGE
        Pago p = new Pago();
        p.setId(1L);
        p.setReservaId(10L);
        p.setMontoTotal(new BigDecimal("15000.00"));
        p.setMetodoPago("TARJETA");
        p.setEstado("COMPLETADO");

        when(repository.findByReservaId(10L)).thenReturn(Optional.of(p));

        // ACT
        PagoDTO resultado = service.buscarPorReserva(10L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getReservaId());
        verify(repository, times(1)).findByReservaId(10L);
    }

    @Test
    @DisplayName("buscarPorReserva() → debe lanzar excepción si no hay pago para la reserva")
    void testBuscarPorReserva_cuandoNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        when(repository.findByReservaId(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.buscarPorReserva(99L);
        });

        assertEquals("No existe pago para la reserva indicada", ex.getMessage());
        verify(repository, times(1)).findByReservaId(99L);
    }

    // =========================================================================
    // 4. guardar()
    // =========================================================================

    @Test
    @DisplayName("guardar() → debe registrar el pago si la reserva existe y no tiene pago previo")
    void testGuardar_cuandoReservaExisteYNoHayPagoPrevio_debeGuardarYRetornarDTO() {
        // ARRANGE
        PagoDTO requestDto = new PagoDTO();
        requestDto.setReservaId(10L);
        requestDto.setMontoTotal(new BigDecimal("15000.00"));
        requestDto.setMetodoPago("TARJETA");
        requestDto.setEstado("COMPLETADO");

        Pago savedEntity = new Pago();
        savedEntity.setId(1L);
        savedEntity.setReservaId(10L);
        savedEntity.setMontoTotal(new BigDecimal("15000.00"));
        savedEntity.setMetodoPago("TARJETA");
        savedEntity.setEstado("COMPLETADO");

        when(repository.findByReservaId(10L)).thenReturn(Optional.empty());
        when(reservaClient.obtenerReserva(10L)).thenReturn(new Object());
        when(repository.save(any(Pago.class))).thenReturn(savedEntity);

        // ACT
        PagoDTO resultado = service.guardar(requestDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getReservaId());
        verify(repository, times(1)).findByReservaId(10L);
        verify(reservaClient, times(1)).obtenerReserva(10L);
        verify(repository, times(1)).save(any(Pago.class));
    }

    @Test
    @DisplayName("guardar() → debe lanzar excepción si ya existe un pago para la misma reserva")
    void testGuardar_cuandoYaExistePagoParaLaReserva_debeLanzarRuntimeException() {
        // ARRANGE
        PagoDTO requestDto = new PagoDTO();
        requestDto.setReservaId(10L);

        Pago existingPago = new Pago();
        existingPago.setId(1L);
        existingPago.setReservaId(10L);

        // Ya existe un pago registrado
        when(repository.findByReservaId(10L)).thenReturn(Optional.of(existingPago));

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.guardar(requestDto);
        });

        assertTrue(ex.getMessage().contains("Ya existe un pago registrado para la reserva ID: 10"));
        verify(repository, times(1)).findByReservaId(10L);
        verify(reservaClient, never()).obtenerReserva(anyLong());
        verify(repository, never()).save(any(Pago.class));
    }

    @Test
    @DisplayName("guardar() → debe lanzar excepción si la reserva no existe en Feign")
    void testGuardar_cuandoReservaNoExisteEnFeign_debeLanzarRuntimeException() {
        // ARRANGE
        PagoDTO requestDto = new PagoDTO();
        requestDto.setReservaId(99L);

        when(repository.findByReservaId(99L)).thenReturn(Optional.empty());

        FeignException.NotFound mockNotFound = mock(FeignException.NotFound.class);
        when(reservaClient.obtenerReserva(99L)).thenThrow(mockNotFound);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.guardar(requestDto);
        });

        assertTrue(ex.getMessage().contains("La reserva con ID 99 no existe"));
        verify(repository, times(1)).findByReservaId(99L);
        verify(reservaClient, times(1)).obtenerReserva(99L);
        verify(repository, never()).save(any(Pago.class));
    }

    // =========================================================================
    // 5. actualizar()
    // =========================================================================

    @Test
    @DisplayName("actualizar() → debe actualizar y no llamar Feign si el ID de reserva no cambió")
    void testActualizar_cuandoExisteYNoCambiaReservaId_debeActualizarYRetornarDTO() {
        // ARRANGE
        Pago existing = new Pago();
        existing.setId(1L);
        existing.setReservaId(10L);
        existing.setMontoTotal(new BigDecimal("15000.00"));
        existing.setMetodoPago("TARJETA");
        existing.setEstado("PENDIENTE");

        PagoDTO updateDto = new PagoDTO();
        updateDto.setReservaId(10L); // Mismo ID
        updateDto.setMontoTotal(new BigDecimal("15500.00"));
        updateDto.setMetodoPago("TARJETA");
        updateDto.setEstado("COMPLETADO");

        Pago saved = new Pago();
        saved.setId(1L);
        saved.setReservaId(10L);
        saved.setMontoTotal(new BigDecimal("15500.00"));
        saved.setMetodoPago("TARJETA");
        saved.setEstado("COMPLETADO");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Pago.class))).thenReturn(saved);

        // ACT
        PagoDTO resultado = service.actualizar(1L, updateDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals("COMPLETADO", resultado.getEstado());
        assertEquals(new BigDecimal("15500.00"), resultado.getMontoTotal());
        // No debe llamarse a Feign
        verify(reservaClient, never()).obtenerReserva(anyLong());
        verify(repository, times(1)).save(any(Pago.class));
    }

    @Test
    @DisplayName("actualizar() → debe validar la nueva reserva en Feign si cambió el ID de reserva")
    void testActualizar_cuandoExisteYCambiaReservaValida_debeActualizarYRetornarDTO() {
        // ARRANGE
        Pago existing = new Pago();
        existing.setId(1L);
        existing.setReservaId(10L);
        existing.setMontoTotal(new BigDecimal("15000.00"));
        existing.setMetodoPago("TARJETA");
        existing.setEstado("COMPLETADO");

        PagoDTO updateDto = new PagoDTO();
        updateDto.setReservaId(20L); // Nuevo ID de Reserva
        updateDto.setMontoTotal(new BigDecimal("15000.00"));
        updateDto.setMetodoPago("TARJETA");
        updateDto.setEstado("COMPLETADO");

        Pago saved = new Pago();
        saved.setId(1L);
        saved.setReservaId(20L);
        saved.setMontoTotal(new BigDecimal("15000.00"));
        saved.setMetodoPago("TARJETA");
        saved.setEstado("COMPLETADO");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(reservaClient.obtenerReserva(20L)).thenReturn(new Object());
        when(repository.save(any(Pago.class))).thenReturn(saved);

        // ACT
        PagoDTO resultado = service.actualizar(1L, updateDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(20L, resultado.getReservaId());
        verify(reservaClient, times(1)).obtenerReserva(20L);
        verify(repository, times(1)).save(any(Pago.class));
    }

    @Test
    @DisplayName("actualizar() → debe lanzar excepción si el pago no existe")
    void testActualizar_cuandoNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        PagoDTO dto = new PagoDTO();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.actualizar(99L, dto);
        });

        assertEquals("Pago no encontrado", ex.getMessage());
        verify(repository, times(1)).findById(99L);
        verify(repository, never()).save(any(Pago.class));
    }

    // =========================================================================
    // 6. eliminar()
    // =========================================================================

    @Test
    @DisplayName("eliminar() → debe invocar deleteById del repositorio")
    void testEliminar_debeLlamarDeleteById() {
        // ARRANGE
        doNothing().when(repository).deleteById(1L);

        // ACT
        service.eliminar(1L);

        // ASSERT
        verify(repository, times(1)).deleteById(1L);
    }
}
