package com.cine.ms_peliculas.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.cine.ms_peliculas.dto.PeliculaDTO;
import com.cine.ms_peliculas.model.Pelicula;
import com.cine.ms_peliculas.repository.PeliculaRepository;
import com.cine.ms_peliculas.service.IPeliculaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeliculaServiceImpl implements IPeliculaService {
    private final PeliculaRepository repository;

    @Override
    public List<PeliculaDTO> listarTodas() {
        log.info("Capa Servicio: Listando todas las películas");
        return repository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public PeliculaDTO buscarPorId(Long id) {
        log.info("Capa Servicio: Buscando película con ID: {}", id);
        return repository.findById(id).map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Película no encontrada"));
    }

    @Override
    public PeliculaDTO guardar(PeliculaDTO dto) {
        log.info("Capa Servicio: Guardando nueva película: {}", dto.getTitulo());
        Pelicula p = mapToEntity(dto);
        return mapToDTO(repository.save(p));
    }

    @Override
    public PeliculaDTO actualizar(Long id, PeliculaDTO dto) {
        log.info("Capa Servicio: Actualizando película ID: {}", id);
        Pelicula p = repository.findById(id).orElseThrow(() -> new RuntimeException("No existe"));
        p.setTitulo(dto.getTitulo());
        p.setGenero(dto.getGenero());
        p.setDuracion(dto.getDuracion());
        p.setClasificacion(dto.getClasificacion());
        return mapToDTO(repository.save(p));
    }

    @Override
    public void eliminar(Long id) {
        log.info("Capa Servicio: Eliminando película ID: {}", id);
        repository.deleteById(id);
    }

    private PeliculaDTO mapToDTO(Pelicula p) {
        PeliculaDTO d = new PeliculaDTO();
        d.setId(p.getId()); d.setTitulo(p.getTitulo()); d.setGenero(p.getGenero());
        d.setDuracion(p.getDuracion()); d.setClasificacion(p.getClasificacion());
        return d;
    }

    private Pelicula mapToEntity(PeliculaDTO d) {
        Pelicula p = new Pelicula();
        p.setTitulo(d.getTitulo()); p.setGenero(d.getGenero());
        p.setDuracion(d.getDuracion()); p.setClasificacion(d.getClasificacion());
        return p;
    }
}
