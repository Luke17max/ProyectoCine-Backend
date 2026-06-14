package com.cine.ms_confiteria.service;
 
import com.cine.ms_confiteria.dto.ConfiteriaDTO;
import java.util.List;
 
public interface IConfiteriaService {
    List<ConfiteriaDTO> listarTodos();
    ConfiteriaDTO buscarPorId(Long id);
    List<ConfiteriaDTO> buscarPorCategoria(String categoria);
    ConfiteriaDTO guardar(ConfiteriaDTO dto);
    ConfiteriaDTO actualizar(Long id, ConfiteriaDTO dto);
    void actualizarStock(Long id, Integer cantidad);
    void eliminar(Long id);
}

