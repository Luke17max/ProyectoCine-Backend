package com.cine.ms_salas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-sucursales")
public interface SucursalClient {
    // Define la ruta exacta del endpoint que queremos consumir
    @GetMapping("/api/sucursales/{id}")
    Object obtenerSucursal(@PathVariable("id") Long id);

}
