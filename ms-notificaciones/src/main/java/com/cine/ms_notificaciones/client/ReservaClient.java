package com.cine.ms_notificaciones.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-reservas")
public interface ReservaClient {
    @GetMapping("/api/reservas/{id}")
    Object obtenerReserva(@PathVariable("id") Long id);
}