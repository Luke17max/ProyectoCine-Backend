package com.cine.ms_peliculas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PeliculaDTO {
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El género es obligatorio")
    private String genero;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser mayor a 0")
    private Integer duracion;

    @NotBlank(message = "La clasificación es obligatoria (TE, R, 14)")
    private String clasificacion;
}
