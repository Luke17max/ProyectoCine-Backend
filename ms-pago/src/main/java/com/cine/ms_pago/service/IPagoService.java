package com.cine.ms_pago.service;

import com.cine.ms_pago.dto.PagoDTO;
import java.util.List;

public interface IPagoService {
    List<PagoDTO> listarTodos();
    PagoDTO buscarPorId(Long id);
    PagoDTO buscarPorReserva(Long reservaId);
    PagoDTO guardar(PagoDTO dto);
    PagoDTO actualizar(Long id, PagoDTO dto);
    void eliminar(Long id);
}
