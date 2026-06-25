package com.cine.ms_funciones.dto;
 
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
@Data
public class FuncionDTO {
    private Long id;
 
    @NotNull(message = "La fecha y hora son obligatorias")
    @Future(message = "La función debe programarse para el futuro")
    private LocalDateTime fechaHora;
 
    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precioBase;
 
    @NotNull(message = "El ID de la película es obligatorio")
    private Long peliculaId;
 
    @NotNull(message = "El ID de la sala es obligatorio")
    private Long salaId;
}
