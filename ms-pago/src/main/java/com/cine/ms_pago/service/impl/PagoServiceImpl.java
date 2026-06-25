package com.cine.ms_pago.service.impl;

import com.cine.ms_pago.client.ReservaClient;
import com.cine.ms_pago.dto.PagoDTO;
import com.cine.ms_pago.model.Pago;
import com.cine.ms_pago.repository.PagoRepository;
import com.cine.ms_pago.service.IPagoService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements IPagoService {

    private final PagoRepository repository;
    private final ReservaClient reservaClient;

    @Override
    public List<PagoDTO> listarTodos() {
        log.info("Capa Servicio: Listando todos los pagos");
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public PagoDTO buscarPorId(Long id) {
        log.info("Capa Servicio: Buscando pago ID: {}", id);
        return repository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }

    @Override
    public PagoDTO buscarPorReserva(Long reservaId) {
        log.info("Capa Servicio: Buscando pago para reserva ID: {}", reservaId);
        return repository.findByReservaId(reservaId).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("No existe pago para la reserva indicada"));
    }

    @Override
    public PagoDTO guardar(PagoDTO dto) {
        log.info("Capa Servicio: Registrando nuevo pago para reserva {}", dto.getReservaId());
        
        // Evitar duplicidad de pagos para una misma reserva (1:1 constraint)
        if (repository.findByReservaId(dto.getReservaId()).isPresent()) {
            throw new RuntimeException("Ya existe un pago registrado para la reserva ID: " + dto.getReservaId());
        }
        
        validarReserva(dto.getReservaId());

        Pago pago = mapToEntity(dto);
        return mapToDTO(repository.save(pago));
    }

    @Override
    public PagoDTO actualizar(Long id, PagoDTO dto) {
        log.info("Capa Servicio: Actualizando pago ID: {}", id);
        Pago pago = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        if (!pago.getReservaId().equals(dto.getReservaId())) {
            validarReserva(dto.getReservaId());
        }

        pago.setReservaId(dto.getReservaId());
        pago.setMontoTotal(dto.getMontoTotal());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstado(dto.getEstado());

        return mapToDTO(repository.save(pago));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Capa Servicio: Eliminando pago ID: {}", id);
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

    private PagoDTO mapToDTO(Pago p) {
        PagoDTO dto = new PagoDTO();
        dto.setId(p.getId());
        dto.setReservaId(p.getReservaId());
        dto.setMontoTotal(p.getMontoTotal());
        dto.setMetodoPago(p.getMetodoPago());
        dto.setEstado(p.getEstado());
        dto.setFechaPago(p.getFechaPago());
        return dto;
    }

    private Pago mapToEntity(PagoDTO dto) {
        Pago p = new Pago();
        p.setReservaId(dto.getReservaId());
        p.setMontoTotal(dto.getMontoTotal());
        p.setMetodoPago(dto.getMetodoPago());
        p.setEstado(dto.getEstado());
        return p;
    }
}
