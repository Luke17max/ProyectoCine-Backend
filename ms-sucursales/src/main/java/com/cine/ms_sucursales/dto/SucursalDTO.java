package com.cine.ms_sucursales.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SucursalDTO {
    private Long id;

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    private String nombre;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

}
