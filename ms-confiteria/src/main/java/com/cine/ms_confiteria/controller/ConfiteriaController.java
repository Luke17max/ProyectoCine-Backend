package com.cine.ms_confiteria.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cine.ms_confiteria.dto.ConfiteriaDTO;
import com.cine.ms_confiteria.service.IConfiteriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/confiteria")
@RequiredArgsConstructor
public class ConfiteriaController {

    private final IConfiteriaService service;

    @GetMapping
    public ResponseEntity<List<ConfiteriaDTO>> listar() {
        log.info("REST: Petición GET a /api/confiteria");
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfiteriaDTO> obtener(@PathVariable Long id) {
        log.info("REST: Petición GET a /api/confiteria/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<ConfiteriaDTO>> obtenerPorCategoria(@PathVariable String categoria) {
        log.info("REST: Petición GET a /api/confiteria/categoria/{}", categoria);
        return ResponseEntity.ok(service.buscarPorCategoria(categoria));
    }

    @PostMapping
    public ResponseEntity<ConfiteriaDTO> crear(@Valid @RequestBody ConfiteriaDTO dto) {
        log.info("REST: Petición POST a /api/confiteria");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfiteriaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ConfiteriaDTO dto) {
        log.info("REST: Petición PUT a /api/confiteria/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    // Endpoint crucial para cuando ms-pago_confiteria procese una venta
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> modificarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        log.info("REST: Petición PATCH a /api/confiteria/{}/stock?cantidad={}", id, cantidad);
        service.actualizarStock(id, cantidad);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("REST: Petición DELETE a /api/confiteria/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}