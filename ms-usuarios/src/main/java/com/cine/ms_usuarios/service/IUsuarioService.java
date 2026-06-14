package com.cine.ms_usuarios.service;

import java.util.List;

import com.cine.ms_usuarios.dto.UsuarioDTO;

public interface IUsuarioService {
    List<UsuarioDTO> listarTodos();
    UsuarioDTO buscarPorId(Long id);
    UsuarioDTO guardar(UsuarioDTO dto);
    UsuarioDTO actualizar(Long id, UsuarioDTO dto);
    void eliminar(Long id);
}
