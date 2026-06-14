package com.cine.ms_sucursales.service;

import java.util.List;

import com.cine.ms_sucursales.dto.SucursalDTO;

public interface ISucursalService {
    List<SucursalDTO> listarTodas();
    SucursalDTO buscarPorId(Long id);
    List<SucursalDTO> buscarPorCiudad(String ciudad);
    SucursalDTO guardar(SucursalDTO dto);
    SucursalDTO actualizar(Long id, SucursalDTO dto);
    void eliminar(Long id);
}
