package com.cine.ms_salas.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cine.ms_salas.clients.SucursalClient;
import com.cine.ms_salas.dto.SalaDTO;
import com.cine.ms_salas.model.Sala;
import com.cine.ms_salas.repository.SalaRepository;
import com.cine.ms_salas.service.ISalaService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalaServiceImpl implements ISalaService {
    private final SalaRepository repository;
    private final SucursalClient sucursalClient; // Inyectamos el cliente Feign

    @Override
    public List<SalaDTO> listarTodas() {
        log.info("Listando todas las salas");
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public SalaDTO buscarPorId(Long id) {
        return repository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));
    }

    @Override
    public List<SalaDTO> buscarPorSucursal(Long sucursalId) {
        log.info("Buscando salas de la sucursal ID: {}", sucursalId);
        return repository.findBySucursalId(sucursalId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public SalaDTO guardar(SalaDTO dto) {
        log.info("Validando existencia de sucursal ID: {}", dto.getSucursalId());
        validarSucursal(dto.getSucursalId()); // Llama a Feign antes de guardar
        
        Sala sala = mapToEntity(dto);
        return mapToDTO(repository.save(sala));
    }

    @Override
    public SalaDTO actualizar(Long id, SalaDTO dto) {
        Sala sala = repository.findById(id).orElseThrow(() -> new RuntimeException("Sala no encontrada"));
        
        if (!sala.getSucursalId().equals(dto.getSucursalId())) {
            validarSucursal(dto.getSucursalId());
        }

        sala.setNombre(dto.getNombre());
        sala.setCapacidad(dto.getCapacidad());
        sala.setSucursalId(dto.getSucursalId());
        return mapToDTO(repository.save(sala));
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    // Método privado para consumir ms-sucursales
    private void validarSucursal(Long sucursalId) {
        try {
            sucursalClient.obtenerSucursal(sucursalId);
            log.info("Sucursal validada exitosamente a través de Eureka/Feign");
        } catch (FeignException.NotFound e) {
            log.error("La sucursal ID {} no existe en ms-sucursales", sucursalId);
            throw new RuntimeException("La sucursal asignada no existe");
        } catch (FeignException e) {
            log.error("Error de comunicación con ms-sucursales: {}", e.getMessage());
            throw new RuntimeException("Error al validar la sucursal. Intente más tarde.");
        }
    }

    private SalaDTO mapToDTO(Sala s) {
        SalaDTO dto = new SalaDTO();
        dto.setId(s.getId()); dto.setNombre(s.getNombre());
        dto.setCapacidad(s.getCapacidad()); dto.setSucursalId(s.getSucursalId());
        return dto;
    }

    private Sala mapToEntity(SalaDTO dto) {
        Sala s = new Sala();
        s.setNombre(dto.getNombre()); s.setCapacidad(dto.getCapacidad());
        s.setSucursalId(dto.getSucursalId());
        return s;
    }

}
