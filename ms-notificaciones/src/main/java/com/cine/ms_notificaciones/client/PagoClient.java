package com.cine.ms_notificaciones.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-pagos")
public interface PagoClient {
    @GetMapping("/api/pagos/{id}")
    Object obtenerPago(@PathVariable("id") Long id);
}
