package com.cine.ms_reservas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservaDTO {
    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID de la función es obligatorio")
    private Long funcionId;

    @NotNull(message = "La cantidad de asientos es obligatoria")
    @Min(value = 1, message = "Debe reservar al menos 1 asiento")
    private Integer cantidadAsientos;

    // El estado no requiere validación de entrada porque lo controla el Backend
    private String estado;
}
