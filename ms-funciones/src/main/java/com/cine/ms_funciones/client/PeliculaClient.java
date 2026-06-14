package com.cine.ms_funciones.client;
 
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
 
@FeignClient(name = "ms-peliculas")
public interface PeliculaClient {
    @GetMapping("/api/peliculas/{id}")
    Object obtenerPelicula(@PathVariable("id") Long id);
}
