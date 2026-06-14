package com.cine.ms_pago_confiteria.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoConfiteriaDTO {
    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser de al menos 1")
    private Integer cantidad;

    @NotNull(message = "El total pagado es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El total pagado debe ser mayor a 0")
    private BigDecimal totalPagado;

    private LocalDateTime fechaCompra;

}
