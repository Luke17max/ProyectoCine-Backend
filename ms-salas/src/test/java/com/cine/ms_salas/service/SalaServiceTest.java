package com.cine.ms_salas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cine.ms_salas.clients.SucursalClient;
import com.cine.ms_salas.dto.SalaDTO;
import com.cine.ms_salas.model.Sala;
import com.cine.ms_salas.repository.SalaRepository;
import com.cine.ms_salas.service.impl.SalaServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SalaServiceTest {

    @Mock
    private SalaRepository repository;

    @Mock
    private SucursalClient sucursalClient;

    @InjectMocks
    private SalaServiceImpl service;

    @Test
    public void testListarTodas_Success() {
        // ARRANGE: preparar datos y mocks
        Sala sala1 = new Sala();
        sala1.setId(1L);
        sala1.setNombre("Sala IMAX");
        sala1.setCapacidad(250);
        sala1.setSucursalId(10L);

        Sala sala2 = new Sala();
        sala2.setId(2L);
        sala2.setNombre("Sala Premium 3D");
        sala2.setCapacidad(120);
        sala2.setSucursalId(10L);

        when(repository.findAll()).thenReturn(Arrays.asList(sala1, sala2));

        // ACT: ejecutar método o endpoint
        List<SalaDTO> resultado = service.listarTodas();

        // ASSERT: verificar resultado esperado
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Sala IMAX", resultado.get(0).getNombre());
        assertEquals("Sala Premium 3D", resultado.get(1).getNombre());

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testBuscarPorId_Success() {
        // ARRANGE: preparar datos y mocks
        Long id = 1L;
        Sala sala = new Sala();
        sala.setId(id);
        sala.setNombre("Sala IMAX");
        sala.setCapacidad(250);
        sala.setSucursalId(10L);

        when(repository.findById(id)).thenReturn(java.util.Optional.of(sala));

        // ACT: ejecutar método
        SalaDTO resultado = service.buscarPorId(id);

        // ASSERT: verificar resultado esperado
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Sala IMAX", resultado.getNombre());
        assertEquals(250, resultado.getCapacidad());
        assertEquals(10L, resultado.getSucursalId());

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(repository, times(1)).findById(id);
    }

    @Test
    public void testBuscarPorSucursal_Success() {
        // ARRANGE: preparar datos y mocks
        Long sucursalId = 10L;
        Sala sala1 = new Sala();
        sala1.setId(1L);
        sala1.setNombre("Sala IMAX");
        sala1.setCapacidad(250);
        sala1.setSucursalId(sucursalId);

        when(repository.findBySucursalId(sucursalId)).thenReturn(Arrays.asList(sala1));

        // ACT: ejecutar método
        List<SalaDTO> resultado = service.buscarPorSucursal(sucursalId);

        // ASSERT: verificar resultado esperado
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Sala IMAX", resultado.get(0).getNombre());

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(repository, times(1)).findBySucursalId(sucursalId);
    }

    @Test
    public void testGuardarSala_Success() {
        // ARRANGE: preparar datos y mocks
        Long sucursalId = 10L;
        
        SalaDTO inputDto = new SalaDTO();
        inputDto.setNombre("Sala IMAX");
        inputDto.setCapacidad(250);
        inputDto.setSucursalId(sucursalId);

        Sala salaGuardada = new Sala();
        salaGuardada.setId(1L);
        salaGuardada.setNombre("Sala IMAX");
        salaGuardada.setCapacidad(250);
        salaGuardada.setSucursalId(sucursalId);

        // Simulamos la llamada exitosa del Feign client
        when(sucursalClient.obtenerSucursal(sucursalId)).thenReturn(new Object());
        // Simulamos el guardado en el repositorio
        when(repository.save(any(Sala.class))).thenReturn(salaGuardada);

        // ACT: ejecutar método
        SalaDTO resultado = service.guardar(inputDto);

        // ASSERT: verificar resultado esperado
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Sala IMAX", resultado.getNombre());
        assertEquals(250, resultado.getCapacidad());
        assertEquals(sucursalId, resultado.getSucursalId());

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(sucursalClient, times(1)).obtenerSucursal(sucursalId);
        verify(repository, times(1)).save(any(Sala.class));
    }

    @Test
    public void testActualizarSala_Success() {
        // ARRANGE: preparar datos y mocks
        Long id = 1L;
        
        Sala salaExistente = new Sala();
        salaExistente.setId(id);
        salaExistente.setNombre("Sala IMAX");
        salaExistente.setCapacidad(250);
        salaExistente.setSucursalId(10L);

        SalaDTO inputDto = new SalaDTO();
        inputDto.setNombre("Sala IMAX Modificada");
        inputDto.setCapacidad(200);
        inputDto.setSucursalId(20L); // Cambia el sucursalId de 10L a 20L

        Sala salaActualizada = new Sala();
        salaActualizada.setId(id);
        salaActualizada.setNombre("Sala IMAX Modificada");
        salaActualizada.setCapacidad(200);
        salaActualizada.setSucursalId(20L);

        when(repository.findById(id)).thenReturn(java.util.Optional.of(salaExistente));
        when(sucursalClient.obtenerSucursal(20L)).thenReturn(new Object());
        when(repository.save(any(Sala.class))).thenReturn(salaActualizada);

        // ACT: ejecutar método
        SalaDTO resultado = service.actualizar(id, inputDto);

        // ASSERT: verificar resultado esperado
        assertNotNull(resultado);
        assertEquals("Sala IMAX Modificada", resultado.getNombre());
        assertEquals(200, resultado.getCapacidad());
        assertEquals(20L, resultado.getSucursalId());

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(repository, times(1)).findById(id);
        verify(sucursalClient, times(1)).obtenerSucursal(20L);
        verify(repository, times(1)).save(any(Sala.class));
    }
@Test
public void testEliminarSala_Success() {
    // ARRANGE
    Long id = 1L;

    // ACT
    service.eliminar(id);

    // ASSERT & VERIFY
    verify(repository, times(1)).deleteById(id);
}
}
// Caso hipotético de falla para QA:
// Si el método devuelve una lista nula en lugar de una vacía o arroja un NullPointerException,
// el test fallará indicando: 'expected not null but was null'.
// Desarrollo debe comprobar que la llamada a 'repository.findAll()' no retorne null o esté bien simulada.
