package com.cine.ms_usuarios.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cine.ms_usuarios.dto.UsuarioDTO;
import com.cine.ms_usuarios.model.Usuario;
import com.cine.ms_usuarios.repository.UsuarioRepository;
import com.cine.ms_usuarios.service.IUsuarioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {
    private final UsuarioRepository repository;

    @Override
    public List<UsuarioDTO> listarTodos() {
        log.info("Obteniendo la lista de todos los usuarios");
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public UsuarioDTO buscarPorId(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        return repository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con ID: {}", id);
                    return new RuntimeException("Usuario no encontrado");
                });
    }

    @Override
    public UsuarioDTO guardar(UsuarioDTO dto) {
        log.info("Registrando nuevo usuario con email: {}", dto.getEmail());
        Usuario usuario = mapToEntity(dto);
        return mapToDTO(repository.save(usuario));
    }

    @Override
    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        log.info("Actualizando usuario con ID: {}", id);
        return repository.findById(id).map(u -> {
            u.setNombre(dto.getNombre());
            u.setEmail(dto.getEmail());
            u.setPassword(dto.getPassword());
            u.setRol(dto.getRol());
            return mapToDTO(repository.save(u));
        }).orElseThrow(() -> {
            log.error("Error al actualizar: Usuario no encontrado con ID: {}", id);
            return new RuntimeException("Usuario no encontrado");
        });
    }

    @Override
    public void eliminar(Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        if (!repository.existsById(id)) {
            log.error("Error al eliminar: Usuario no encontrado con ID: {}", id);
            throw new RuntimeException("Usuario no encontrado");
        }
        repository.deleteById(id);
    }

    private UsuarioDTO mapToDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setPassword(u.getPassword());
        dto.setRol(u.getRol());
        return dto;
    }

    private Usuario mapToEntity(UsuarioDTO dto) {
        Usuario u = new Usuario();
        u.setNombre(dto.getNombre());
        u.setEmail(dto.getEmail());
        u.setPassword(dto.getPassword());
        u.setRol(dto.getRol());
        return u;
    }
}
