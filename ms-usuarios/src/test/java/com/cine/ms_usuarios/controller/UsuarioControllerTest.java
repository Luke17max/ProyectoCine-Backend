package com.cine.ms_usuarios.controller;

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

import com.cine.ms_usuarios.dto.UsuarioDTO;
import com.cine.ms_usuarios.service.IUsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IUsuarioService usuarioService;

    @Test
    public void testListarUsuarios_Success() throws Exception {
        // ARRANGE: preparar datos y mocks
        UsuarioDTO user1 = new UsuarioDTO();
        user1.setId(1L);
        user1.setNombre("Juan Perez");
        user1.setEmail("juan.perez@example.com");
        user1.setRol("CLIENTE");

        UsuarioDTO user2 = new UsuarioDTO();
        user2.setId(2L);
        user2.setNombre("Maria Lopez");
        user2.setEmail("maria.lopez@example.com");
        user2.setRol("ADMINISTRADOR");

        List<UsuarioDTO> listaUsuarios = Arrays.asList(user1, user2);
        when(usuarioService.listarTodos()).thenReturn(listaUsuarios);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(get("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Juan Perez"))
                .andExpect(jsonPath("$[0].email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nombre").value("Maria Lopez"));

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(usuarioService, times(1)).listarTodos();
    }

    @Test
    public void testBuscarPorId_Success() throws Exception {
        // ARRANGE: preparar datos y mocks
        UsuarioDTO user = new UsuarioDTO();
        user.setId(1L);
        user.setNombre("Juan Perez");
        user.setEmail("juan.perez@example.com");
        user.setRol("CLIENTE");

        when(usuarioService.buscarPorId(1L)).thenReturn(user);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(get("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Perez"))
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(usuarioService, times(1)).buscarPorId(1L);
    }

    @Test
    public void testCrearUsuario_Success() throws Exception {
        // ARRANGE: preparar datos y mocks
        UsuarioDTO inputDto = new UsuarioDTO();
        inputDto.setNombre("Carlos Gomez");
        inputDto.setEmail("carlos.gomez@example.com");
        inputDto.setPassword("secreto123");
        inputDto.setRol("CLIENTE");

        UsuarioDTO outputDto = new UsuarioDTO();
        outputDto.setId(3L);
        outputDto.setNombre("Carlos Gomez");
        outputDto.setEmail("carlos.gomez@example.com");
        outputDto.setRol("CLIENTE");

        when(usuarioService.guardar(any(UsuarioDTO.class))).thenReturn(outputDto);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nombre").value("Carlos Gomez"))
                .andExpect(jsonPath("$.email").value("carlos.gomez@example.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(usuarioService, times(1)).guardar(any(UsuarioDTO.class));
    }

    @Test
    public void testActualizarUsuario_Success() throws Exception {
        // ARRANGE: preparar datos y mocks
        UsuarioDTO inputDto = new UsuarioDTO();
        inputDto.setNombre("Carlos Gomez Modificado");
        inputDto.setEmail("carlos.gomez@example.com");
        inputDto.setPassword("nuevocontrasena");
        inputDto.setRol("CLIENTE");

        UsuarioDTO outputDto = new UsuarioDTO();
        outputDto.setId(1L);
        outputDto.setNombre("Carlos Gomez Modificado");
        outputDto.setEmail("carlos.gomez@example.com");
        outputDto.setRol("CLIENTE");

        when(usuarioService.actualizar(eq(1L), any(UsuarioDTO.class))).thenReturn(outputDto);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(put("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Carlos Gomez Modificado"))
                .andExpect(jsonPath("$.email").value("carlos.gomez@example.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(usuarioService, times(1)).actualizar(eq(1L), any(UsuarioDTO.class));
    }

    @Test
    public void testEliminarUsuario_Success() throws Exception {
        // ARRANGE: preparar datos y mocks
        doNothing().when(usuarioService).eliminar(1L);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(delete("/api/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(usuarioService, times(1)).eliminar(1L);
    }
}
// Caso hipotético de falla para QA:
// Si el endpoint '/api/usuarios' cambia su ruta a '/api/v1/usuarios' o si el controlador
// devuelve un estado HTTP 500 (Internal Server Error) por una mala inyección del servicio,
// MockMvc fallará indicando: 'Range for response status value 200 expected but was 500' 
// o 'Range for response status value 200 expected but was 404'.
// Desarrollo debe verificar la configuración de mapeo del controlador y la correcta definición de los mocks.
