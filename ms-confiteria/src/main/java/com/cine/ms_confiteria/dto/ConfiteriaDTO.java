package com.cine.ms_confiteria.dto;
 
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
 
@Data
public class ConfiteriaDTO {
    private Long id;
 
    @NotBlank(message = "El nombre del snack es obligatorio")
    private String nombre;
 
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
 
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
 
    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;
}