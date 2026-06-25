package com.cine.ms_funciones.service.impl;
 
import com.cine.ms_funciones.client.PeliculaClient;
import com.cine.ms_funciones.client.SalaClient;
import com.cine.ms_funciones.dto.FuncionDTO;
import com.cine.ms_funciones.model.Funcion;
import com.cine.ms_funciones.repository.FuncionRepository;
import com.cine.ms_funciones.service.IFuncionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Slf4j
@Service
@RequiredArgsConstructor
public class FuncionServiceImpl implements IFuncionService {
 
    private final FuncionRepository repository;
    private final PeliculaClient peliculaClient;
    private final SalaClient salaClient;
 
    @Override
    public List<FuncionDTO> listarTodas() {
        log.info("Capa Servicio: Listando todas las funciones");
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }
 
    @Override
    public FuncionDTO buscarPorId(Long id) {
        log.info("Capa Servicio: Buscando función ID: {}", id);
        return repository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Función no encontrada"));
    }
 
    @Override
    public List<FuncionDTO> buscarPorPelicula(Long peliculaId) {
        log.info("Capa Servicio: Buscando funciones para la película ID: {}", peliculaId);
        return repository.findByPeliculaId(peliculaId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }
 
    @Override
    public FuncionDTO guardar(FuncionDTO dto) {
        log.info("Capa Servicio: Guardando nueva función, validando dependencias cruzadas");
        validarPelicula(dto.getPeliculaId());
        validarSala(dto.getSalaId());
        
        Funcion funcion = mapToEntity(dto);
        return mapToDTO(repository.save(funcion));
    }
 
    @Override
    public FuncionDTO actualizar(Long id, FuncionDTO dto) {
        log.info("Capa Servicio: Actualizando función ID: {}", id);
        Funcion funcion = repository.findById(id).orElseThrow(() -> new RuntimeException("Función no encontrada"));
        
        if (!funcion.getPeliculaId().equals(dto.getPeliculaId())) validarPelicula(dto.getPeliculaId());
        if (!funcion.getSalaId().equals(dto.getSalaId())) validarSala(dto.getSalaId());
 
        funcion.setFechaHora(dto.getFechaHora());
        funcion.setPrecioBase(dto.getPrecioBase());
        funcion.setPeliculaId(dto.getPeliculaId());
        funcion.setSalaId(dto.getSalaId());
        
        return mapToDTO(repository.save(funcion));
    }
 
    @Override
    public void eliminar(Long id) {
        log.info("Capa Servicio: Eliminando función ID: {}", id);
        repository.deleteById(id);
    }
 
    private void validarPelicula(Long id) {
        try {
            peliculaClient.obtenerPelicula(id);
            log.info("Validación exitosa: Película ID {} existe", id);
        } catch (FeignException.NotFound e) {
            log.error("Fallo de integridad: Película ID {} no encontrada", id);
            throw new RuntimeException("Error: La película con ID " + id + " no existe.");
        }
    }
 
    private void validarSala(Long id) {
        try {
            salaClient.obtenerSala(id);
            log.info("Validación exitosa: Sala ID {} existe", id);
        } catch (FeignException.NotFound e) {
            log.error("Fallo de integridad: Sala ID {} no encontrada", id);
            throw new RuntimeException("Error: La sala con ID " + id + " no existe.");
        }
    }
 
    private FuncionDTO mapToDTO(Funcion f) {
        FuncionDTO dto = new FuncionDTO();
        dto.setId(f.getId()); dto.setFechaHora(f.getFechaHora());
        dto.setPrecioBase(f.getPrecioBase()); dto.setPeliculaId(f.getPeliculaId());
        dto.setSalaId(f.getSalaId());
        return dto;
    }
 
    private Funcion mapToEntity(FuncionDTO dto) {
        Funcion f = new Funcion();
        f.setFechaHora(dto.getFechaHora()); f.setPrecioBase(dto.getPrecioBase());
        f.setPeliculaId(dto.getPeliculaId()); f.setSalaId(dto.getSalaId());
        return f;
    }
}
