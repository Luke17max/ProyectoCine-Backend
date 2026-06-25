package com.cine.ms_usuarios.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cine.ms_usuarios.dto.UsuarioDTO;
import com.cine.ms_usuarios.model.Usuario;
import com.cine.ms_usuarios.repository.UsuarioRepository;
import com.cine.ms_usuarios.service.impl.UsuarioServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioServiceImpl service;

    @Test
    public void testListarTodos_Success() {
        Usuario u1 = new Usuario();
        u1.setId(1L);
        u1.setNombre("Juan");
        u1.setEmail("juan@example.com");
        u1.setPassword("pass");
        u1.setRol("USER");

        Usuario u2 = new Usuario();
        u2.setId(2L);
        u2.setNombre("Maria");
        u2.setEmail("maria@example.com");
        u2.setPassword("pass2");
        u2.setRol("ADMIN");

        when(repository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<UsuarioDTO> result = service.listarTodos();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Juan", result.get(0).getNombre());
        assertEquals("Maria", result.get(1).getNombre());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testBuscarPorId_Success() {
        Long id = 1L;
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre("Juan");
        u.setEmail("juan@example.com");
        u.setPassword("pass");
        u.setRol("USER");

        when(repository.findById(id)).thenReturn(java.util.Optional.of(u));

        UsuarioDTO result = service.buscarPorId(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Juan", result.getNombre());
        verify(repository, times(1)).findById(id);
    }

    @Test
    public void testGuardar_Success() {
        UsuarioDTO input = new UsuarioDTO();
        input.setNombre("Carlos");
        input.setEmail("carlos@example.com");
        input.setPassword("secret");
        input.setRol("USER");

        Usuario saved = new Usuario();
        saved.setId(1L);
        saved.setNombre("Carlos");
        saved.setEmail("carlos@example.com");
        saved.setPassword("secret");
        saved.setRol("USER");

        when(repository.save(any(Usuario.class))).thenReturn(saved);

        UsuarioDTO result = service.guardar(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Carlos", result.getNombre());
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    public void testActualizar_Success() {
        Long id = 1L;
        Usuario existing = new Usuario();
        existing.setId(id);
        existing.setNombre("Old");
        existing.setEmail("old@example.com");
        existing.setPassword("oldpass");
        existing.setRol("USER");

        UsuarioDTO updateDto = new UsuarioDTO();
        updateDto.setNombre("New");
        updateDto.setEmail("new@example.com");
        updateDto.setPassword("newpass");
        updateDto.setRol("ADMIN");

        Usuario updated = new Usuario();
        updated.setId(id);
        updated.setNombre("New");
        updated.setEmail("new@example.com");
        updated.setPassword("newpass");
        updated.setRol("ADMIN");

        when(repository.findById(id)).thenReturn(java.util.Optional.of(existing));
        when(repository.save(any(Usuario.class))).thenReturn(updated);

        UsuarioDTO result = service.actualizar(id, updateDto);

        assertNotNull(result);
        assertEquals("New", result.getNombre());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("ADMIN", result.getRol());
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    public void testEliminar_Success() {
        Long id = 1L;
        service.eliminar(id);
        verify(repository, times(1)).deleteById(id);
    }
}
