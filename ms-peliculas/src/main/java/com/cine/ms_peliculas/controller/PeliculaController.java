package com.cine.ms_peliculas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cine.ms_peliculas.dto.PeliculaDTO;
import com.cine.ms_peliculas.service.IPeliculaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/peliculas")
@RequiredArgsConstructor
public class PeliculaController {
    private final IPeliculaService service;

    @GetMapping
    public ResponseEntity<List<PeliculaDTO>> listar() {
        log.info("REST: Petición GET a /api/peliculas");
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PeliculaDTO> obtener(@PathVariable Long id) {
        log.info("REST: Petición GET a /api/peliculas/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PeliculaDTO> crear(@Valid @RequestBody PeliculaDTO dto) {
        log.info("REST: Petición POST a /api/peliculas");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PeliculaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PeliculaDTO dto) {
        log.info("REST: Petición PUT a /api/peliculas/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("REST: Petición DELETE a /api/peliculas/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
