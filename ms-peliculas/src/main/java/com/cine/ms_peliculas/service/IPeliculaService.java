package com.cine.ms_peliculas.service;

import java.util.List;

import com.cine.ms_peliculas.dto.PeliculaDTO;

public interface IPeliculaService {
    List<PeliculaDTO> listarTodas();
    PeliculaDTO buscarPorId(Long id);
    PeliculaDTO guardar(PeliculaDTO dto);
    PeliculaDTO actualizar(Long id, PeliculaDTO dto);
    void eliminar(Long id);
}
