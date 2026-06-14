package com.cine.ms_pago_confiteria.service;

import java.util.List;

import com.cine.ms_pago_confiteria.dto.PagoConfiteriaDTO;

public interface IPagoConfiteriaService {
    List<PagoConfiteriaDTO> listarTodos();
    PagoConfiteriaDTO buscarPorId(Long id);
    List<PagoConfiteriaDTO> buscarPorUsuario(Long usuarioId);
    PagoConfiteriaDTO guardar(PagoConfiteriaDTO dto);
    PagoConfiteriaDTO actualizar(Long id, PagoConfiteriaDTO dto);
    void eliminar(Long id);

}
