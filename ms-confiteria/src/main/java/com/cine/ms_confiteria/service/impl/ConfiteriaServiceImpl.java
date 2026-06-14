package com.cine.ms_confiteria.service.impl;
 
import com.cine.ms_confiteria.dto.ConfiteriaDTO;
import com.cine.ms_confiteria.model.Confiteria;
import com.cine.ms_confiteria.repository.ConfiteriaRepository;
import com.cine.ms_confiteria.service.IConfiteriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiteriaServiceImpl implements IConfiteriaService {
 
    private final ConfiteriaRepository repository;
 
    @Override
    public List<ConfiteriaDTO> listarTodos() {
        log.info("Capa Servicio: Listando catálogo completo de confitería");
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }
 
    @Override
    public ConfiteriaDTO buscarPorId(Long id) {
        log.info("Capa Servicio: Buscando Confiteria ID: {}", id);
        return repository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Confiteria no encontrado"));
    }
 
    @Override
    public List<ConfiteriaDTO> buscarPorCategoria(String categoria) {
        log.info("Capa Servicio: Buscando Confiterias de categoría: {}", categoria);
        return repository.findByCategoriaIgnoreCase(categoria).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
    }
 
    @Override
    public ConfiteriaDTO guardar(ConfiteriaDTO dto) {
        log.info("Capa Servicio: Registrando nuevo Confiteria '{}'", dto.getNombre());
        Confiteria Confiteria = mapToEntity(dto);
        return mapToDTO(repository.save(Confiteria));
    }
 
    @Override
    public ConfiteriaDTO actualizar(Long id, ConfiteriaDTO dto) {
        log.info("Capa Servicio: Actualizando Confiteria ID: {}", id);
        Confiteria Confiteria = repository.findById(id).orElseThrow(() -> new RuntimeException("Confiteria no encontrado"));
        Confiteria.setNombre(dto.getNombre());
        Confiteria.setPrecio(dto.getPrecio());
        Confiteria.setStock(dto.getStock());
        Confiteria.setCategoria(dto.getCategoria());
        return mapToDTO(repository.save(Confiteria));
    }
 
    @Override
    public void actualizarStock(Long id, Integer cantidad) {
        log.info("Capa Servicio: Modificando stock para Confiteria ID: {} por {} unidades", id, cantidad);
        Confiteria Confiteria = repository.findById(id).orElseThrow(() -> new RuntimeException("Confiteria no encontrado"));
        
        int nuevoStock = Confiteria.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente para realizar la operación");
        }
        
        Confiteria.setStock(nuevoStock);
        repository.save(Confiteria);
    }
 
    @Override
    public void eliminar(Long id) {
        log.info("Capa Servicio: Eliminando Confiteria ID: {}", id);
        repository.deleteById(id);
    }
 
    private ConfiteriaDTO mapToDTO(Confiteria s) {
        ConfiteriaDTO dto = new ConfiteriaDTO();
        dto.setId(s.getId()); dto.setNombre(s.getNombre());
        dto.setPrecio(s.getPrecio()); dto.setStock(s.getStock());
        dto.setCategoria(s.getCategoria());
        return dto;
    }
 
    private Confiteria mapToEntity(ConfiteriaDTO dto) {
        Confiteria s = new Confiteria();
        s.setNombre(dto.getNombre()); s.setPrecio(dto.getPrecio());
        s.setStock(dto.getStock()); s.setCategoria(dto.getCategoria());
        return s;
    }
}