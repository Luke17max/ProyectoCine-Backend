package com.cine.ms_confiteria.service;

import com.cine.ms_confiteria.dto.ConfiteriaDTO;
import com.cine.ms_confiteria.model.Confiteria;
import com.cine.ms_confiteria.repository.ConfiteriaRepository;
import com.cine.ms_confiteria.service.impl.ConfiteriaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ============================================================
 * Pruebas unitarias para ConfiteriaServiceImpl (Capa de Servicio)
 * Microservicio: ms-confiteria
 * Paquete: com.cine.ms_confiteria.service
 * ============================================================
 *
 * Estrategia de testing:
 *   @ExtendWith(MockitoExtension.class) → Inicia Mockito sin levantar contexto de Spring.
 *   @Mock        → Simula el repositorio ConfiteriaRepository.
 *   @InjectMocks → Inyecta el mock en la clase de implementación del servicio.
 *
 * Patrón en cada test:
 *   ARRANGE → configurar comportamiento del mock repository.
 *   ACT     → llamar al método del servicio.
 *   ASSERT  → verificar valores devueltos, aserciones y excepciones.
 *   VERIFY  → confirmar interacciones con el repositorio.
 * ============================================================
 */
@ExtendWith(MockitoExtension.class)
class ConfiteriaServiceTest {

    @Mock
    private ConfiteriaRepository repository;

    @InjectMocks
    private ConfiteriaServiceImpl service;

    // =========================================================================
    // 1. listarTodos()
    // =========================================================================

    @Test
    @DisplayName("listarTodos() → debe mapear y retornar lista de todos los DTOs")
    void testListarTodos_debeRetornarListaDeDTOs() {
        // ARRANGE
        Confiteria c1 = new Confiteria();
        c1.setId(1L);
        c1.setNombre("Perrito Caliente");
        c1.setPrecio(new BigDecimal("3000.00"));
        c1.setStock(20);
        c1.setCategoria("SNACK");

        Confiteria c2 = new Confiteria();
        c2.setId(2L);
        c2.setNombre("Agua Mineral");
        c2.setPrecio(new BigDecimal("1500.00"));
        c2.setStock(40);
        c2.setCategoria("BEBIDA");

        when(repository.findAll()).thenReturn(List.of(c1, c2));

        // ACT
        List<ConfiteriaDTO> resultado = service.listarTodos();

        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Perrito Caliente", resultado.get(0).getNombre());
        assertEquals("Agua Mineral", resultado.get(1).getNombre());
        verify(repository, times(1)).findAll();
    }

    // =========================================================================
    // 2. buscarPorId()
    // =========================================================================

    @Test
    @DisplayName("buscarPorId() → debe retornar DTO cuando el ID existe")
    void testBuscarPorId_cuandoExiste_debeRetornarDTO() {
        // ARRANGE
        Confiteria c1 = new Confiteria();
        c1.setId(1L);
        c1.setNombre("Perrito Caliente");
        c1.setPrecio(new BigDecimal("3000.00"));
        c1.setStock(20);
        c1.setCategoria("SNACK");

        when(repository.findById(1L)).thenReturn(Optional.of(c1));

        // ACT
        ConfiteriaDTO resultado = service.buscarPorId(1L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Perrito Caliente", resultado.getNombre());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId() → debe lanzar RuntimeException cuando el ID no existe")
    void testBuscarPorId_cuandoNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            service.buscarPorId(99L);
        });

        assertEquals("Confiteria no encontrado", excepcion.getMessage());
        verify(repository, times(1)).findById(99L);
    }

    // =========================================================================
    // 3. buscarPorCategoria()
    // =========================================================================

    @Test
    @DisplayName("buscarPorCategoria() → debe retornar lista filtrada e ignorar mayúsculas/minúsculas")
    void testBuscarPorCategoria_debeRetornarListaFiltrada() {
        // ARRANGE
        Confiteria c1 = new Confiteria();
        c1.setId(1L);
        c1.setNombre("Agua Mineral");
        c1.setPrecio(new BigDecimal("1500.00"));
        c1.setStock(40);
        c1.setCategoria("BEBIDA");

        when(repository.findByCategoriaIgnoreCase("bebida")).thenReturn(List.of(c1));

        // ACT
        List<ConfiteriaDTO> resultado = service.buscarPorCategoria("bebida");

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("BEBIDA", resultado.get(0).getCategoria());
        verify(repository, times(1)).findByCategoriaIgnoreCase("bebida");
    }

    // =========================================================================
    // 4. guardar()
    // =========================================================================

    @Test
    @DisplayName("guardar() → debe mapear a entidad, guardar y retornar DTO con ID asignado")
    void testGuardar_debeGuardarYRetornarDTO() {
        // ARRANGE
        ConfiteriaDTO requestDto = new ConfiteriaDTO();
        requestDto.setNombre("Nachos Gigantes");
        requestDto.setPrecio(new BigDecimal("4500.00"));
        requestDto.setStock(15);
        requestDto.setCategoria("SNACK");

        Confiteria savedEntity = new Confiteria();
        savedEntity.setId(5L);
        savedEntity.setNombre("Nachos Gigantes");
        savedEntity.setPrecio(new BigDecimal("4500.00"));
        savedEntity.setStock(15);
        savedEntity.setCategoria("SNACK");

        // El mock save() recibe una entidad de entrada y devuelve la entidad guardada (con ID 5)
        when(repository.save(any(Confiteria.class))).thenReturn(savedEntity);

        // ACT
        ConfiteriaDTO resultado = service.guardar(requestDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals("Nachos Gigantes", resultado.getNombre());
        verify(repository, times(1)).save(any(Confiteria.class));
    }

    // =========================================================================
    // 5. actualizar()
    // =========================================================================

    @Test
    @DisplayName("actualizar() → debe actualizar campos de entidad existente y guardarla")
    void testActualizar_cuandoExiste_debeActualizarYRetornarDTO() {
        // ARRANGE
        Confiteria existingEntity = new Confiteria();
        existingEntity.setId(1L);
        existingEntity.setNombre("Perrito Caliente");
        existingEntity.setPrecio(new BigDecimal("3000.00"));
        existingEntity.setStock(20);
        existingEntity.setCategoria("SNACK");

        ConfiteriaDTO updateDto = new ConfiteriaDTO();
        updateDto.setNombre("Súper Perrito");
        updateDto.setPrecio(new BigDecimal("3500.00"));
        updateDto.setStock(25);
        updateDto.setCategoria("SNACK");

        Confiteria savedEntity = new Confiteria();
        savedEntity.setId(1L);
        savedEntity.setNombre("Súper Perrito");
        savedEntity.setPrecio(new BigDecimal("3500.00"));
        savedEntity.setStock(25);
        savedEntity.setCategoria("SNACK");

        when(repository.findById(1L)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(Confiteria.class))).thenReturn(savedEntity);

        // ACT
        ConfiteriaDTO resultado = service.actualizar(1L, updateDto);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Súper Perrito", resultado.getNombre());
        assertEquals(25, resultado.getStock());
        assertEquals(new BigDecimal("3500.00"), resultado.getPrecio());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Confiteria.class));
    }

    @Test
    @DisplayName("actualizar() → debe lanzar excepción si la confitería a actualizar no existe")
    void testActualizar_cuandoNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        ConfiteriaDTO dto = new ConfiteriaDTO();
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            service.actualizar(99L, dto);
        });

        assertEquals("Confiteria no encontrado", excepcion.getMessage());
        verify(repository, times(1)).findById(99L);
        verify(repository, never()).save(any(Confiteria.class));
    }

    // =========================================================================
    // 6. actualizarStock()
    // =========================================================================

    @Test
    @DisplayName("actualizarStock() → debe sumar cantidad positiva al stock existente")
    void testActualizarStock_cuandoExisteYStockSuficiente_debeModificarStock() {
        // ARRANGE
        Confiteria entity = new Confiteria();
        entity.setId(1L);
        entity.setNombre("Soda");
        entity.setStock(10);
        entity.setPrecio(new BigDecimal("1000.00"));
        entity.setCategoria("BEBIDA");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        // Guardará con stock = 10 + 5 = 15
        when(repository.save(any(Confiteria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        service.actualizarStock(1L, 5);

        // ASSERT
        assertEquals(15, entity.getStock());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("actualizarStock() → debe restar cantidad negativa y lanzar excepción si queda stock menor a 0")
    void testActualizarStock_cuandoExisteYStockInsuficiente_debeLanzarRuntimeException() {
        // ARRANGE
        Confiteria entity = new Confiteria();
        entity.setId(1L);
        entity.setNombre("Soda");
        entity.setStock(10);
        entity.setPrecio(new BigDecimal("1000.00"));
        entity.setCategoria("BEBIDA");

        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        // ACT & ASSERT
        // Queremos restar 12 unidades teniendo solo 10
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            service.actualizarStock(1L, -12);
        });

        assertEquals("Stock insuficiente para realizar la operación", excepcion.getMessage());
        assertEquals(10, entity.getStock()); // El stock original permanece intacto
        verify(repository, times(1)).findById(1L);
        verify(repository, never()).save(any(Confiteria.class));
    }

    @Test
    @DisplayName("actualizarStock() → debe lanzar excepción si la confitería a modificar stock no existe")
    void testActualizarStock_cuandoNoExiste_debeLanzarRuntimeException() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            service.actualizarStock(99L, 5);
        });

        assertEquals("Confiteria no encontrado", excepcion.getMessage());
        verify(repository, times(1)).findById(99L);
        verify(repository, never()).save(any(Confiteria.class));
    }

    // =========================================================================
    // 7. eliminar()
    // =========================================================================

    @Test
    @DisplayName("eliminar() → debe llamar a deleteById del repositorio")
    void testEliminar_debeLlamarDeleteById() {
        // ARRANGE
        doNothing().when(repository).deleteById(1L);

        // ACT
        service.eliminar(1L);

        // ASSERT
        verify(repository, times(1)).deleteById(1L);
    }
}
