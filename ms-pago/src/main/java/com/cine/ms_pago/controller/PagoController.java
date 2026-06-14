package com.cine.ms_pago.controller;

import com.cine.ms_pago.dto.PagoDTO;
import com.cine.ms_pago.service.IPagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final IPagoService service;

    @GetMapping
    public ResponseEntity<List<PagoDTO>> listar() {
        log.info("REST: Petición GET a /api/pagos");
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtener(@PathVariable Long id) {
        log.info("REST: Petición GET a /api/pagos/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<PagoDTO> obtenerPorReserva(@PathVariable Long reservaId) {
        log.info("REST: Petición GET a /api/pagos/reserva/{}", reservaId);
        return ResponseEntity.ok(service.buscarPorReserva(reservaId));
    }

    @PostMapping
    public ResponseEntity<PagoDTO> crear(@Valid @RequestBody PagoDTO dto) {
        log.info("REST: Petición POST a /api/pagos");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PagoDTO dto) {
        log.info("REST: Petición PUT a /api/pagos/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("REST: Petición DELETE a /api/pagos/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
