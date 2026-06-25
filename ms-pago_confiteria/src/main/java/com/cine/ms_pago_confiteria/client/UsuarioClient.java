package com.cine.ms_pago_confiteria.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-usuarios")
public interface UsuarioClient {
    @GetMapping("/api/usuarios/{id}")
    Object obtenerUsuario(@PathVariable("id") Long id);

}
