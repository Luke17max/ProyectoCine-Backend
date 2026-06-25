package com.cine.ms_notificaciones.controller;

import com.cine.ms_notificaciones.dto.NotificacionDTO;
import com.cine.ms_notificaciones.service.INotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final INotificacionService service;

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> listar() {
        log.info("REST: Petición GET a /api/notificaciones");
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> obtener(@PathVariable Long id) {
        log.info("REST: Petición GET a /api/notificaciones/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<List<NotificacionDTO>> obtenerPorReserva(@PathVariable Long reservaId) {
        log.info("REST: Petición GET a /api/notificaciones/reserva/{}", reservaId);
        return ResponseEntity.ok(service.buscarPorReserva(reservaId));
    }

    @PostMapping
    public ResponseEntity<NotificacionDTO> crear(@Valid @RequestBody NotificacionDTO dto) {
        log.info("REST: Petición POST a /api/notificaciones");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("REST: Petición DELETE a /api/notificaciones/{}", id);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}