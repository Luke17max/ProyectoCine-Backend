package com.cine.ms_sucursales.controller;

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

import com.cine.ms_sucursales.dto.SucursalDTO;
import com.cine.ms_sucursales.service.ISucursalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {
    private final ISucursalService service;

    @GetMapping
    public ResponseEntity<List<SucursalDTO>> listar() {
        log.info("REST: Petición GET a /api/sucursales");
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalDTO> obtener(@PathVariable Long id) {
        log.info("REST: Petición GET a /api/sucursales/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<SucursalDTO>> obtenerPorCiudad(@PathVariable String ciudad) {
        log.info("REST: Petición GET a /api/sucursales/ciudad/{}", ciudad);
        return ResponseEntity.ok(service.buscarPorCiudad(ciudad));
    }

    @PostMapping
    public ResponseEntity<SucursalDTO> crear(@Valid @RequestBody SucursalDTO dto) {
        log.info("REST: Petición POST a /api/sucursales");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalDTO> actualizar(@PathVariable Long id, @Valid @RequestBody SucursalDTO dto) {
        log.info("REST: Petición PUT a /api/sucursales/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("REST: Petición DELETE a /api/sucursales/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
