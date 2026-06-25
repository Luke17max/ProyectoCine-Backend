package com.cine.ms_reservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="ms-funciones")
public interface FuncionClient {
    @GetMapping("/api/funciones/{id}")
    Object obtenerFuncion(@PathVariable("id") Long id);

}
