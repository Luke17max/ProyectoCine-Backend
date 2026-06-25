package com.cine.ms_reservas.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cine.ms_reservas.client.FuncionClient;
import com.cine.ms_reservas.dto.ReservaDTO;
import com.cine.ms_reservas.model.Reserva;
import com.cine.ms_reservas.repository.ReservaRepository;
import com.cine.ms_reservas.service.IReservaService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements IReservaService{
    private final ReservaRepository repository;
    private final FuncionClient funcionClient;

    @Override
    public List<ReservaDTO> listarTodas() {
        log.info("Capa Servicio: Listando todas las reservas");
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public ReservaDTO buscarPorId(Long id) {
        log.info("Capa Servicio: Buscando reserva ID: {}", id);
        return repository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
    }

    @Override
    public List<ReservaDTO> buscarPorUsuario(Long usuarioId) {
        log.info("Capa Servicio: Buscando reservas del usuario ID: {}", usuarioId);
        return repository.findByUsuarioId(usuarioId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public ReservaDTO guardar(ReservaDTO dto) {
        log.info("Capa Servicio: Iniciando proceso de reserva para función ID: {}", dto.getFuncionId());
        validarFuncion(dto.getFuncionId());
        
        Reserva reserva = mapToEntity(dto);
        // Regla de negocio: Toda reserva nueva nace en estado PENDIENTE
        reserva.setEstado("PENDIENTE");
        return mapToDTO(repository.save(reserva));
    }

    @Override
    public ReservaDTO actualizarEstado(Long id, String nuevoEstado) {
        log.info("Capa Servicio: Actualizando estado de la reserva ID: {} a {}", id, nuevoEstado);
        Reserva reserva = repository.findById(id).orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reserva.setEstado(nuevoEstado.toUpperCase());
        return mapToDTO(repository.save(reserva));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Capa Servicio: Eliminando reserva ID: {}", id);
        repository.deleteById(id);
    }

    private void validarFuncion(Long funcionId) {
        try {
            funcionClient.obtenerFuncion(funcionId);
            log.info("Validación exitosa: La función ID {} existe en ms-funciones", funcionId);
        } catch (FeignException.NotFound e) {
            log.error("Fallo de integridad: Función ID {} no existe", funcionId);
            throw new RuntimeException("Error: La función solicitada no existe.");
        } catch (FeignException e) {
            log.error("Error de comunicación con ms-funciones: {}", e.getMessage());
            throw new RuntimeException("Error de comunicación al validar la función.");
        }
    }

    private ReservaDTO mapToDTO(Reserva r) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(r.getId()); dto.setUsuarioId(r.getUsuarioId());
        dto.setFuncionId(r.getFuncionId()); dto.setCantidadAsientos(r.getCantidadAsientos());
        dto.setEstado(r.getEstado());
        return dto;
    }

    private Reserva mapToEntity(ReservaDTO dto) {
        Reserva r = new Reserva();
        r.setUsuarioId(dto.getUsuarioId()); r.setFuncionId(dto.getFuncionId());
        r.setCantidadAsientos(dto.getCantidadAsientos());
        return r;
    }

}
