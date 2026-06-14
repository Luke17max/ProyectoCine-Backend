package com.cine.ms_funciones.client;
 
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
 
@FeignClient(name = "ms-salas")
public interface SalaClient {
    @GetMapping("/api/salas/{id}")
    Object obtenerSala(@PathVariable("id") Long id);
}
