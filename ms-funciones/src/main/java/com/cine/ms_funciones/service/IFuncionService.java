package com.cine.ms_funciones.service;
 
import com.cine.ms_funciones.dto.FuncionDTO;
import java.util.List;
 
public interface IFuncionService {
    List<FuncionDTO> listarTodas();
    FuncionDTO buscarPorId(Long id);
    List<FuncionDTO> buscarPorPelicula(Long peliculaId);
    FuncionDTO guardar(FuncionDTO dto);
    FuncionDTO actualizar(Long id, FuncionDTO dto);
    void eliminar(Long id);
}
