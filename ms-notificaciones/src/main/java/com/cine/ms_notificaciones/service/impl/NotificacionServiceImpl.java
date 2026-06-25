package com.cine.ms_notificaciones.service.impl;

import com.cine.ms_notificaciones.client.PagoClient;
import com.cine.ms_notificaciones.client.ReservaClient;
import com.cine.ms_notificaciones.dto.NotificacionDTO;
import com.cine.ms_notificaciones.model.Notificacion;
import com.cine.ms_notificaciones.repository.NotificacionRepository;
import com.cine.ms_notificaciones.service.INotificacionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements INotificacionService {

    private final NotificacionRepository repository;
    private final ReservaClient reservaClient;
    private final PagoClient pagoClient;

    @Override
    public List<NotificacionDTO> listarTodas() {
        log.info("Capa Servicio: Listando todas las notificaciones");
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public NotificacionDTO buscarPorId(Long id) {
        log.info("Capa Servicio: Buscando notificación ID: {}", id);
        return repository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    @Override
    public List<NotificacionDTO> buscarPorReserva(Long reservaId) {
        log.info("Capa Servicio: Buscando notificaciones para la reserva ID: {}", reservaId);
        return repository.findByReservaId(reservaId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public NotificacionDTO guardar(NotificacionDTO dto) {
        log.info("Capa Servicio: Guardando nueva notificación, validando dependencias cruzadas");
        
        validarReserva(dto.getReservaId());
        
        if (dto.getPagoId() != null) {
            validarPago(dto.getPagoId());
        }

        Notificacion notificacion = mapToEntity(dto);
        return mapToDTO(repository.save(notificacion));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Capa Servicio: Eliminando notificación ID: {}", id);
        repository.deleteById(id);
    }

    private void validarReserva(Long id) {
        try {
            reservaClient.obtenerReserva(id);
            log.info("Validación exitosa: Reserva ID {} existe", id);
        } catch (FeignException.NotFound e) {
            log.error("Fallo de integridad: Reserva ID {} no encontrada", id);
            throw new RuntimeException("Error: La reserva con ID " + id + " no existe.");
        }
    }

    private void validarPago(Long id) {
        try {
            pagoClient.obtenerPago(id);
            log.info("Validación exitosa: Pago ID {} existe", id);
        } catch (FeignException.NotFound e) {
            log.error("Fallo de integridad: Pago ID {} no encontrado", id);
            throw new RuntimeException("Error: El pago con ID " + id + " no existe.");
        }
    }

    private NotificacionDTO mapToDTO(Notificacion n) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(n.getId());
        dto.setReservaId(n.getReservaId());
        dto.setPagoId(n.getPagoId());
        dto.setTipo(n.getTipo());
        dto.setMensaje(n.getMensaje());
        dto.setFechaCreacion(n.getFechaCreacion());
        return dto;
    }

    private Notificacion mapToEntity(NotificacionDTO dto) {
        Notificacion n = new Notificacion();
        n.setReservaId(dto.getReservaId());
        n.setPagoId(dto.getPagoId());
        n.setTipo(dto.getTipo());
        n.setMensaje(dto.getMensaje());
        return n;
    }
}
