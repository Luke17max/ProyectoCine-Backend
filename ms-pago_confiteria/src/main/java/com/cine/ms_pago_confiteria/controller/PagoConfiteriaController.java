package com.cine.ms_pago_confiteria.controller;

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

import com.cine.ms_pago_confiteria.dto.PagoConfiteriaDTO;
import com.cine.ms_pago_confiteria.service.IPagoConfiteriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/pagos-confiteria")
@RequiredArgsConstructor
public class PagoConfiteriaController {
    private final IPagoConfiteriaService service;

    @GetMapping
    public ResponseEntity<List<PagoConfiteriaDTO>> listar() {
        log.info("REST: Petición GET a /api/pagos-confiteria");
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoConfiteriaDTO> obtener(@PathVariable Long id) {
        log.info("REST: Petición GET a /api/pagos-confiteria/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoConfiteriaDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        log.info("REST: Petición GET a /api/pagos-confiteria/usuario/{}", usuarioId);
        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<PagoConfiteriaDTO> crear(@Valid @RequestBody PagoConfiteriaDTO dto) {
        log.info("REST: Petición POST a /api/pagos-confiteria");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoConfiteriaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PagoConfiteriaDTO dto) {
        log.info("REST: Petición PUT a /api/pagos-confiteria/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("REST: Petición DELETE a /api/pagos-confiteria/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
