package com.cine.ms_salas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalaDTO {
    private Long id;

    @NotBlank(message = "El nombre de la sala es obligatorio")
    private String nombre;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 10, message = "La capacidad mínima es de 10 asientos")
    private Integer capacidad;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long sucursalId;

}
