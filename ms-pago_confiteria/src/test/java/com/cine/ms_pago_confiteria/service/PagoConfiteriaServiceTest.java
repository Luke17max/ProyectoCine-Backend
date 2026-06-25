package com.cine.ms_pago_confiteria.service;

import com.cine.ms_pago_confiteria.client.ProductoClient;
import com.cine.ms_pago_confiteria.client.UsuarioClient;
import com.cine.ms_pago_confiteria.dto.PagoConfiteriaDTO;
import com.cine.ms_pago_confiteria.model.PagoConfiteria;
import com.cine.ms_pago_confiteria.repository.PagoConfiteriaRepository;
import com.cine.ms_pago_confiteria.service.impl.PagoConfiteriaServiceImpl;
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
 * Pruebas unitarias para PagoConfiteriaServiceImpl (Capa de Servicio)
 * Microservicio: ms-pago_confiteria
 * Paquete: com.cine.ms_pago_confiteria.service
 * ============================================================
 *
 * Estrategia de testing:
 *   @ExtendWith(MockitoExtension.class) → Inicia Mockito sin levantar contexto completo.
 *   @Mock        → Simula PagoConfiteriaRepository, UsuarioClient y ProductoClient.
 *   @InjectMocks → Inyecta los mocks en PagoConfiteriaServiceImpl.
 *
 * Patrón en cada test:
 *   ARRANGE → preparar mocks.
 *   ACT     → ejecutar servicio.
 *   ASSERT  → verificar valores y excepciones.
 *   VERIFY  → verificar llamadas.
 * ============================================================
 */
@ExtendWith(MockitoExtension.class)
class PagoConfiteriaServiceTest {

    @Mock
    private PagoConfiteriaRepository repository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private PagoConfiteriaServiceImpl service;

    // =========================================================================
    // 1. listarTodos()
    // =========================================================================

    @Test
    @DisplayName("listarTodos() → debe mapear y retornar todos los pagos de confitería")
    void testListarTodos_debeRetornarListaDeDTOs() {
        // ARRANGE
        PagoConfiteria p1 = new PagoConfiteria();
        p1.setId(1L);
        p1.setUsuarioId(10L);
        p1.setProductoId(5L);
        p1.setCantidad(2);
        p1.setTotalPagado(new BigDecimal("7000.00"));
        p1.setFechaCompra(LocalDateTime.of(2025, 6, 1, 10, 30));

        PagoConfiteria p2 = new PagoConfiteria();
        p2.setId(2L);
        p2.setUsuarioId(11L);
        p2.setProductoId(3L);
        p2.setCantidad(1);
        p2.setTotalPagado(new BigDecimal("3500.00"));
        p2.setFechaCompra(LocalDateTime.of(2025, 6, 2, 12, 0));

        when(repository.findAll()).thenReturn(List.of(p1, p2));

        // ACT
        List<PagoConfiteriaDTO> resultado = service.listarTodos();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(10L, resultado.get(0).getUsuarioId());
        assertEquals(new BigDecimal("7000.00"), resultado.get(0).getTotalPagado());
        assertEquals(2L, resultado.get(1).getId());
        verify(repository, times(1)).findAll();
    }

    // =========================================================================
    // 2. buscarPorId()
    // =========================================================================

    @Test
    @DisplayName("buscarPorId() → debe retornar DTO cuando el ID existe")
    void testBuscarPorId_cuandoExiste_debeRetornarDTO() {
        // ARRANGE
        PagoConfiteria p = new PagoConfiteria();
        p.setId(1L);
        p.setUsuarioId(10L);
        p.setProductoId(5L);
        p.setCantidad(2);
        p.setTotalPagado(new BigDecimal("7000.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(p));

        // ACT
        PagoConfiteriaDTO resultado = service.buscarPorId(1L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getUsuarioId());
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

        assertEquals("Pago de confitería no encontrado", ex.getMessage());
        verify(repository, times(1)).findById(99L);
    }

    // =========================================================================
    // 3. buscarPorUsuario()
    // =========================================================================

    @Test
    @DisplayName("buscarPorUsuario() → debe retornar lista para el ID de usuario especificado")
    void testBuscarPorUsuario_debeRetornarListaDeDTOs() {
        // ARRANGE
        PagoConfiteria p = new PagoConfiteria();
        p.setId(1L);
        p.setUsuarioId(10L);
        p.setProductoId(5L);
        p.setCantidad(2);
        p.setTotalPagado(new BigDecimal("7000.00"));

        when(repository.findByUsuarioId(10L)).thenReturn(List.of(p));

        // ACT
        List<PagoConfiteriaDTO> resultado = service.buscarPorUsuario(10L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getUsuarioId());
        verify(repository, times(1)).findByUsuarioId(10L);
    }

    // =========================================================================
    // 4. guardar()
    // =========================================================================

    @Test
    @DisplayName("guardar() → debe guardar si el usuario y el producto existen en Feign")
    void testGuardar_cuandoUsuarioYProductoExisten_debeGuardarYRetornarDTO() {
        // ARRANGE
        PagoConfiteriaDTO requestDto = new PagoConfiteriaDTO();
        requestDto.setUsuarioId(10L);
        requestDto.setProductoId(5L);
        requestDto.setCantidad(2);
        requestDto.setTotalPagado(new BigDecimal("7000.00"));

        PagoConfiteria savedEntity = new PagoConfiteria();
        savedEntity.setId(1L);
        savedEntity.setUsuarioId(10L);
        savedEntity.setProductoId(5L);
        savedEntity.setCantidad(2);
        savedEntity.setTotalPagado(new BigDecimal("7000.00"));

        when(usuarioClient.obtenerUsuario(10L)).thenReturn(new Object());
        when(productoClient.obtenerProducto(5L)).thenReturn(new Object());
        when(repository.save(any(PagoConfiteria.class))).thenReturn(savedEntity);

        // ACT
        PagoConfiteriaDTO resultado = service.guardar(requestDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getUsuarioId());
        assertEquals(5L, resultado.getProductoId());
        verify(usuarioClient, times(1)).obtenerUsuario(10L);
        verify(productoClient, times(1)).obtenerProducto(5L);
        verify(repository, times(1)).save(any(PagoConfiteria.class));
    }

    @Test
    @DisplayName("guardar() → debe lanzar excepción si el usuario no existe en Feign")
    void testGuardar_cuandoUsuarioNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        PagoConfiteriaDTO requestDto = new PagoConfiteriaDTO();
        requestDto.setUsuarioId(99L);
        requestDto.setProductoId(5L);

        FeignException.NotFound mockNotFound = mock(FeignException.NotFound.class);
        when(usuarioClient.obtenerUsuario(99L)).thenThrow(mockNotFound);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.guardar(requestDto);
        });

        assertTrue(ex.getMessage().contains("El usuario con ID 99 no existe"));
        verify(usuarioClient, times(1)).obtenerUsuario(99L);
        verify(productoClient, never()).obtenerProducto(anyLong());
        verify(repository, never()).save(any(PagoConfiteria.class));
    }

    @Test
    @DisplayName("guardar() → debe lanzar excepción si el producto no existe en Feign")
    void testGuardar_cuandoProductoNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        PagoConfiteriaDTO requestDto = new PagoConfiteriaDTO();
        requestDto.setUsuarioId(10L);
        requestDto.setProductoId(88L);

        when(usuarioClient.obtenerUsuario(10L)).thenReturn(new Object());

        FeignException.NotFound mockNotFound = mock(FeignException.NotFound.class);
        when(productoClient.obtenerProducto(88L)).thenThrow(mockNotFound);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.guardar(requestDto);
        });

        assertTrue(ex.getMessage().contains("El producto de confitería con ID 88 no existe"));
        verify(usuarioClient, times(1)).obtenerUsuario(10L);
        verify(productoClient, times(1)).obtenerProducto(88L);
        verify(repository, never()).save(any(PagoConfiteria.class));
    }

    // =========================================================================
    // 5. actualizar()
    // =========================================================================

    @Test
    @DisplayName("actualizar() → debe actualizar y no llamar Feign si los IDs no cambiaron")
    void testActualizar_cuandoExisteYNoCambianIds_debeActualizarYRetornarDTO() {
        // ARRANGE
        PagoConfiteria existing = new PagoConfiteria();
        existing.setId(1L);
        existing.setUsuarioId(10L);
        existing.setProductoId(5L);
        existing.setCantidad(2);
        existing.setTotalPagado(new BigDecimal("7000.00"));

        PagoConfiteriaDTO updateDto = new PagoConfiteriaDTO();
        updateDto.setUsuarioId(10L); // Mismo ID
        updateDto.setProductoId(5L);  // Mismo ID
        updateDto.setCantidad(3);
        updateDto.setTotalPagado(new BigDecimal("10500.00"));

        PagoConfiteria saved = new PagoConfiteria();
        saved.setId(1L);
        saved.setUsuarioId(10L);
        saved.setProductoId(5L);
        saved.setCantidad(3);
        saved.setTotalPagado(new BigDecimal("10500.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(PagoConfiteria.class))).thenReturn(saved);

        // ACT
        PagoConfiteriaDTO resultado = service.actualizar(1L, updateDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(3, resultado.getCantidad());
        assertEquals(new BigDecimal("10500.00"), resultado.getTotalPagado());
        verify(usuarioClient, never()).obtenerUsuario(anyLong());
        verify(productoClient, never()).obtenerProducto(anyLong());
        verify(repository, times(1)).save(any(PagoConfiteria.class));
    }

    @Test
    @DisplayName("actualizar() → debe validar el nuevo usuario en Feign si cambió de ID")
    void testActualizar_cuandoExisteYCambiaUsuarioValido_debeActualizarYRetornarDTO() {
        // ARRANGE
        PagoConfiteria existing = new PagoConfiteria();
        existing.setId(1L);
        existing.setUsuarioId(10L);
        existing.setProductoId(5L);
        existing.setCantidad(2);
        existing.setTotalPagado(new BigDecimal("7000.00"));

        PagoConfiteriaDTO updateDto = new PagoConfiteriaDTO();
        updateDto.setUsuarioId(20L); // Nuevo ID de Usuario
        updateDto.setProductoId(5L);
        updateDto.setCantidad(2);
        updateDto.setTotalPagado(new BigDecimal("7000.00"));

        PagoConfiteria saved = new PagoConfiteria();
        saved.setId(1L);
        saved.setUsuarioId(20L);
        saved.setProductoId(5L);
        saved.setCantidad(2);
        saved.setTotalPagado(new BigDecimal("7000.00"));

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(usuarioClient.obtenerUsuario(20L)).thenReturn(new Object());
        when(repository.save(any(PagoConfiteria.class))).thenReturn(saved);

        // ACT
        PagoConfiteriaDTO resultado = service.actualizar(1L, updateDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(20L, resultado.getUsuarioId());
        verify(usuarioClient, times(1)).obtenerUsuario(20L);
        verify(productoClient, never()).obtenerProducto(anyLong());
        verify(repository, times(1)).save(any(PagoConfiteria.class));
    }

    @Test
    @DisplayName("actualizar() → debe lanzar excepción si el pago no existe")
    void testActualizar_cuandoNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        PagoConfiteriaDTO dto = new PagoConfiteriaDTO();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.actualizar(99L, dto);
        });

        assertEquals("Pago de confitería no encontrado", ex.getMessage());
        verify(repository, times(1)).findById(99L);
        verify(repository, never()).save(any(PagoConfiteria.class));
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
