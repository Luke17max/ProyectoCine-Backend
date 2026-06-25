package com.cine.ms_notificaciones.service;

import com.cine.ms_notificaciones.client.PagoClient;
import com.cine.ms_notificaciones.client.ReservaClient;
import com.cine.ms_notificaciones.dto.NotificacionDTO;
import com.cine.ms_notificaciones.model.Notificacion;
import com.cine.ms_notificaciones.repository.NotificacionRepository;
import com.cine.ms_notificaciones.service.impl.NotificacionServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * ============================================================
 * Pruebas unitarias para NotificacionServiceImpl (Capa de Servicio)
 * Microservicio: ms-notificaciones
 * Paquete: com.cine.ms_notificaciones.service
 * ============================================================
 *
 * Estrategia de testing:
 *   @ExtendWith(MockitoExtension.class) → Inicia Mockito sin contexto pesado.
 *   @Mock        → Simula NotificacionRepository, ReservaClient y PagoClient.
 *   @InjectMocks → Inyecta los mocks en NotificacionServiceImpl.
 *
 * Patrón en cada test:
 *   ARRANGE → preparar mocks.
 *   ACT     → ejecutar servicio.
 *   ASSERT  → verificar valores y excepciones.
 *   VERIFY  → verificar llamadas.
 * ============================================================
 */
@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository repository;

    @Mock
    private ReservaClient reservaClient;

    @Mock
    private PagoClient pagoClient;

    @InjectMocks
    private NotificacionServiceImpl service;

    // =========================================================================
    // 1. listarTodas()
    // =========================================================================

    @Test
    @DisplayName("listarTodas() → debe mapear y retornar todas las notificaciones")
    void testListarTodas_debeRetornarListaDeDTOs() {
        // ARRANGE
        Notificacion n1 = new Notificacion();
        n1.setId(1L);
        n1.setReservaId(10L);
        n1.setPagoId(100L);
        n1.setTipo("CONFIRMACION");
        n1.setMensaje("Tu reserva 10 está confirmada");
        n1.setFechaCreacion(LocalDateTime.of(2025, 6, 1, 10, 0));

        Notificacion n2 = new Notificacion();
        n2.setId(2L);
        n2.setReservaId(11L);
        n2.setPagoId(null);
        n2.setTipo("RECORDATORIO");
        n2.setMensaje("Recordatorio de tu reserva 11");
        n2.setFechaCreacion(LocalDateTime.of(2025, 6, 2, 10, 0));

        when(repository.findAll()).thenReturn(List.of(n1, n2));

        // ACT
        List<NotificacionDTO> resultado = service.listarTodas();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(10L, resultado.get(0).getReservaId());
        assertEquals("CONFIRMACION", resultado.get(0).getTipo());
        assertEquals(2L, resultado.get(1).getId());
        assertNull(resultado.get(1).getPagoId());
        verify(repository, times(1)).findAll();
    }

    // =========================================================================
    // 2. buscarPorId()
    // =========================================================================

    @Test
    @DisplayName("buscarPorId() → debe retornar DTO cuando el ID existe")
    void testBuscarPorId_cuandoExiste_debeRetornarDTO() {
        // ARRANGE
        Notificacion n = new Notificacion();
        n.setId(1L);
        n.setReservaId(10L);
        n.setPagoId(100L);
        n.setTipo("CONFIRMACION");
        n.setMensaje("Tu reserva 10 está confirmada");

        when(repository.findById(1L)).thenReturn(Optional.of(n));

        // ACT
        NotificacionDTO resultado = service.buscarPorId(1L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("CONFIRMACION", resultado.getTipo());
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

        assertEquals("Notificación no encontrada", ex.getMessage());
        verify(repository, times(1)).findById(99L);
    }

    // =========================================================================
    // 3. buscarPorReserva()
    // =========================================================================

    @Test
    @DisplayName("buscarPorReserva() → debe retornar lista para el ID de reserva especificado")
    void testBuscarPorReserva_debeRetornarListaDeDTOs() {
        // ARRANGE
        Notificacion n = new Notificacion();
        n.setId(1L);
        n.setReservaId(10L);
        n.setTipo("CONFIRMACION");
        n.setMensaje("Tu reserva 10 está confirmada");

        when(repository.findByReservaId(10L)).thenReturn(List.of(n));

        // ACT
        List<NotificacionDTO> resultado = service.buscarPorReserva(10L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getReservaId());
        verify(repository, times(1)).findByReservaId(10L);
    }

    // =========================================================================
    // 4. guardar()
    // =========================================================================

    @Test
    @DisplayName("guardar() → debe guardar si la reserva existe y no hay pago especificado")
    void testGuardar_cuandoReservaExisteYNoHayPago_debeGuardarYRetornarDTO() {
        // ARRANGE
        NotificacionDTO requestDto = new NotificacionDTO();
        requestDto.setReservaId(10L);
        requestDto.setPagoId(null);
        requestDto.setTipo("RECORDATORIO");
        requestDto.setMensaje("Recordatorio...");

        Notificacion savedEntity = new Notificacion();
        savedEntity.setId(1L);
        savedEntity.setReservaId(10L);
        savedEntity.setPagoId(null);
        savedEntity.setTipo("RECORDATORIO");
        savedEntity.setMensaje("Recordatorio...");

        when(reservaClient.obtenerReserva(10L)).thenReturn(new Object());
        when(repository.save(any(Notificacion.class))).thenReturn(savedEntity);

        // ACT
        NotificacionDTO resultado = service.guardar(requestDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertNull(resultado.getPagoId());
        verify(reservaClient, times(1)).obtenerReserva(10L);
        verify(pagoClient, never()).obtenerPago(anyLong());
        verify(repository, times(1)).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("guardar() → debe guardar si la reserva y el pago existen en Feign")
    void testGuardar_cuandoReservaXPagoExisten_debeGuardarYRetornarDTO() {
        // ARRANGE
        NotificacionDTO requestDto = new NotificacionDTO();
        requestDto.setReservaId(10L);
        requestDto.setPagoId(100L);
        requestDto.setTipo("CONFIRMACION");
        requestDto.setMensaje("Pago confirmado");

        Notificacion savedEntity = new Notificacion();
        savedEntity.setId(1L);
        savedEntity.setReservaId(10L);
        savedEntity.setPagoId(100L);
        savedEntity.setTipo("CONFIRMACION");
        savedEntity.setMensaje("Pago confirmado");

        when(reservaClient.obtenerReserva(10L)).thenReturn(new Object());
        when(pagoClient.obtenerPago(100L)).thenReturn(new Object());
        when(repository.save(any(Notificacion.class))).thenReturn(savedEntity);

        // ACT
        NotificacionDTO resultado = service.guardar(requestDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(100L, resultado.getPagoId());
        verify(reservaClient, times(1)).obtenerReserva(10L);
        verify(pagoClient, times(1)).obtenerPago(100L);
        verify(repository, times(1)).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("guardar() → debe lanzar excepción si la reserva no existe en Feign")
    void testGuardar_cuandoReservaNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        NotificacionDTO requestDto = new NotificacionDTO();
        requestDto.setReservaId(99L);

        FeignException.NotFound mockNotFound = mock(FeignException.NotFound.class);
        when(reservaClient.obtenerReserva(99L)).thenThrow(mockNotFound);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.guardar(requestDto);
        });

        assertTrue(ex.getMessage().contains("La reserva con ID 99 no existe"));
        verify(reservaClient, times(1)).obtenerReserva(99L);
        verify(pagoClient, never()).obtenerPago(anyLong());
        verify(repository, never()).save(any(Notificacion.class));
    }

    @Test
    @DisplayName("guardar() → debe lanzar excepción si el pago no existe en Feign")
    void testGuardar_cuandoPagoNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        NotificacionDTO requestDto = new NotificacionDTO();
        requestDto.setReservaId(10L);
        requestDto.setPagoId(999L);

        when(reservaClient.obtenerReserva(10L)).thenReturn(new Object());

        FeignException.NotFound mockNotFound = mock(FeignException.NotFound.class);
        when(pagoClient.obtenerPago(999L)).thenThrow(mockNotFound);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.guardar(requestDto);
        });

        assertTrue(ex.getMessage().contains("El pago con ID 999 no existe"));
        verify(reservaClient, times(1)).obtenerReserva(10L);
        verify(pagoClient, times(1)).obtenerPago(999L);
        verify(repository, never()).save(any(Notificacion.class));
    }

    // =========================================================================
    // 5. eliminar()
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
