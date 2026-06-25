package com.cine.ms_pago_confiteria.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-confiteria")
public interface ProductoClient {
    @GetMapping("/api/confiteria/{id}")
    Object obtenerProducto(@PathVariable("id") Long id);


}
