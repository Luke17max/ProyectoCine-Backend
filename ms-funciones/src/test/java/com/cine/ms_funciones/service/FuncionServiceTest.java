package com.cine.ms_funciones.service;

import com.cine.ms_funciones.client.PeliculaClient;
import com.cine.ms_funciones.client.SalaClient;
import com.cine.ms_funciones.dto.FuncionDTO;
import com.cine.ms_funciones.model.Funcion;
import com.cine.ms_funciones.repository.FuncionRepository;
import com.cine.ms_funciones.service.impl.FuncionServiceImpl;
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
 * Pruebas unitarias para FuncionServiceImpl (Capa de Servicio)
 * Microservicio: ms-funciones
 * Paquete: com.cine.ms_funciones.service
 * ============================================================
 *
 * Estrategia de testing:
 *   @ExtendWith(MockitoExtension.class) → Inicia Mockito sin levantar contexto de Spring.
 *   @Mock        → Simula FuncionRepository, PeliculaClient y SalaClient.
 *   @InjectMocks → Inyecta los mocks en la implementación de servicio.
 *
 * Patrón en cada test:
 *   ARRANGE → configurar mocks.
 *   ACT     → ejecutar servicio.
 *   ASSERT  → verificar resultados / excepciones.
 *   VERIFY  → verificar interacciones.
 * ============================================================
 */
@ExtendWith(MockitoExtension.class)
class FuncionServiceTest {

    @Mock
    private FuncionRepository repository;

    @Mock
    private PeliculaClient peliculaClient;

    @Mock
    private SalaClient salaClient;

    @InjectMocks
    private FuncionServiceImpl service;

    // =========================================================================
    // 1. listarTodas()
    // =========================================================================

    @Test
    @DisplayName("listarTodas() → debe mapear y retornar todas las funciones")
    void testListarTodas_debeRetornarListaDeDTOs() {
        // ARRANGE
        Funcion f1 = new Funcion();
        f1.setId(1L);
        f1.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));
        f1.setPrecioBase(new BigDecimal("4000.00"));
        f1.setPeliculaId(10L);
        f1.setSalaId(2L);

        Funcion f2 = new Funcion();
        f2.setId(2L);
        f2.setFechaHora(LocalDateTime.of(2028, 12, 25, 21, 0));
        f2.setPrecioBase(new BigDecimal("4500.00"));
        f2.setPeliculaId(10L);
        f2.setSalaId(2L);

        when(repository.findAll()).thenReturn(List.of(f1, f2));

        // ACT
        List<FuncionDTO> resultado = service.listarTodas();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
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
        Funcion f = new Funcion();
        f.setId(1L);
        f.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));
        f.setPrecioBase(new BigDecimal("4000.00"));
        f.setPeliculaId(10L);
        f.setSalaId(2L);

        when(repository.findById(1L)).thenReturn(Optional.of(f));

        // ACT
        FuncionDTO resultado = service.buscarPorId(1L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getPeliculaId());
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

        assertEquals("Función no encontrada", ex.getMessage());
        verify(repository, times(1)).findById(99L);
    }

    // =========================================================================
    // 3. buscarPorPelicula()
    // =========================================================================

    @Test
    @DisplayName("buscarPorPelicula() → debe retornar lista para el ID de película especificado")
    void testBuscarPorPelicula_debeRetornarListaDeDTOs() {
        // ARRANGE
        Funcion f = new Funcion();
        f.setId(1L);
        f.setPeliculaId(10L);
        f.setSalaId(2L);
        f.setPrecioBase(new BigDecimal("4000.00"));
        f.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));

        when(repository.findByPeliculaId(10L)).thenReturn(List.of(f));

        // ACT
        List<FuncionDTO> resultado = service.buscarPorPelicula(10L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.get(0).getPeliculaId());
        verify(repository, times(1)).findByPeliculaId(10L);
    }

    // =========================================================================
    // 4. guardar()
    // =========================================================================

    @Test
    @DisplayName("guardar() → debe registrar la función si la película y la sala existen en Feign")
    void testGuardar_cuandoPeliculaYSalaExisten_debeGuardarYRetornarDTO() {
        // ARRANGE
        FuncionDTO requestDto = new FuncionDTO();
        requestDto.setPeliculaId(10L);
        requestDto.setSalaId(2L);
        requestDto.setPrecioBase(new BigDecimal("4000.00"));
        requestDto.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));

        Funcion savedEntity = new Funcion();
        savedEntity.setId(1L);
        savedEntity.setPeliculaId(10L);
        savedEntity.setSalaId(2L);
        savedEntity.setPrecioBase(new BigDecimal("4000.00"));
        savedEntity.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));

        // Feign clients devuelven mock/dummy data (validación exitosa sin excepciones)
        when(peliculaClient.obtenerPelicula(10L)).thenReturn(new Object());
        when(salaClient.obtenerSala(2L)).thenReturn(new Object());
        when(repository.save(any(Funcion.class))).thenReturn(savedEntity);

        // ACT
        FuncionDTO resultado = service.guardar(requestDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(10L, resultado.getPeliculaId());
        assertEquals(2L, resultado.getSalaId());
        verify(peliculaClient, times(1)).obtenerPelicula(10L);
        verify(salaClient, times(1)).obtenerSala(2L);
        verify(repository, times(1)).save(any(Funcion.class));
    }

    @Test
    @DisplayName("guardar() → debe lanzar excepción si la película no existe en Feign")
    void testGuardar_cuandoPeliculaNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        FuncionDTO requestDto = new FuncionDTO();
        requestDto.setPeliculaId(99L);
        requestDto.setSalaId(2L);

        // Lanzamos Mock FeignException.NotFound
        FeignException.NotFound mockNotFound = mock(FeignException.NotFound.class);
        when(peliculaClient.obtenerPelicula(99L)).thenThrow(mockNotFound);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.guardar(requestDto);
        });

        assertTrue(ex.getMessage().contains("La película con ID 99 no existe"));
        verify(peliculaClient, times(1)).obtenerPelicula(99L);
        verify(salaClient, never()).obtenerSala(anyLong());
        verify(repository, never()).save(any(Funcion.class));
    }

    @Test
    @DisplayName("guardar() → debe lanzar excepción si la sala no existe en Feign")
    void testGuardar_cuandoSalaNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        FuncionDTO requestDto = new FuncionDTO();
        requestDto.setPeliculaId(10L);
        requestDto.setSalaId(88L);

        when(peliculaClient.obtenerPelicula(10L)).thenReturn(new Object());

        // Sala Feign lanza NotFound
        FeignException.NotFound mockNotFound = mock(FeignException.NotFound.class);
        when(salaClient.obtenerSala(88L)).thenThrow(mockNotFound);

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.guardar(requestDto);
        });

        assertTrue(ex.getMessage().contains("La sala con ID 88 no existe"));
        verify(peliculaClient, times(1)).obtenerPelicula(10L);
        verify(salaClient, times(1)).obtenerSala(88L);
        verify(repository, never()).save(any(Funcion.class));
    }

    // =========================================================================
    // 5. actualizar()
    // =========================================================================

    @Test
    @DisplayName("actualizar() → debe actualizar y no llamar Feign si los IDs no cambiaron")
    void testActualizar_cuandoExisteYNoCambianIds_debeActualizarYRetornarDTO() {
        // ARRANGE
        Funcion existing = new Funcion();
        existing.setId(1L);
        existing.setPeliculaId(10L);
        existing.setSalaId(2L);
        existing.setPrecioBase(new BigDecimal("4000.00"));
        existing.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));

        FuncionDTO updateDto = new FuncionDTO();
        updateDto.setPeliculaId(10L); // Mismo ID
        updateDto.setSalaId(2L);     // Mismo ID
        updateDto.setPrecioBase(new BigDecimal("4500.00"));
        updateDto.setFechaHora(LocalDateTime.of(2028, 12, 25, 20, 0));

        Funcion saved = new Funcion();
        saved.setId(1L);
        saved.setPeliculaId(10L);
        saved.setSalaId(2L);
        saved.setPrecioBase(new BigDecimal("4500.00"));
        saved.setFechaHora(LocalDateTime.of(2028, 12, 25, 20, 0));

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Funcion.class))).thenReturn(saved);

        // ACT
        FuncionDTO resultado = service.actualizar(1L, updateDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(new BigDecimal("4500.00"), resultado.getPrecioBase());
        // No deben llamarse los Feign clients
        verify(peliculaClient, never()).obtenerPelicula(anyLong());
        verify(salaClient, never()).obtenerSala(anyLong());
        verify(repository, times(1)).save(any(Funcion.class));
    }

    @Test
    @DisplayName("actualizar() → debe validar película nueva en Feign si cambió de ID")
    void testActualizar_cuandoExisteYCambiaPeliculaValida_debeActualizarYRetornarDTO() {
        // ARRANGE
        Funcion existing = new Funcion();
        existing.setId(1L);
        existing.setPeliculaId(10L);
        existing.setSalaId(2L);
        existing.setPrecioBase(new BigDecimal("4000.00"));
        existing.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));

        FuncionDTO updateDto = new FuncionDTO();
        updateDto.setPeliculaId(15L); // Nueva Película
        updateDto.setSalaId(2L);
        updateDto.setPrecioBase(new BigDecimal("4000.00"));
        updateDto.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));

        Funcion saved = new Funcion();
        saved.setId(1L);
        saved.setPeliculaId(15L);
        saved.setSalaId(2L);
        saved.setPrecioBase(new BigDecimal("4000.00"));
        saved.setFechaHora(LocalDateTime.of(2028, 12, 25, 18, 0));

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(peliculaClient.obtenerPelicula(15L)).thenReturn(new Object());
        when(repository.save(any(Funcion.class))).thenReturn(saved);

        // ACT
        FuncionDTO resultado = service.actualizar(1L, updateDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(15L, resultado.getPeliculaId());
        verify(peliculaClient, times(1)).obtenerPelicula(15L);
        verify(salaClient, never()).obtenerSala(anyLong());
        verify(repository, times(1)).save(any(Funcion.class));
    }

    @Test
    @DisplayName("actualizar() → debe lanzar excepción si la función no existe")
    void testActualizar_cuandoNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        FuncionDTO dto = new FuncionDTO();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            service.actualizar(99L, dto);
        });

        assertEquals("Función no encontrada", ex.getMessage());
        verify(repository, times(1)).findById(99L);
        verify(repository, never()).save(any(Funcion.class));
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
