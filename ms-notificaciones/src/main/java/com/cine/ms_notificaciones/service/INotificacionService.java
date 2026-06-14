package com.cine.ms_notificaciones.service;

import com.cine.ms_notificaciones.dto.NotificacionDTO;
import java.util.List;

public interface INotificacionService {
    List<NotificacionDTO> listarTodas();
    NotificacionDTO buscarPorId(Long id);
    List<NotificacionDTO> buscarPorReserva(Long reservaId);
    NotificacionDTO guardar(NotificacionDTO dto);
    void eliminar(Long id);
}
