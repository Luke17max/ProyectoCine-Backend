package com.cine.ms_sucursales.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cine.ms_sucursales.dto.SucursalDTO;
import com.cine.ms_sucursales.service.ISucursalService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(SucursalController.class)
public class SucursalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ISucursalService sucursalService;

    @Test
    public void testListarSucursales_Success() throws Exception {
        // ARRANGE: preparar datos y mocks
        SucursalDTO suc1 = new SucursalDTO();
        suc1.setId(1L);
        suc1.setNombre("Sucursal Central");
        suc1.setDireccion("Av. Providencia 123");
        suc1.setCiudad("Santiago");

        SucursalDTO suc2 = new SucursalDTO();
        suc2.setId(2L);
        suc2.setNombre("Sucursal Norte");
        suc2.setDireccion("Av. Libertad 456");
        suc2.setCiudad("Viña del Mar");

        List<SucursalDTO> lista = Arrays.asList(suc1, suc2);
        when(sucursalService.listarTodas()).thenReturn(lista);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(get("/api/sucursales")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Sucursal Central"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nombre").value("Sucursal Norte"));

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(sucursalService, times(1)).listarTodas();
    }

    @Test
    public void testObtenerSucursalPorId_Success() throws Exception {
        // ARRANGE: preparar datos y mocks
        Long id = 1L;
        SucursalDTO suc = new SucursalDTO();
        suc.setId(id);
        suc.setNombre("Sucursal Central");
        suc.setDireccion("Av. Providencia 123");
        suc.setCiudad("Santiago");

        when(sucursalService.buscarPorId(id)).thenReturn(suc);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(get("/api/sucursales/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Sucursal Central"))
                .andExpect(jsonPath("$.direccion").value("Av. Providencia 123"))
                .andExpect(jsonPath("$.ciudad").value("Santiago"));

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(sucursalService, times(1)).buscarPorId(id);
    }

    @Test
    public void testObtenerPorCiudad_Success() throws Exception {
        // ARRANGE: preparar datos y mocks
        String ciudad = "Santiago";
        SucursalDTO suc = new SucursalDTO();
        suc.setId(1L);
        suc.setNombre("Sucursal Central");
        suc.setDireccion("Av. Providencia 123");
        suc.setCiudad(ciudad);

        List<SucursalDTO> lista = List.of(suc);
        when(sucursalService.buscarPorCiudad(ciudad)).thenReturn(lista);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(get("/api/sucursales/ciudad/Santiago")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Sucursal Central"))
                .andExpect(jsonPath("$[0].ciudad").value("Santiago"));

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(sucursalService, times(1)).buscarPorCiudad(ciudad);
    }

    @Test
    public void testCrearSucursal_Success() throws Exception {
        // ARRANGE: preparar datos y mocks
        SucursalDTO inputDto = new SucursalDTO();
        inputDto.setNombre("Sucursal Poniente");
        inputDto.setDireccion("Av. Pajaritos 789");
        inputDto.setCiudad("Maipú");

        SucursalDTO outputDto = new SucursalDTO();
        outputDto.setId(3L);
        outputDto.setNombre("Sucursal Poniente");
        outputDto.setDireccion("Av. Pajaritos 789");
        outputDto.setCiudad("Maipú");

        when(sucursalService.guardar(any(SucursalDTO.class))).thenReturn(outputDto);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(post("/api/sucursales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nombre").value("Sucursal Poniente"))
                .andExpect(jsonPath("$.direccion").value("Av. Pajaritos 789"))
                .andExpect(jsonPath("$.ciudad").value("Maipú"));

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(sucursalService, times(1)).guardar(any(SucursalDTO.class));
    }

    @Test
    public void testActualizarSucursal_Success() throws Exception {
        // ARRANGE: preparar datos y mocks
        Long id = 1L;
        SucursalDTO inputDto = new SucursalDTO();
        inputDto.setNombre("Sucursal Central Modificada");
        inputDto.setDireccion("Av. Providencia 123");
        inputDto.setCiudad("Santiago");

        SucursalDTO outputDto = new SucursalDTO();
        outputDto.setId(id);
        outputDto.setNombre("Sucursal Central Modificada");
        outputDto.setDireccion("Av. Providencia 123");
        outputDto.setCiudad("Santiago");

        when(sucursalService.actualizar(eq(id), any(SucursalDTO.class))).thenReturn(outputDto);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(put("/api/sucursales/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Sucursal Central Modificada"))
                .andExpect(jsonPath("$.direccion").value("Av. Providencia 123"))
                .andExpect(jsonPath("$.ciudad").value("Santiago"));

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(sucursalService, times(1)).actualizar(eq(id), any(SucursalDTO.class));
    }

    @Test
    public void testEliminarSucursal_Success() throws Exception {
        // ARRANGE: preparar datos y mocks
        Long id = 1L;
        doNothing().when(sucursalService).eliminar(id);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(delete("/api/sucursales/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(sucursalService, times(1)).eliminar(id);
    }
}
// Caso hipotético de falla para QA:
// Si el endpoint '/api/sucursales' cambia de ruta, el test fallará con HTTP 404.
// Si hay un error de serialización o base de datos en el controlador, responderá HTTP 500.
// Desarrollo debe verificar la configuración de ruta y la simulación del servicio.
