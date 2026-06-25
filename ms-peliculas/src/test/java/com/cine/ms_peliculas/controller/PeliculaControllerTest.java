package com.cine.ms_peliculas.controller;

import com.cine.ms_peliculas.dto.PeliculaDTO;
import com.cine.ms_peliculas.service.IPeliculaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Test unitario para PeliculaController.
 * * En este test NO se conecta a MySQL, ni usa Eureka o API Gateway.
 * Solo se valida el comportamiento de la capa Controller usando MockMvc.
 */
@WebMvcTest(PeliculaController.class)

public class PeliculaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IPeliculaService peliculaService;

    // --- TEST 1: LISTAR ---
    @Test
    void listar_deberiaRetornarStatus200YListaDePeliculas() throws Exception {
        PeliculaDTO pelicula = new PeliculaDTO();
        pelicula.setId(1L);
        pelicula.setTitulo("Inception");
        pelicula.setGenero("Ciencia Ficción");
        pelicula.setDuracion(148);
        pelicula.setClasificacion("TE+7");

        when(peliculaService.listarTodas()).thenReturn(List.of(pelicula));

        mockMvc.perform(get("/api/peliculas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].titulo").value("Inception"));

        verify(peliculaService, times(1)).listarTodas();
    }

    // --- TEST 2: OBTENER POR ID (NUEVO) ---
    @Test
    void buscarPorId_cuandoExiste_deberiaRetornarStatus200YPelicula() throws Exception {
        /*
         * ARRANGE:
         * Definimos que buscaremos el ID 1L y preparamos el DTO simulado 
         * que el Service debería retornar para ese ID específico.
         */
        Long idBuscado = 1L;
        PeliculaDTO peliculaSimulada = new PeliculaDTO();
        peliculaSimulada.setId(idBuscado);
        peliculaSimulada.setTitulo("Interstellar");
        peliculaSimulada.setGenero("Ciencia Ficción");
        peliculaSimulada.setDuracion(169);
        peliculaSimulada.setClasificacion("TE");

        // Configuramos el comportamiento del Mock
        when(peliculaService.buscarPorId(idBuscado)).thenReturn(peliculaSimulada);

        /*
         * ACT:
         * Simulamos la petición HTTP GET al endpoint dinámico /api/peliculas/1
         */
        mockMvc.perform(get("/api/peliculas/{id}", idBuscado))

        /*
         * ASSERT:
         * Verificamos que el servidor responda con un HTTP 200 OK
         * y que las propiedades del JSON coincidan con nuestro objeto simulado.
         */
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idBuscado))
                .andExpect(jsonPath("$.titulo").value("Interstellar"))
                .andExpect(jsonPath("$.genero").value("Ciencia Ficción"))
                .andExpect(jsonPath("$.duracion").value(169))
                .andExpect(jsonPath("$.clasificacion").value("TE"));

        /*
         * VERIFY:
         * Aseguramos que el controlador llamó al Service pasando exactamente el ID correcto.
         */
        verify(peliculaService, times(1)).buscarPorId(idBuscado);
    }

    // --- TEST 3: CREAR PELÍCULA (POST) NUEVO ---
    @Test
    void crear_conDatosValidos_deberiaRetornarStatus201YPeliculaCreada() throws Exception {
        /*
         * ARRANGE:
         * 1. Creamos el DTO "nuevo" (sin ID, simulando lo que digita el usuario).
         * 2. Creamos el DTO "guardado" (con ID, simulando lo que responde la BD).
         */
        PeliculaDTO nuevaPelicula = new PeliculaDTO();
        nuevaPelicula.setTitulo("The Matrix");
        nuevaPelicula.setGenero("Acción");
        nuevaPelicula.setDuracion(136);
        nuevaPelicula.setClasificacion("TE");

        PeliculaDTO peliculaGuardada = new PeliculaDTO();
        peliculaGuardada.setId(10L);
        peliculaGuardada.setTitulo("The Matrix");
        peliculaGuardada.setGenero("Acción");
        peliculaGuardada.setDuracion(136);
        peliculaGuardada.setClasificacion("TE");

        // Simulamos que al recibir CUALQUIER objeto PeliculaDTO, el service retorne la película guardada
        when(peliculaService.guardar(any(PeliculaDTO.class))).thenReturn(peliculaGuardada);

        /*
         * ACT:
         * Hacemos un POST a /api/peliculas enviando el JSON en el cuerpo de la petición.
         */
        mockMvc.perform(post("/api/peliculas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevaPelicula))) // Convierte el DTO a String JSON

        /*
         * ASSERT:
         * Verificamos que el código HTTP sea 201 (CREATED) y que retorne el ID asignado.
         */
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.titulo").value("The Matrix"));

        /*
         * VERIFY:
         * Confirmamos que el controller le haya entregado los datos al Service exactamente 1 vez.
         */
        verify(peliculaService, times(1)).guardar(any(PeliculaDTO.class));
    }

    // --- TEST 4: ACTUALIZAR PELÍCULA (PUT) ---
    @Test
    void actualizar_conDatosValidos_deberiaRetornarStatus200YPeliculaActualizada() throws Exception {
        // ARRANGE: preparar datos y mocks
        Long id = 1L;
        PeliculaDTO inputDto = new PeliculaDTO();
        inputDto.setTitulo("Inception Modificado");
        inputDto.setGenero("Ciencia Ficción");
        inputDto.setDuracion(150);
        inputDto.setClasificacion("TE+7");

        PeliculaDTO outputDto = new PeliculaDTO();
        outputDto.setId(id);
        outputDto.setTitulo("Inception Modificado");
        outputDto.setGenero("Ciencia Ficción");
        outputDto.setDuracion(150);
        outputDto.setClasificacion("TE+7");

        when(peliculaService.actualizar(eq(id), any(PeliculaDTO.class))).thenReturn(outputDto);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(put("/api/peliculas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Inception Modificado"))
                .andExpect(jsonPath("$.duracion").value(150))
                .andExpect(jsonPath("$.clasificacion").value("TE+7"));

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(peliculaService, times(1)).actualizar(eq(id), any(PeliculaDTO.class));
    }

    // --- TEST 5: ELIMINAR PELÍCULA (DELETE) ---
    @Test
    void eliminar_deberiaRetornarStatus204() throws Exception {
        // ARRANGE: preparar datos y mocks
        Long id = 1L;
        doNothing().when(peliculaService).eliminar(id);

        // ACT: ejecutar método o endpoint & ASSERT: verificar resultado esperado
        mockMvc.perform(delete("/api/peliculas/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // VERIFY: comprobar llamadas al mock si corresponde
        verify(peliculaService, times(1)).eliminar(id);
    }
    
    /*
     * 🚨 CASO HIPOTÉTICO DE FALLA PARA REPORTAR A QA:
     *
     * Si este test fallara con un "400 Bad Request", se documentaría así:
     * * Reporte QA: "Al enviar una petición POST válida a /api/peliculas, el sistema rechaza
     * la creación retornando 400 Bad Request en lugar de 201 Created. 
     * El equipo de desarrollo debe revisar las anotaciones de validación (@Valid) 
     * en el Controller o dentro del objeto PeliculaDTO".
     */
    
}
