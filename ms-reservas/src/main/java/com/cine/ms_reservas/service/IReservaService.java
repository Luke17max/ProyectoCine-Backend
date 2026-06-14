package com.cine.ms_reservas.service;

import java.util.List;

import com.cine.ms_reservas.dto.ReservaDTO;

public interface IReservaService {
    List<ReservaDTO> listarTodas();
    ReservaDTO buscarPorId(Long id);
    List<ReservaDTO> buscarPorUsuario(Long usuarioId);
    ReservaDTO guardar(ReservaDTO dto);
    ReservaDTO actualizarEstado(Long id, String nuevoEstado);
    void eliminar(Long id);

}
