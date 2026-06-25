package com.cine.ms_peliculas.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cine.ms_peliculas.dto.PeliculaDTO;
import com.cine.ms_peliculas.model.Pelicula;
import com.cine.ms_peliculas.repository.PeliculaRepository;
import com.cine.ms_peliculas.service.impl.PeliculaServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PeliculaServiceTest {

    @Mock
    private PeliculaRepository repository;

    @InjectMocks
    private PeliculaServiceImpl service;

    @Test
    public void testListarTodas_Success() {
        Pelicula p1 = new Pelicula();
        p1.setId(1L);
        p1.setTitulo("Inception");
        p1.setGenero("Sci-Fi");
        p1.setDuracion(148);
        p1.setClasificacion("PG-13");

        Pelicula p2 = new Pelicula();
        p2.setId(2L);
        p2.setTitulo("Titanic");
        p2.setGenero("Drama");
        p2.setDuracion(195);
        p2.setClasificacion("PG-13");

        when(repository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<PeliculaDTO> result = service.listarTodas();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Inception", result.get(0).getTitulo());
        assertEquals("Titanic", result.get(1).getTitulo());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testBuscarPorId_Success() {
        Long id = 1L;
        Pelicula p = new Pelicula();
        p.setId(id);
        p.setTitulo("Inception");
        p.setGenero("Sci-Fi");
        p.setDuracion(148);
        p.setClasificacion("PG-13");

        when(repository.findById(id)).thenReturn(java.util.Optional.of(p));

        PeliculaDTO result = service.buscarPorId(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Inception", result.getTitulo());
        verify(repository, times(1)).findById(id);
    }

    @Test
    public void testGuardarPelicula_Success() {
        PeliculaDTO input = new PeliculaDTO();
        input.setTitulo("Interstellar");
        input.setGenero("Sci-Fi");
        input.setDuracion(169);
        input.setClasificacion("PG-13");

        Pelicula saved = new Pelicula();
        saved.setId(1L);
        saved.setTitulo("Interstellar");
        saved.setGenero("Sci-Fi");
        saved.setDuracion(169);
        saved.setClasificacion("PG-13");

        when(repository.save(any(Pelicula.class))).thenReturn(saved);

        PeliculaDTO result = service.guardar(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Interstellar", result.getTitulo());
        verify(repository, times(1)).save(any(Pelicula.class));
    }

    @Test
    public void testActualizarPelicula_Success() {
        Long id = 1L;
        Pelicula existing = new Pelicula();
        existing.setId(id);
        existing.setTitulo("Old Title");
        existing.setGenero("Drama");
        existing.setDuracion(120);
        existing.setClasificacion("PG");

        PeliculaDTO updateDto = new PeliculaDTO();
        updateDto.setTitulo("New Title");
        updateDto.setGenero("Action");
        updateDto.setDuracion(130);
        updateDto.setClasificacion("R");

        Pelicula updated = new Pelicula();
        updated.setId(id);
        updated.setTitulo("New Title");
        updated.setGenero("Action");
        updated.setDuracion(130);
        updated.setClasificacion("R");

        when(repository.findById(id)).thenReturn(java.util.Optional.of(existing));
        when(repository.save(any(Pelicula.class))).thenReturn(updated);

        PeliculaDTO result = service.actualizar(id, updateDto);

        assertNotNull(result);
        assertEquals("New Title", result.getTitulo());
        assertEquals("Action", result.getGenero());
        assertEquals(130, result.getDuracion());
        assertEquals("R", result.getClasificacion());
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(Pelicula.class));
    }

    @Test
    public void testEliminarPelicula_Success() {
        Long id = 1L;
        service.eliminar(id);
        verify(repository, times(1)).deleteById(id);
    }
}
