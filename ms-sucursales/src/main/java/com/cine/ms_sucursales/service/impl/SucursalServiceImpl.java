package com.cine.ms_sucursales.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cine.ms_sucursales.dto.SucursalDTO;
import com.cine.ms_sucursales.model.Sucursal;
import com.cine.ms_sucursales.repository.SucursalRepository;
import com.cine.ms_sucursales.service.ISucursalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements ISucursalService {
    private final SucursalRepository repository;

    @Override
    public List<SucursalDTO> listarTodas() {
        log.info("Capa Servicio: Listando todas las sucursales");
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public SucursalDTO buscarPorId(Long id) {
        log.info("Capa Servicio: Buscando sucursal ID: {}", id);
        return repository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
    }

    @Override
    public List<SucursalDTO> buscarPorCiudad(String ciudad) {
        log.info("Capa Servicio: Buscando sucursales en ciudad: {}", ciudad);
        return repository.findByCiudadIgnoreCase(ciudad).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public SucursalDTO guardar(SucursalDTO dto) {
        log.info("Capa Servicio: Guardando nueva sucursal '{}'", dto.getNombre());
        Sucursal sucursal = mapToEntity(dto);
        return mapToDTO(repository.save(sucursal));
    }

    @Override
    public SucursalDTO actualizar(Long id, SucursalDTO dto) {
        log.info("Capa Servicio: Actualizando sucursal ID: {}", id);
        Sucursal sucursal = repository.findById(id).orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setCiudad(dto.getCiudad());
        return mapToDTO(repository.save(sucursal));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Capa Servicio: Eliminando sucursal ID: {}", id);
        repository.deleteById(id);
    }

    private SucursalDTO mapToDTO(Sucursal s) {
        SucursalDTO dto = new SucursalDTO();
        dto.setId(s.getId()); dto.setNombre(s.getNombre());
        dto.setDireccion(s.getDireccion()); dto.setCiudad(s.getCiudad());
        return dto;
    }

    private Sucursal mapToEntity(SucursalDTO dto) {
        Sucursal s = new Sucursal();
        s.setNombre(dto.getNombre()); s.setDireccion(dto.getDireccion());
        s.setCiudad(dto.getCiudad());
        return s;
    }
}
