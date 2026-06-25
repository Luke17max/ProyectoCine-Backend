package com.cine.ms_reservas.service;

import com.cine.ms_reservas.client.FuncionClient;
import com.cine.ms_reservas.dto.ReservaDTO;
import com.cine.ms_reservas.model.Reserva;
import com.cine.ms_reservas.repository.ReservaRepository;
import com.cine.ms_reservas.service.impl.ReservaServiceImpl;

import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceImplTest {

    @Mock
    private ReservaRepository repository;

    @Mock
    private FuncionClient funcionClient;

    @InjectMocks
    private ReservaServiceImpl service;

    // Entidades de apoyo reutilizadas en los tests
    private Reserva reservaEntity1;
    private Reserva reservaEntity2;

    @BeforeEach
    void setUp() {
        reservaEntity1 = new Reserva();
        reservaEntity1.setId(1L);
        reservaEntity1.setUsuarioId(10L);
        reservaEntity1.setFuncionId(5L);
        reservaEntity1.setCantidadAsientos(2);
        reservaEntity1.setEstado("PENDIENTE");

        reservaEntity2 = new Reserva();
        reservaEntity2.setId(2L);
        reservaEntity2.setUsuarioId(11L);
        reservaEntity2.setFuncionId(6L);
        reservaEntity2.setCantidadAsientos(4);
        reservaEntity2.setEstado("PAGADA");
    }

    // =========================================================
    // listarTodas()
    // =========================================================

    @Test
    @DisplayName("listarTodas() - debe retornar lista de DTOs mapeados")
    void listarTodas_DeberiaRetornarListaDeDTOs() {
        // ARRANGE
        when(repository.findAll()).thenReturn(Arrays.asList(reservaEntity1, reservaEntity2));

        // ACT
        List<ReservaDTO> resultado = service.listarTodas();

        // ASSERT
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getEstado()).isEqualTo("PENDIENTE");
        assertThat(resultado.get(1).getId()).isEqualTo(2L);
        assertThat(resultado.get(1).getEstado()).isEqualTo("PAGADA");

        // VERIFY
        verify(repository).findAll();
    }

    // =========================================================
    // buscarPorId()
    // =========================================================

    @Test
    @DisplayName("buscarPorId() - debe retornar DTO cuando la reserva existe")
    void buscarPorId_CuandoExiste_DeberiaRetornarDTO() {
        // ARRANGE
        when(repository.findById(1L)).thenReturn(Optional.of(reservaEntity1));

        // ACT
        ReservaDTO resultado = service.buscarPorId(1L);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getUsuarioId()).isEqualTo(10L);
        assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");

        // VERIFY
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId() - debe lanzar RuntimeException cuando la reserva no existe")
    void buscarPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Reserva no encontrada");

        // VERIFY
        verify(repository).findById(99L);
    }

    // =========================================================
    // buscarPorUsuario()
    // =========================================================

    @Test
    @DisplayName("buscarPorUsuario() - debe retornar las reservas del usuario indicado")
    void buscarPorUsuario_DeberiaRetornarListaDeDTOs() {
        // ARRANGE
        when(repository.findByUsuarioId(10L)).thenReturn(Arrays.asList(reservaEntity1));

        // ACT
        List<ReservaDTO> resultado = service.buscarPorUsuario(10L);

        // ASSERT
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsuarioId()).isEqualTo(10L);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);

        // VERIFY
        verify(repository).findByUsuarioId(10L);
    }

    // =========================================================
    // guardar()
    // =========================================================

    @Test
    @DisplayName("guardar() - debe forzar estado PENDIENTE y persistir la reserva")
    void guardar_DeberiaForzarEstadoPendienteYGuardar() {
        // ARRANGE
        ReservaDTO dto = new ReservaDTO();
        dto.setUsuarioId(10L);
        dto.setFuncionId(5L);
        dto.setCantidadAsientos(3);
        dto.setEstado("PAGADA"); // el servicio debe ignorar este estado

        Reserva saved = new Reserva();
        saved.setId(1L);
        saved.setUsuarioId(10L);
        saved.setFuncionId(5L);
        saved.setCantidadAsientos(3);
        saved.setEstado("PENDIENTE"); // siempre PENDIENTE al crear

        when(funcionClient.obtenerFuncion(5L)).thenReturn(new Object()); // Feign responde OK = función válida
        when(repository.save(any(Reserva.class))).thenReturn(saved);

        // ACT
        ReservaDTO resultado = service.guardar(dto);

        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");

        // VERIFY: save recibió una entidad con estado PENDIENTE
        verify(repository).save(argThat(r -> "PENDIENTE".equals(r.getEstado())));
        verify(funcionClient).obtenerFuncion(5L);
    }

    @Test
    @DisplayName("guardar() - debe lanzar RuntimeException cuando la función no existe (FeignException.NotFound)")
    void guardar_CuandoFuncionNoExiste_DeberiaLanzarExcepcion() {
        // ARRANGE
        ReservaDTO dto = new ReservaDTO();
        dto.setUsuarioId(10L);
        dto.setFuncionId(99L);
        dto.setCantidadAsientos(2);

        FeignException.NotFound notFound = mock(FeignException.NotFound.class);
        doThrow(notFound).when(funcionClient).obtenerFuncion(99L);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.guardar(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("función solicitada no existe");

        // VERIFY: repository.save nunca fue llamado
        verify(repository, never()).save(any());
    }

    // =========================================================
    // actualizarEstado()
    // =========================================================

    @Test
    @DisplayName("actualizarEstado() - debe guardar el estado en mayúsculas")
    void actualizarEstado_DeberiaGuardarEstadoEnMayusculas() {
        // ARRANGE
        when(repository.findById(1L)).thenReturn(Optional.of(reservaEntity1));
        Reserva updatedEntity = new Reserva();
        updatedEntity.setId(1L);
        updatedEntity.setUsuarioId(10L);
        updatedEntity.setFuncionId(5L);
        updatedEntity.setCantidadAsientos(2);
        updatedEntity.setEstado("PAGADA");
        when(repository.save(any(Reserva.class))).thenReturn(updatedEntity);

        // ACT
        ReservaDTO resultado = service.actualizarEstado(1L, "pagada"); // minúsculas intencionales

        // ASSERT
        assertThat(resultado.getEstado()).isEqualTo("PAGADA");

        // VERIFY: save fue llamado con el estado en mayúsculas
        verify(repository).save(argThat(r -> "PAGADA".equals(r.getEstado())));
    }

    @Test
    @DisplayName("actualizarEstado() - debe lanzar RuntimeException cuando la reserva no existe")
    void actualizarEstado_CuandoNoExiste_DeberiaLanzarExcepcion() {
        // ARRANGE
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.actualizarEstado(99L, "PAGADA"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Reserva no encontrada");

        // VERIFY
        verify(repository, never()).save(any());
    }

    // =========================================================
    // eliminar()
    // =========================================================

    @Test
    @DisplayName("eliminar() - debe invocar deleteById con el id correcto")
    void eliminar_DeberiaInvocarDeleteById() {
        // ARRANGE
        doNothing().when(repository).deleteById(1L);

        // ACT
        service.eliminar(1L);

        // ASSERT - (verificación implícita vía VERIFY)
        // VERIFY
        verify(repository).deleteById(1L);
    }
}
