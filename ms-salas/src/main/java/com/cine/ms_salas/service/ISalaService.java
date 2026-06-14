package com.cine.ms_salas.service;

import java.util.List;

import com.cine.ms_salas.dto.SalaDTO;

public interface ISalaService {
    List<SalaDTO> listarTodas();
    SalaDTO buscarPorId(Long id);
    List<SalaDTO> buscarPorSucursal(Long sucursalId);
    SalaDTO guardar(SalaDTO dto);
    SalaDTO actualizar(Long id, SalaDTO dto);
    void eliminar(Long id);

}
