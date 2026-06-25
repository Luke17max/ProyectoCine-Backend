package com.cine.ms_sucursales.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cine.ms_sucursales.dto.SucursalDTO;
import com.cine.ms_sucursales.model.Sucursal;
import com.cine.ms_sucursales.repository.SucursalRepository;
import com.cine.ms_sucursales.service.impl.SucursalServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SucursalServiceTest {

    @Mock
    private SucursalRepository repository;

    @InjectMocks
    private SucursalServiceImpl service;

    @Test
    public void testListarTodas_Success() {
        Sucursal s1 = new Sucursal();
        s1.setId(1L);
        s1.setNombre("Sucursal A");
        s1.setDireccion("Calle 1");
        s1.setCiudad("CiudadX");

        Sucursal s2 = new Sucursal();
        s2.setId(2L);
        s2.setNombre("Sucursal B");
        s2.setDireccion("Calle 2");
        s2.setCiudad("CiudadY");

        when(repository.findAll()).thenReturn(Arrays.asList(s1, s2));

        List<SucursalDTO> result = service.listarTodas();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Sucursal A", result.get(0).getNombre());
        assertEquals("Sucursal B", result.get(1).getNombre());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testBuscarPorId_Success() {
        Long id = 1L;
        Sucursal s = new Sucursal();
        s.setId(id);
        s.setNombre("Sucursal A");
        s.setDireccion("Calle 1");
        s.setCiudad("CiudadX");

        when(repository.findById(id)).thenReturn(java.util.Optional.of(s));

        SucursalDTO result = service.buscarPorId(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Sucursal A", result.getNombre());
        verify(repository, times(1)).findById(id);
    }

    @Test
    public void testBuscarPorCiudad_Success() {
        String ciudad = "CiudadX";
        Sucursal s = new Sucursal();
        s.setId(1L);
        s.setNombre("Sucursal A");
        s.setDireccion("Calle 1");
        s.setCiudad(ciudad);

        when(repository.findByCiudadIgnoreCase(ciudad)).thenReturn(Arrays.asList(s));

        List<SucursalDTO> result = service.buscarPorCiudad(ciudad);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sucursal A", result.get(0).getNombre());
        verify(repository, times(1)).findByCiudadIgnoreCase(ciudad);
    }

    @Test
    public void testGuardar_Success() {
        SucursalDTO input = new SucursalDTO();
        input.setNombre("Nueva Sucursal");
        input.setDireccion("Calle 3");
        input.setCiudad("CiudadZ");

        Sucursal saved = new Sucursal();
        saved.setId(1L);
        saved.setNombre("Nueva Sucursal");
        saved.setDireccion("Calle 3");
        saved.setCiudad("CiudadZ");

        when(repository.save(any(Sucursal.class))).thenReturn(saved);

        SucursalDTO result = service.guardar(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Nueva Sucursal", result.getNombre());
        verify(repository, times(1)).save(any(Sucursal.class));
    }

    @Test
    public void testActualizar_Success() {
        Long id = 1L;
        Sucursal existing = new Sucursal();
        existing.setId(id);
        existing.setNombre("Antigua");
        existing.setDireccion("Vieja");
        existing.setCiudad("CiudadA");

        SucursalDTO updateDto = new SucursalDTO();
        updateDto.setNombre("Nueva");
        updateDto.setDireccion("Nueva Dirección");
        updateDto.setCiudad("CiudadB");

        Sucursal updated = new Sucursal();
        updated.setId(id);
        updated.setNombre("Nueva");
        updated.setDireccion("Nueva Dirección");
        updated.setCiudad("CiudadB");

        when(repository.findById(id)).thenReturn(java.util.Optional.of(existing));
        when(repository.save(any(Sucursal.class))).thenReturn(updated);

        SucursalDTO result = service.actualizar(id, updateDto);

        assertNotNull(result);
        assertEquals("Nueva", result.getNombre());
        assertEquals("Nueva Dirección", result.getDireccion());
        assertEquals("CiudadB", result.getCiudad());
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(Sucursal.class));
    }

    @Test
    public void testEliminar_Success() {
        Long id = 1L;
        service.eliminar(id);
        verify(repository, times(1)).deleteById(id);
    }
}
